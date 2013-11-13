/* _____________________________________________________________________________
 *
 * Project: ACM
 * File:    ProvideMojo.java
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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.tools.ant.taskdefs.Zip;

/**
 * Provides 
 */
@Mojo(name = "provide", 
    aggregator = false, 
    defaultPhase = LifecyclePhase.PACKAGE, 
    requiresDependencyResolution = ResolutionScope.COMPILE, 
    threadSafe = true)
public class ProvideMojo extends AbstractMojo {

    @Component
    private MavenProject project;

    @Component
    private MavenProjectHelper projectHelper;

    @Parameter
    private List<Resource>  resources;

    public void execute() throws MojoExecutionException, MojoFailureException {

        for (Resource resource : resources) {
            String classifier = resource.getClassfier() != null ? resource.getClassfier() : resource.getFolder().getName();
            File targetArchive = new File(project.getBuild().getDirectory(), project.getBuild().getFinalName()+ ".jar");
            
            Zip zip = new Zip();
            zip.setBasedir(resource.getFolder());
            zip.setBasedir(targetArchive);
            
            projectHelper.attachArtifact(project, "jar", classifier, targetArchive);
        }
   }
    
}
