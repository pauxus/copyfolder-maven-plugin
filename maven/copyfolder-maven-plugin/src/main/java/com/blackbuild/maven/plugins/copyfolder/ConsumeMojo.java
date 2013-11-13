/* _____________________________________________________________________________
 *
 * Project: ACM
 * File:    CopyFolderMojo.java
 * _____________________________________________________________________________
 *
 * Description:       See class comment
 * _____________________________________________________________________________
 *
 * Copyright: (C) DAIMLER 2013, all rights reserved
 * _____________________________________________________________________________
 */
package com.blackbuild.maven.plugins.copyfolder;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.impl.ArtifactResolver;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.util.artifact.DefaultArtifact;

/**
 * Consumes files provided by the provided goal in a different project.
 */
@Mojo(name = "consume", aggregator = false, defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.TEST, threadSafe = true)
public class ConsumeMojo extends AbstractMojo {

    @Component
    private MavenProject project;

    @Component
    private MavenSession session;
    
    @Component
    private RepositorySystem repoSystem;
    
    @Parameter(readonly=true, defaultValue="${repositorySystemSession}")
    private RepositorySystemSession repoSession;
    
    @Parameter(readonly=true, defaultValue="${project.remoteProjectRepositories}")
    private List<RemoteRepository> remoteRepos;
    
    
    /**
     * Project to consume files from. format: groupId:artifactId. The consumed project must be a dependency of this project.
     */
    @Parameter(required = true)
    private String source;

    /**
     * Classifier of the resource to consume.
     */
    @Parameter(required = true)
    private String classifier;
    
    /**
     * Where should the files be copied to.
     */
    @Parameter(defaultValue="${project.build.directory}/consumer")
    private String targetFolder;
    
    @Parameter(defaultValue = "${project.build.directory}/copyfolders-markers")
    private File markersDir;

    public void execute() throws MojoExecutionException, MojoFailureException {
        
        Dependency baseArtifact = findMatchingArtifact();

        // possible scenarios:
        // LifecyclePhase is lower than compile and no IDE build: Artifact would be 
        
        
        Artifact provided = new DefaultArtifact(baseArtifact.getGroupId(), baseArtifact.getArtifactId(), classifier, "jar", baseArtifact.getVersion());
        
        ArtifactResult result;
        try {
            result = repoSystem.resolveArtifact(repoSession, new ArtifactRequest(provided, remoteRepos, null));
        } catch (ArtifactResolutionException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
        
        getLog().info(result.getArtifact().getFile().toString());
        
//        for (File source : sources) {
//            this.project.addCompileSourceRoot(source.getAbsolutePath());
//            if (getLog().isInfoEnabled()) {
//                getLog().info("Source directory: " + source + " added.");
//            }
//        }

    }
    
    private Dependency findMatchingArtifact() throws MojoExecutionException {
        String groupId = null;
        String artifactId;
        
        int index = source.indexOf(":");
        
        if (index < 0) {
            artifactId = source;
        } else {
            groupId = source.substring(0, index);
            artifactId = source.substring(index + 1);
        }
        
        for (Dependency dependency : project.getDependencies()) {
            if (dependency.getArtifactId().equals(artifactId) && (groupId == null || dependency.getGroupId().equals(groupId))) {
                return dependency;
            }
        }
        
        getLog().error("Could not find matching dependency for '" + source + "'");
        throw new MojoExecutionException("Could not find matching dependency for '" + source + "'");
    }

    private MavenProject findSourceProject() {
//        for (MavenProject next : session.getProjects()) {
//            if (next.)
//        }
        return null;
    }
}
