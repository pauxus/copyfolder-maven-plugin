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
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.project.configurator.MojoExecutionBuildParticipant;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 * TODO Replace with class description.
 */
public class ProviderBuildParticipant extends MojoExecutionBuildParticipant {

    static final String MAPPING_QNAME = "com.blackbuild.maven.m2e.copyfolder.mapping";

    public ProviderBuildParticipant(MojoExecution execution) {
        super(execution, true);
    }

    @Override
    public Set<IProject> build(int kind, IProgressMonitor monitor) throws Exception {
        IMaven maven = MavenPlugin.getMaven();
        BuildContext buildContext = getBuildContext();

        List<Object> mojo = maven.getMojoParameterValue(getSession().getCurrentProject(), getMojoExecution(), "resources", List.class, monitor);
        
        storeResolveResources(mojo, getSession().getCurrentProject().getBasedir(), buildContext);
        
        return null; 
    }
    
    public void storeResolveResources(List<Object> resources, File basedir, BuildContext buildContext) throws CoreException, MojoExecutionException {
        IProject project = getMavenProjectFacade().getProject();
        
        for (Object rawResource : resources) {
            RResource resource = RResource.fromObject(rawResource);
            
            
            if (resource.isSimpleResource()) {
                File folder = new File(basedir, resource.getFolder());
                String classifier = resource.getClassifier() != null ? resource.getClassifier() : folder.getName();

                project.setSessionProperty(new QualifiedName(MAPPING_QNAME, classifier), Path.fromOSString(folder.getAbsolutePath()));
                
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

                project.setSessionProperty(new QualifiedName(MAPPING_QNAME, resource.getClassifier().replace("*", matchedResource)), Path.fromOSString(actualFolder.getAbsolutePath()));
            }
        }
    }

}
