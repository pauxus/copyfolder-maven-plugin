/* _____________________________________________________________________________
 *
 * Project: ACM
 * File:    CopyFolderBuildParticipant.java
 * _____________________________________________________________________________
 *
 * Description:       See class comment
 * _____________________________________________________________________________
 *
 * Copyright: (C) DAIMLER 2013, all rights reserved
 * _____________________________________________________________________________
 */
package com.blackbuild.maven.m2e.copyfolder.legacy;

import java.io.File;
import java.util.Set;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.eclipse.m2e.core.project.configurator.MojoExecutionBuildParticipant;

/**
 * TODO Replace with class description.
 */
public class CopyFolderBuildParticipant extends MojoExecutionBuildParticipant {

    public CopyFolderBuildParticipant(MojoExecution execution) {
        super(execution, true);
    }

    @Override
    public Set<IProject> build(int kind, IProgressMonitor monitor) throws Exception {
        IMaven maven = MavenPlugin.getMaven();

        IMavenProjectFacade sourceProject = findSourceProject(monitor);

        File generated = maven.getMojoParameterValue(getSession().getCurrentProject(), getMojoExecution(), "outputDirectory", File.class, monitor);
        IPath projectRelativePath = getMavenProjectFacade().getProjectRelativePath(generated.getAbsolutePath());
        IProject eclipseProject = getMavenProjectFacade().getProject();
        IFolder localFolder = eclipseProject.getFolder(projectRelativePath);

        if (sourceProject == null) {
            // no open source project, let the mojo do its work
            if (localFolder.isLinked()) {
                localFolder.delete(true, monitor);
            }

            maven.execute(getSession().getCurrentProject(), getMojoExecution(), monitor);
            return null;
        }

        String classifier = maven.getMojoParameterValue(getSession().getCurrentProject(), getMojoExecution(), "classifier", String.class, monitor);

        IPath external = (IPath) sourceProject.getProject().getSessionProperty(new QualifiedName(ProviderBuildParticipant.MAPPING_QNAME, classifier));

        Boolean linkFolders = maven.getMojoParameterValue(getSession().getCurrentProject(), getMojoExecution(), "linkFolders", Boolean.class, monitor);

        if (!linkFolders) {
            if (localFolder.exists()) localFolder.delete(true, monitor);

            IFolder externalFolder = sourceProject.getProject().getFolder(sourceProject.getProjectRelativePath(external.toOSString()));

            localFolder.create(false, false, monitor);

            externalFolder.copy(localFolder.getLocation(), IResource.FORCE | IResource.DERIVED, monitor);

            return null;
        }

        prepare(localFolder, monitor);

        if (!localFolder.getRawLocation().equals(external)) {
            localFolder.createLink(external, IResource.REPLACE | IResource.ALLOW_MISSING_LOCAL, monitor);
        }

        return null;
    }

    private IMavenProjectFacade findSourceProject(IProgressMonitor monitor) throws CoreException {
        IMaven maven = MavenPlugin.getMaven();

        IMavenProjectRegistry projectRegistry = MavenPlugin.getMavenProjectRegistry();

        String source = maven.getMojoParameterValue(getSession().getCurrentProject(), getMojoExecution(), "source", String.class, monitor);

        Dependency baseArtifact = findMatchingArtifact(source);

        if (baseArtifact == null) return null;
        return projectRegistry.getMavenProject(baseArtifact.getGroupId(), baseArtifact.getArtifactId(), baseArtifact.getVersion());
    }

    private Dependency findMatchingArtifact(String source) {
        String groupId = null;
        String artifactId;

        int index = source.indexOf(":");

        if (index < 0) {
            artifactId = source;
        } else {
            groupId = source.substring(0, index);
            artifactId = source.substring(index + 1);
        }

        for (Dependency dependency : getSession().getCurrentProject().getDependencies()) {
            if (dependency.getArtifactId().equals(artifactId)
                    && (groupId == null || dependency.getGroupId().equals(groupId))) {
                return dependency;
            }
        }

        return null;
    }



    private void prepare(IFolder folder, IProgressMonitor monitor) throws CoreException {
        if (!folder.exists()) {
            prepare((IFolder) folder.getParent(), monitor);
            folder.create(false, false, monitor);
        }
    }

}
