/**
 * Copyright (C) 2008 Mycila (mathieu.carbou@gmail.com)
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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffConfig;
import org.eclipse.jgit.lib.Constants;
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
public class GitLookup {
    public static final TimeZone DEFAULT_ZONE = TimeZone.getTimeZone("GMT");
    public static final int DEFAULT_COMMITS_COUNT = 10;

    public enum DateSource {
        AUTHOR, COMMITER
    }

    private final int checkCommitsCount;
    private final DateSource dateSource;
    private final GitPathResolver pathResolver;
    private final Repository repository;
    private final TimeZone timeZone;

    /**
     * Creates a new {@link GitLookup} for a repository that is detected from the supplied {@code anyFile}.
     * <p>
     * Note on time zones:
     *
     * @param anyFile
     *            - any path from the working tree of the git repository to consider in all subsequent calls to
     *            {@link #getYearOfLastChange(File)}
     * @param dateSource
     *            where to read the commit dates from - committer date or author date
     * @param timeZone
     *            the time zone if {@code dateSource} is {@link DateSource#COMMITER}; otherwise must be {@code null}.
     * @param checkCommitsCount
     * @throws IOException
     */
    public GitLookup(File anyFile, DateSource dateSource, TimeZone timeZone, int checkCommitsCount) throws IOException {
        super();
        this.repository = new FileRepositoryBuilder().findGitDir(anyFile).build();
        /* A workaround for  https://bugs.eclipse.org/bugs/show_bug.cgi?id=457961 */
        this.repository.getObjectDatabase().newReader().getShallowCommits();

        this.pathResolver = new GitPathResolver(repository.getWorkTree().getAbsolutePath());
        this.dateSource = dateSource;
        switch (dateSource) {
        case COMMITER:
            this.timeZone = timeZone == null ? DEFAULT_ZONE : timeZone;
            break;
        case AUTHOR:
            if (timeZone != null) {
                throw new IllegalArgumentException("Time zone must be null with dateSource " + DateSource.AUTHOR.name()
                        + " because git author name already contrains time zone information.");
            }
            this.timeZone = null;
            break;
        default:
            throw new IllegalStateException("Unexpected " + DateSource.class.getName() + " " + dateSource);
        }
        this.checkCommitsCount = checkCommitsCount;
    }

    /**
     * Returns the year of the last change of the given {@code file} based on the history of the present git branch. The
     * year is taken either from the committer date or from the author identity depending on how {@link #dateSource} was
     * initialized.
     * <p>
     * See also the note on time zones in {@link #GitLookup(File, DateSource, TimeZone, int)}.
     *
     * @param file for which the year should be retrieved
     * @return year of last modification of the file
     * @throws NoHeadException if unable to retrieve the head of the git history
     * @throws IOException if unable to read the file
     * @throws GitAPIException if unable to process the git history
     */
    int getYearOfLastChange(File file) throws NoHeadException, GitAPIException, IOException {
        String repoRelativePath = pathResolver.relativize(file);

        if (isFileModifiedOrUnstaged(repoRelativePath)) {
            return getCurrentYear();
        }

        int commitYear = 0;
        RevWalk walk = getGitRevWalk(repoRelativePath, false);
        for (RevCommit commit : walk) {
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
     * @throws IOException if unable to read the file
     * @throws GitAPIException if unable to process the git history
     */
    int getYearOfCreation(File file) throws IOException, GitAPIException {
        String repoRelativePath = pathResolver.relativize(file);

        if (isFileModifiedOrUnstaged(repoRelativePath)) {
            return getCurrentYear();
        }

        int commitYear = 0;
        RevWalk walk = getGitRevWalk(repoRelativePath, true);
        Iterator<RevCommit> iterator = walk.iterator();
        if (iterator.hasNext()) {
            RevCommit commit = iterator.next();
            commitYear = getYearFromCommit(commit);
        }
        walk.dispose();
        return commitYear;
    }

    private boolean isFileModifiedOrUnstaged(String repoRelativePath) throws GitAPIException {
        Status status = new Git(repository).status().addPath(repoRelativePath).call();
        return status.isClean();
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
}
