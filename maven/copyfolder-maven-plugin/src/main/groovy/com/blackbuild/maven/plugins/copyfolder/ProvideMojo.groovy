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

import javax.management.modelmbean.RequiredModelMBean;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.gmaven.mojo.GroovyMojo;

/**
 * Provides 
 */
@Mojo(name = "provide", 
    aggregator = false, 
    defaultPhase = LifecyclePhase.PACKAGE, 
    requiresDependencyResolution = ResolutionScope.COMPILE, 
    threadSafe = true)
public class ProvideMojo extends GroovyMojo {

    @Component
    MavenProject project

    @Component
    MavenProjectHelper projectHelper

    @Parameter(required = true)
    Resource[] resources

    public void execute() throws MojoExecutionException, MojoFailureException {

        resources.each { resource ->
            def classifier = resource.classfier ?: resource.folder.name
            def targetArchive = new File(project.build.directory, project.build.finalName + ".jar")
            ant.zip(basedir : resource.folder, destFile : targetArchive)
            projectHelper.attachArtifact(project, "jar", classifier, targetArchive)
        }
   }
    
}
