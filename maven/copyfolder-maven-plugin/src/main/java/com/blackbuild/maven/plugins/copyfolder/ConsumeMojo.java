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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.tools.ant.taskdefs.Expand;
import org.codehaus.plexus.util.xml.Xpp3Dom;
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
    
    @Component
    private MavenProjectHelper projectHelper;
    
    @Component
    private PluginDescriptor descriptor;
    
    @Parameter(readonly=true, defaultValue="${repositorySystemSession}")
    private RepositorySystemSession repoSession;
    
    @Parameter(readonly=true, defaultValue="${project.remoteProjectRepositories}")
    private List<RemoteRepository> remoteRepos;
    
    @Parameter(defaultValue="false")
    private boolean addAsSource; 
    
    @Parameter(defaultValue="false")
    private boolean addAsResource; 
        
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

        Artifact provided = new DefaultArtifact(baseArtifact.getGroupId(), baseArtifact.getArtifactId(), classifier, "jar", baseArtifact.getVersion());

        MavenProject reactorProject = findSourceProject(provided);
        
        File sourceFile = null;
        
        if (reactorProject != null) {
            // artifact from reactor
            sourceFile = findAttachedArtifactFile(reactorProject);
            
            if (sourceFile == null) {
                // this can happen, when the reactor is not build up to package
                sourceFile = new File(reactorProject.getBuild().getOutputDirectory());
            }
        }

        if (sourceFile == null) {
            try {
                sourceFile = repoSystem.resolveArtifact(repoSession, new ArtifactRequest(provided, remoteRepos, null)).getArtifact().getFile();
            } catch (ArtifactResolutionException e) {
                throw new MojoExecutionException(e.getMessage(), e);
            }
        }
        
        getLog().info("Using source from " + sourceFile);

        if (sourceFile.isFile()) {
            copyFromArtifact(sourceFile);
        } else {
            copyFromFolder(reactorProject);
        }
        
        if (addAsSource) {
            this.project.addCompileSourceRoot(targetFolder);
            getLog().info("Source directory: " + source + " added.");
        }
        if (addAsResource) {
            projectHelper.addResource(project, targetFolder, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
            getLog().info("Resource directory: " + source + " added.");
        }
        

    }
    

    private void copyFromFolder(MavenProject reactorProject) throws MojoExecutionException {
         for (PluginExecution execution : reactorProject.getPlugin(descriptor.getPluginLookupKey()).getExecutions()) {
             if (!execution.getGoals().contains("provide")) continue;
             
             Xpp3Dom configuration = (Xpp3Dom) execution.getConfiguration();
             
             if (configuration == null) continue;
             
             Resource target = findResourceWithClassifier(configuration.getChild("resources"));

             
             
             
             
         }
    }
    
    private Resource findResourceWithClassifier(Xpp3Dom resources) throws MojoExecutionException {
        for ( Xpp3Dom node : resources.getChildren() ) {
            Resource mapping = nodeToResource(node);
            if (mapping.getClassifier().equals(classifier)) return mapping;
        }
        
        throw new MojoExecutionException("No resource with classifier '" + classifier + "' found.");
    }
    
    private Resource nodeToResource(Xpp3Dom resource) {
        Resource result = new Resource();
        result.setFolder(new File(resource.getChild("folder").getValue()));
        if (resource.getChild("classifier") != null) result.setClassifier(resource.getChild("classifier").getValue());
        return result;
    }
    
    private void copyFromArtifact(File sourceFile) {
        Expand expand = new Expand();
        expand.setProject(AntHelper.createProject());
        expand.setTaskType("jar");
        expand.setTaskName("CONSUME");
        expand.setSrc(sourceFile);
        expand.setDest(new File(targetFolder));
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

    private MavenProject findSourceProject(Artifact artifact) {
        for (MavenProject project : session.getProjects()) {
            if (projectMatchesArtifact(project, artifact)) return project;
        }

        return null;
    }

    private boolean projectMatchesArtifact(MavenProject projectToTest, Artifact artifact) {
        return projectToTest.getGroupId().equals(artifact.getGroupId()) 
                && projectToTest.getArtifactId().equals(artifact.getArtifactId())
                && projectToTest.getVersion().equals(artifact.getVersion());
    }
    
    private File findAttachedArtifactFile(MavenProject reactorProject) {
        for (org.apache.maven.artifact.Artifact attached : reactorProject.getAttachedArtifacts()) {
            if (classifier.equals(attached.getClassifier())) return attached.getFile(); 
        }
        return null;

    }
}
