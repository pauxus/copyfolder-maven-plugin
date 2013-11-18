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
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.Scanner;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;
import org.eclipse.m2e.core.project.configurator.MojoExecutionBuildParticipant;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 * TODO Replace with class description.
 */
public class ProviderBuildParticipant extends MojoExecutionBuildParticipant {

    public ProviderBuildParticipant(MojoExecution execution) {
        super(execution, true);
    }

    @Override
    public Set<IProject> build(int kind, IProgressMonitor monitor) throws Exception {
        IMaven maven = MavenPlugin.getMaven();
        BuildContext buildContext = getBuildContext();

        List<Object> mojo = maven.getMojoParameterValue(getSession().getCurrentProject(), getMojoExecution(), "resources", List.class, monitor);
        
        Properties provisionFolders = resolveResources(mojo, getSession().getCurrentProject().getBasedir());
        
        File output = new File(getSession().getCurrentProject().getBuild().getDirectory(), "copyfolders.m2e.provider.properties");
        
        provisionFolders.store(new FileWriter(output), "Created by m2e-copyfolder plugin");
        
        return null;
    }
    
    public Properties resolveResources(List<Object> resources, File basedir) throws MojoExecutionException {
        Properties result = new Properties();
        
        for (Object rawResource : resources) {
            Resource resource = Resource.fromObject(rawResource);
            
            
            if (resource.isSimpleResource()) {
                File folder = new File(basedir, resource.getFolder());
                result.put(resource.getClassifier() != null ? resource.getClassifier() : folder.getName(), new File(basedir, resource.getFolder()).getAbsolutePath());
                continue;
            }
            
            File matchdir = new File(basedir, resource.getBasePath());
            
            for (String matchedResource : matchdir.list()) {
                File actualFolder = new File(basedir, resource.getFolder().replace("*", matchedResource));
                
                if (!actualFolder.exists()) {
                    continue;
                }
                
                if (actualFolder.isFile()) {
                    throw new MojoExecutionException("Matched folder " + actualFolder + " must be a directory.");
                }
                
                result.put(resource.getClassifier().replace("*", matchedResource), actualFolder.getAbsolutePath());
            }
        }
        
        return result;
    }

}
