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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.types.FileSet;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.plexus.build.incremental.BuildContext;

public abstract class AbstractConsumerMojo extends AbstractResourceAwareMojo {

    @Component
    protected MavenProject project;

    @Component
    private MavenSession session;

    @Component
    private RepositorySystem repoSystem;

    @Component
    private PluginDescriptor descriptor;
    
    @Component
    private BuildContext buildContext;
    
    @Parameter(readonly = true, defaultValue = "${repositorySystemSession}")
    private RepositorySystemSession repoSession;

    @Parameter(readonly = true, defaultValue = "${project.remoteProjectRepositories}")
    private List<RemoteRepository> remoteRepos;

    @Parameter(defaultValue = "consumer.link.path")
    private File linkMarkerProperty;
    
    protected File realTargetFolder;

    /**
     * Project to consume files from. format: [groupId:]artifactId. The consumed project must be a dependency of this
     * project.
     */
    @Parameter(required = true)
    private String source;

    /**
     * Classifier of the resource to consume.
     */
    @Parameter(required = true)
    private String classifier;

    public void execute() throws MojoExecutionException, MojoFailureException {

        Dependency baseArtifact = findMatchingArtifact();

        Artifact provided = new DefaultArtifact(baseArtifact.getGroupId(), baseArtifact.getArtifactId(), classifier,
                "jar", baseArtifact.getVersion());

        MavenProject reactorProject = findSourceProject(provided);

        File sourceFile = null;

        if (reactorProject != null) {
            sourceFile = readSourceFromReactor(reactorProject);
        }

        if (sourceFile == null) {
            sourceFile = readSourceFromArtifact(provided);
        }

        getLog().info("Using source from " + sourceFile);

        if (sourceFile.isFile()) {
            copyFromArtifact(sourceFile);
        } else if (reactorProject == null) {
            copyFromPropertiesFolder(sourceFile);
            
        } else {
            copyOrLinkFromReactorProject(reactorProject);
        }

        addNewFolderToMavenModel();
        
        buildContext.refresh(getTargetFolder());
        buildContext.refresh(realTargetFolder);
    }

    private void copyFromPropertiesFolder(File sourceFile) throws MojoExecutionException {
        getLog().info("Source is a non-reactor folder, assuming IDE build, checking for properties.");
        
        File mappingFile = new File(sourceFile, AbstractProviderMojo.MAPPING_FILE_NAME);
        
        if (!mappingFile.isFile()) {
            throw new MojoExecutionException("IDE Build, but not Mapping file present. You either need to execute the create-mapping goal or install the copyfolder m2e-provider.");
        }
        
        Properties mapping = new Properties();
        try {
            mapping.load(new FileInputStream(mappingFile));
        } catch (IOException e) {
            throw new MojoExecutionException("Could not read Mapping file", e);
        }
        
        File target = new File(sourceFile, mapping.getProperty(classifier));
        
        copyOrLinkFolder(target);
    }

    private File readSourceFromArtifact(Artifact provided) throws MojoExecutionException {
        try {
            return repoSystem.resolveArtifact(repoSession, new ArtifactRequest(provided, remoteRepos, null))
                    .getArtifact().getFile();
        } catch (ArtifactResolutionException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private File readSourceFromReactor(MavenProject reactorProject) {
        return new File(reactorProject.getBuild().getOutputDirectory());
    }

    protected abstract void addNewFolderToMavenModel();

    protected abstract File getTargetFolder();

    protected abstract boolean linkFoldersIfPossible();

    private void copyOrLinkFromReactorProject(MavenProject reactorProject) throws MojoExecutionException {
        for (PluginExecution execution : reactorProject.getPlugin(descriptor.getPluginLookupKey()).getExecutions()) {
            if (!execution.getGoals().contains("provide")) {
                continue;
            }

            getLog().info("Consuming files provided by " + execution.getId());

            Xpp3Dom configuration = (Xpp3Dom) execution.getConfiguration();

            if (configuration == null) {
                continue;
            }

            ResolvedResource target = findResourceWithClassifier(configuration.getChild("resources"),
                    reactorProject.getBasedir());

            copyOrLinkFolder(target.getFolder());
        }
    }

    private void copyOrLinkFolder(File target) {
        if (linkFoldersIfPossible()) {
            getLog().info("Only linking source folder (" + target + ").");
            realTargetFolder = target;
            Delete delete = new Delete();
            delete.setProject(AntHelper.createProject());
            delete.setQuiet(true);
            delete.setDir(getTargetFolder());
            delete.setTaskName("LINK");
            delete.execute();
            getTargetFolder().mkdirs();
        } else {
            getLog().info("Copying source folder content (" + target + ").");
            Copy copy = new Copy();
            copy.setProject(AntHelper.createProject());
            copy.setTodir(getTargetFolder());
            FileSet copySource = new FileSet();
            copySource.setDir(target);

            copy.addFileset(copySource);

            copy.execute();
        }
    }

    private ResolvedResource findResourceWithClassifier(Xpp3Dom resources, File basedir) throws MojoExecutionException {
        List<Resource> sourceResource = new ArrayList<Resource>(resources.getChildCount());

        for (Xpp3Dom node : resources.getChildren()) {
            sourceResource.add(nodeToResource(node));
        }

        List<ResolvedResource> resolvedResources = resolveResources(sourceResource, basedir);

        for (ResolvedResource resolvedResource : resolvedResources) {
            if (resolvedResource.getClassifier().equals(classifier)) {
                return resolvedResource;
            }
        }

        throw new MojoExecutionException("No resource with classifier '" + classifier + "' found.");
    }

    private Resource nodeToResource(Xpp3Dom resource) {
        Resource result = new Resource();
        result.setFolder(resource.getChild("folder").getValue());
        if (resource.getChild("classifier") != null) {
            result.setClassifier(resource.getChild("classifier").getValue());
        }
        return result;
    }

    private void copyFromArtifact(File sourceFile) throws MojoExecutionException {
        Expand expand = new Expand();
        expand.setProject(AntHelper.createProject());
        expand.setTaskType("jar");
        expand.setTaskName("CONSUME");
        expand.setSrc(sourceFile);
        expand.setDest(getTargetFolder());
        expand.execute();

        this.realTargetFolder = getTargetFolder();
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
            if (dependency.getArtifactId().equals(artifactId)
                    && (groupId == null || dependency.getGroupId().equals(groupId))) {
                return dependency;
            }
        }

        getLog().error("Could not find matching dependency for '" + source + "'");
        throw new MojoExecutionException("Could not find matching dependency for '" + source + "'");
    }

    private MavenProject findSourceProject(Artifact artifact) {
        for (MavenProject sessionProject : session.getProjects()) {
            if (projectMatchesArtifact(sessionProject, artifact)) {
                return sessionProject;
            }
        }

        return null;
    }

    private boolean projectMatchesArtifact(MavenProject projectToTest, Artifact artifact) {
        return projectToTest.getGroupId().equals(artifact.getGroupId())
                && projectToTest.getArtifactId().equals(artifact.getArtifactId())
                && projectToTest.getVersion().equals(artifact.getVersion());
    }
}
