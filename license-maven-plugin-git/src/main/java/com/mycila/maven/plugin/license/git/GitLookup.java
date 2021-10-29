/*
 * Copyright (C) 2008-2022 Mycila (mathieu.carbou@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mycila.maven.plugin.license.git;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffConfig;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.FollowFilter;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.MaxCountRevFilter;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.filter.AndTreeFilter;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

/**
 * A jGit library wrapper to query the date of the last commit.
 *
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 */
public class GitLookup implements Closeable {

  public static final TimeZone DEFAULT_ZONE = TimeZone.getTimeZone("GMT");

  public static final String MAX_COMMITS_LOOKUP_KEY = "license.git.maxCommitsLookup";
  // keep for compatibility
  private static final String COPYRIGHT_LAST_YEAR_MAX_COMMITS_LOOKUP_KEY = "license.git.copyrightLastYearMaxCommitsLookup";
  public static final String COPYRIGHT_LAST_YEAR_SOURCE_KEY = "license.git.copyrightLastYearSource";
  public static final String COPYRIGHT_LAST_YEAR_TIME_ZONE_KEY = "license.git.copyrightLastYearTimeZone";
  public static final String COMMITS_TO_IGNORE_KEY = "license.git.commitsToIgnore";

  public enum DateSource {
    AUTHOR, COMMITER
  }

  private final int checkCommitsCount;
  private final DateSource dateSource;
  private final GitPathResolver pathResolver;
  private final Repository repository;
  private final TimeZone timeZone;
  private final boolean shallow;
  private final Set<ObjectId> commitsToIgnore;

  /**
   * Lazily initializes #gitLookup assuming that all subsequent calls to this method will be related
   * to the same git repository.
   */
  public static GitLookup create(File file, Map<String, String> props) {
    String dateSourceString = props.get(COPYRIGHT_LAST_YEAR_SOURCE_KEY);
    if (dateSourceString == null) {
      dateSourceString = GitLookup.DateSource.AUTHOR.name();
    }
    GitLookup.DateSource dateSource = GitLookup.DateSource.valueOf(
        dateSourceString.toUpperCase(Locale.US));
    String checkCommitsCountString = props.get(MAX_COMMITS_LOOKUP_KEY);
    // Backwards compatibility
    if (checkCommitsCountString == null) {
      checkCommitsCountString = props.get(COPYRIGHT_LAST_YEAR_MAX_COMMITS_LOOKUP_KEY);
    }
    int checkCommitsCount = Integer.MAX_VALUE;
    if (checkCommitsCountString != null) {
      checkCommitsCountString = checkCommitsCountString.trim();
      checkCommitsCount = Integer.parseInt(checkCommitsCountString);
    }
    String commitsToIgnoreString = props.get(COMMITS_TO_IGNORE_KEY);
    Set<ObjectId> commitsToIgnore = Collections.emptySet();
    if (commitsToIgnoreString != null) {
      commitsToIgnoreString = commitsToIgnoreString.trim();
      commitsToIgnore = Arrays.stream(commitsToIgnoreString.split(","))
              .map(String::trim)
              .map(ObjectId::fromString)
              .collect(Collectors.toSet());
    }
    final TimeZone timeZone;
    String tzString = props.get(COPYRIGHT_LAST_YEAR_TIME_ZONE_KEY);
    switch (dateSource) {
      case COMMITER:
        timeZone = tzString == null ? GitLookup.DEFAULT_ZONE : TimeZone.getTimeZone(tzString);
        break;
      case AUTHOR:
        if (tzString != null) {
          throw new RuntimeException(COPYRIGHT_LAST_YEAR_TIME_ZONE_KEY + " must not be set with "
              + COPYRIGHT_LAST_YEAR_SOURCE_KEY + " = " + GitLookup.DateSource.AUTHOR.name()
              + " because git author name already contains time zone information.");
        }
        timeZone = null;
        break;
      default:
        throw new IllegalStateException(
            "Unexpected " + GitLookup.DateSource.class.getName() + " " + dateSource);
    }
    return new GitLookup(file, dateSource, timeZone, checkCommitsCount, commitsToIgnore);
  }

