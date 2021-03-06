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
package com.blackbuild.maven.m2e.copyfolder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
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

        File generated = maven.getMojoParameterValue(getSession().getCurrentProject(), getMojoExecution(), "outputDirectory", File.class, monitor);
        IPath projectRelativePath = getMavenProjectFacade().getProjectRelativePath(generated.getAbsolutePath());
        IProject eclipseProject = getMavenProjectFacade().getProject();
        IFolder localFolder = eclipseProject.getFolder(projectRelativePath);

        List<String> oldRoots = new ArrayList<String>(getSession().getCurrentProject().getCompileSourceRoots());
        ArrayList<org.apache.maven.model.Resource> oldResources = new ArrayList<org.apache.maven.model.Resource>(getSession().getCurrentProject().getResources());

        // execute the mojo
        maven.execute(getSession().getCurrentProject(), getMojoExecution(), monitor);

        List<String> newRoots = new ArrayList<String>(getSession().getCurrentProject().getCompileSourceRoots());

        String realFolder;

        newRoots.removeAll(oldRoots);
        if (! newRoots.isEmpty()) {
            realFolder = newRoots.get(0);
        } else {
            ArrayList<org.apache.maven.model.Resource> newResources = new ArrayList<org.apache.maven.model.Resource>(getSession().getCurrentProject().getResources());
            newResources.removeAll(oldResources);

            if (!newResources.isEmpty()) {
                realFolder = newResources.get(0).getDirectory();
            } else {
                return null;
            }
        }

        IPath external = Path.fromOSString(realFolder);

        if (getMavenProjectFacade().getProjectRelativePath(external.toOSString()) != null) {
            if (localFolder.isLinked()) {
                localFolder.delete(true, monitor);
            }
            return null;
        }

        prepare(localFolder, monitor);

        if (!localFolder.getRawLocation().equals(external)) {
            localFolder.createLink(external, IResource.REPLACE | IResource.ALLOW_MISSING_LOCAL, monitor);
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
