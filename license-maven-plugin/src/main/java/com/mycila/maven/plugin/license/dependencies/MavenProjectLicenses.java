/*
 * Copyright (C) 2008-2022 Mycila (mathieu.carbou@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mycila.maven.plugin.license.dependencies;

import org.apache.maven.Maven;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.resolver.filter.CumulativeScopeArtifactFilter;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.License;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.DefaultProjectBuilder;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.apache.maven.shared.dependency.graph.internal.Maven31DependencyGraphBuilder;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Helper class for building Artifact/License mappings from a maven project
 * (multi module or single).
 *
 * @author Royce Remer
 */
public class MavenProjectLicenses implements LicenseMap, LicenseMessage {

  private MavenSession session;
  private Set<MavenProject> projects;
  private DependencyGraphBuilder graph;
  private ProjectBuilder projectBuilder;
  private ArtifactFilter filter;
  private Log log;

  /**
   * @param projects       the Set of {@link MavenProject} to scan
   * @param graph          the {@link DependencyGraphBuilder} implementation
   * @param projectBuilder the maven {@link ProjectBuilder} implementation
   * @param log            the log to sync to
   */
  MavenProjectLicenses(final MavenSession session, final Set<MavenProject> projects, final DependencyGraphBuilder graph,
                              final ProjectBuilder projectBuilder,
                              final ArtifactFilter filter, final Log log) {
    this.setSession(session);
    this.setProjects(projects);
    this.setGraph(graph);
    this.setFilter(filter);
    this.setProjectBuilder(projectBuilder);
    this.setLog(log);

    log.info(String.format("%s %s", INFO_LICENSE_IMPL, this.getClass()));
  }

  /**
   * @param session        the current {@link MavenSession}
   * @param graph          the {@link DependencyGraphBuilder} implementation
   * @param projectBuilder the maven {@link ProjectBuilder} implementation
   */
  public MavenProjectLicenses(final MavenSession session, MavenProject project, final DependencyGraphBuilder graph,
                              final ProjectBuilder projectBuilder, final List<String> scopes, final Log log) {
    this(session, Collections.singleton(project), graph, projectBuilder, new CumulativeScopeArtifactFilter(scopes), log);
  }

  /**
   * Return a set of licenses attributed to a single artifact.
   */
  protected Set<License> getLicensesFromArtifact(final Artifact artifact) {
    Set<License> licenses = new HashSet<>();
    try {
      MavenProject project = getProjectBuilder().build(artifact, getBuildingRequest()).getProject();
      licenses.addAll(project.getLicenses());
    } catch (ProjectBuildingException ex) {
      getLog().warn(String.format("Could not get project from dependency's artifact: %s", artifact.getFile()));
    }

    return licenses;
  }

  /**
   * Get mapping of Licenses to a set of artifacts presenting that license.
   *
   * @param dependencies Set to collate License entries from
   * @return the same artifacts passed in, keyed by license
   */
  protected Map<License, Set<Artifact>> getLicenseMapFromArtifacts(final Set<Artifact> dependencies) {
    final ConcurrentMap<License, Set<Artifact>> map = new ConcurrentHashMap<>();

    // license:artifact is a many-to-many relationship.
    // Each artifact may have several licenses.
    // Each artifact may appear multiple times in the map.
    dependencies.parallelStream().forEach(artifact -> getLicensesFromArtifact(artifact).forEach(license -> {
      map.putIfAbsent(license, new HashSet<>());
      Set<Artifact> artifacts = map.get(license);
      artifacts.add(artifact);
      map.put(license, artifacts);
    }));

    return map;
  }

  @Override
  public Map<License, Set<Artifact>> getLicenseMap() {
    return getLicenseMapFromArtifacts(getDependencies());
  }

  /**
   * Return the Set of all direct and transitive Artifact dependencies.
   */
  private Set<Artifact> getDependencies() {
    final Set<Artifact> artifacts = new HashSet<>();
    final Set<DependencyNode> dependencies = new HashSet<>();

    // build the set of maven dependencies for each module in the reactor (might
    // only be the single one) and all its transitives
    getLog().debug(String.format("Building dependency graphs for %d projects", getProjects().size()));
    getProjects().parallelStream().forEach(project -> {
      try {
        DefaultProjectBuildingRequest buildingRequest = new DefaultProjectBuildingRequest(getBuildingRequest());
        buildingRequest.setProject(project);
        dependencies.addAll(getGraph().buildDependencyGraph(buildingRequest, getFilter()).getChildren());
      } catch (DependencyGraphBuilderException ex) {
        getLog().warn(
            String.format("Could not get children from project %s, it's dependencies will not be checked!",
                project.getId()));
      }
    });

    // build the complete set of direct+transitive dependent artifacts in all
    // modules in the reactor
    dependencies.parallelStream().forEach(d -> artifacts.add(d.getArtifact()));
    getLog().info(String.format("%s: %d", INFO_DEPS_DISCOVERED, dependencies.size()));

    return artifacts;

    // tempting, but does not resolve dependencies after the scope in which this
    // plugin is invoked
    // return project.getArtifacts();
  }

  protected Set<MavenProject> getProjects() {
    return projects;
  }

  private void setSession(MavenSession session) {
    this.session = session;
  }

  protected void setProjects(final Set<MavenProject> projects) {
    this.projects = Optional.ofNullable(projects).orElse(new HashSet<>());
  }

  private DependencyGraphBuilder getGraph() {
    return graph;
  }

  private void setGraph(DependencyGraphBuilder graph) {
    this.graph = Optional.ofNullable(graph).orElse(new Maven31DependencyGraphBuilder());
  }

  private ProjectBuilder getProjectBuilder() {
    return projectBuilder;
  }

  private void setProjectBuilder(ProjectBuilder projectBuilder) {
    this.projectBuilder = Optional.ofNullable(projectBuilder).orElse(new DefaultProjectBuilder());
  }

  private ArtifactFilter getFilter() {
    return filter;
  }

  private void setFilter(ArtifactFilter filter) {
    this.filter = filter;
  }

  private Log getLog() {
    return log;
  }

  private void setLog(Log log) {
    this.log = log;
  }

  private ProjectBuildingRequest getBuildingRequest() {
    // There's an odd comment on the below used method, pretty sure it is not as stable as one likes it to be
    return session.getProjectBuildingRequest();
  }
}