  /**
   * Creates a new {@link GitLookup} for a repository that is detected from the supplied {@code
   * anyFile}.
   * <p>
   * Note on time zones:
   *
   * @param anyFile           - any path from the working tree of the git repository to consider in
   *                          all subsequent calls to {@link #getYearOfLastChange(File)}
   * @param dateSource        where to read the commit dates from - committer date or author date
   * @param timeZone          the time zone if {@code dateSource} is {@link DateSource#COMMITER};
   *                          otherwise must be {@code null}.
   * @param checkCommitsCount the number of historical commits, per file, to check
   * @param commitsToIgnore   the commits to ignore while inspecting the history for {@code anyFile}
   * @throws IOException
   */
  private GitLookup(File anyFile, DateSource dateSource, TimeZone timeZone, int checkCommitsCount, Set<ObjectId> commitsToIgnore) {
    try {
      this.repository = new FileRepositoryBuilder().findGitDir(anyFile).build();
      /* A workaround for  https://bugs.eclipse.org/bugs/show_bug.cgi?id=457961 */
      // Also contains contents of .git/shallow and can detect shallow repo
      // the line below reads and caches the entries in the FileObjectDatabase of the repository to
      // avoid concurrent modifications during RevWalk
      // Closing the repository will close the FileObjectDatabase.
      // Here the newReader() is a WindowCursor which delegates the getShallowCommits() to the FileObjectDatabase.
      this.shallow = !this.repository.getObjectDatabase().newReader().getShallowCommits().isEmpty();

      this.pathResolver = new GitPathResolver(repository.getWorkTree().getAbsolutePath());
      this.dateSource = dateSource;
      switch (dateSource) {
        case COMMITER:
          this.timeZone = timeZone == null ? DEFAULT_ZONE : timeZone;
          break;
        case AUTHOR:
          if (timeZone != null) {
            throw new IllegalArgumentException(
                "Time zone must be null with dateSource " + DateSource.AUTHOR.name()
                    + " because git author name already contains time zone information.");
          }
          this.timeZone = null;
          break;
        default:
          throw new IllegalStateException(
              "Unexpected " + DateSource.class.getName() + " " + dateSource);
      }
      this.checkCommitsCount = checkCommitsCount;
      this.commitsToIgnore = commitsToIgnore;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /**
   * Returns the year of the last change of the given {@code file} based on the history of the present git branch. The
   * year is taken either from the committer date or from the author identity depending on how {@link #dateSource} was
   * initialized.
   * <p>
   * See also the note on time zones in {@link #GitLookup(File, DateSource, TimeZone, int, Set)}.
   *
   * @param file for which the year should be retrieved
   * @return year of last modification of the file
   * @throws IOException     if unable to read the file
   * @throws GitAPIException if unable to process the git history
   */
  int getYearOfLastChange(File file) throws GitAPIException, IOException {
    String repoRelativePath = pathResolver.relativize(file);

    if (isFileModifiedOrUnstaged(repoRelativePath)) {
      return getCurrentYear();
    }

    int commitYear = 0;
    RevWalk walk = getGitRevWalk(repoRelativePath, false);
    for (RevCommit commit : walk) {
      if (commitsToIgnore.contains(commit.getId())) {
        continue;
      }
      int y = getYearFromCommit(commit);
      if (y > commitYear) {
        commitYear = y;
      }
    }
    walk.dispose();
    return commitYear;
  }

  /**
   * Returns the year of creation for the given {@code file) based on the history of the present git branch. The
   * year is taken either from the committer date or from the author identity depending on how {@link #dateSource} was
   * initialized.
   *
   * @param file for which the year should be retrieved
   * @return year of creation of the file
   * @throws IOException     if unable to read the file
   * @throws GitAPIException if unable to process the git history
   */
  int getYearOfCreation(File file) throws IOException, GitAPIException {
    String repoRelativePath = pathResolver.relativize(file);

    int commitYear = 0;
    RevWalk walk = getGitRevWalk(repoRelativePath, true);
    Iterator<RevCommit> iterator = walk.iterator();
    if (iterator.hasNext()) {
      RevCommit commit = iterator.next();
      commitYear = getYearFromCommit(commit);
    }
    walk.dispose();

    // If we couldn't find a creation year from Git assume newly created file
    if (commitYear == 0) {
      return getCurrentYear();
    }

    return commitYear;
  }

  String getAuthorNameOfCreation(File file) throws IOException {
    String repoRelativePath = pathResolver.relativize(file);
    String authorName = "";
    RevWalk walk = getGitRevWalk(repoRelativePath, true);
    Iterator<RevCommit> iterator = walk.iterator();
    if (iterator.hasNext()) {
      RevCommit commit = iterator.next();
      authorName = getAuthorNameFromCommit(commit);
    }
    walk.dispose();
    return authorName;
  }

  String getAuthorEmailOfCreation(File file) throws IOException {
    String repoRelativePath = pathResolver.relativize(file);
    String authorEmail = "";
    RevWalk walk = getGitRevWalk(repoRelativePath, true);
    Iterator<RevCommit> iterator = walk.iterator();
    if (iterator.hasNext()) {
      RevCommit commit = iterator.next();
      authorEmail = getAuthorEmailFromCommit(commit);
    }
    walk.dispose();
    return authorEmail;
  }

  boolean isShallowRepository() {
    return this.shallow;
  }

  private boolean isFileModifiedOrUnstaged(String repoRelativePath) throws GitAPIException {
    @SuppressWarnings("resource")
    Status status = new Git(repository).status().addPath(repoRelativePath).call();
    return !status.isClean();
  }

  private RevWalk getGitRevWalk(String repoRelativePath, boolean oldestCommitsFirst) throws IOException {
    DiffConfig diffConfig = repository.getConfig().get(DiffConfig.KEY);

    RevWalk walk = new RevWalk(repository);
    walk.markStart(walk.parseCommit(repository.resolve(Constants.HEAD)));
    walk.setTreeFilter(AndTreeFilter.create(Arrays.asList(
        PathFilter.create(repoRelativePath),
        FollowFilter.create(repoRelativePath, diffConfig), // Allows us to follow files as they move or are renamed
        TreeFilter.ANY_DIFF)
    ));
    walk.setRevFilter(MaxCountRevFilter.create(checkCommitsCount));
    walk.setRetainBody(false);
    if (oldestCommitsFirst) {
      walk.sort(RevSort.REVERSE);
    }

    return walk;
  }

  private int getCurrentYear() {
    return toYear(System.currentTimeMillis(), timeZone != null ? timeZone : DEFAULT_ZONE);
  }

  private int getYearFromCommit(RevCommit commit) {
    switch (dateSource) {
      case COMMITER:
        int epochSeconds = commit.getCommitTime();
        return toYear(epochSeconds * 1000L, timeZone);
      case AUTHOR:
        PersonIdent id = commit.getAuthorIdent();
        Date date = id.getWhen();
        return toYear(date.getTime(), id.getTimeZone());
      default:
        throw new IllegalStateException("Unexpected " + DateSource.class.getName() + " " + dateSource);
    }
  }

  private static int toYear(long epochMilliseconds, TimeZone timeZone) {
    Calendar result = Calendar.getInstance(timeZone);
    result.setTimeInMillis(epochMilliseconds);
    return result.get(Calendar.YEAR);
  }

  private String getAuthorNameFromCommit(RevCommit commit) {
    PersonIdent id = commit.getAuthorIdent();
    return id.getName();
  }

  private String getAuthorEmailFromCommit(RevCommit commit) {
    PersonIdent id = commit.getAuthorIdent();
    return id.getEmailAddress();
  }

  @Override
  public void close() {
    repository.close();
  }
}