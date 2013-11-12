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

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.gmaven.mojo.GroovyMojo;
import org.sonatype.inject.Parameters;

/**
 * TODO Replace with class description.
 */
@Mojo(name = "consume", aggregator = false, defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE, threadSafe = true)
public class ConsumeMojo extends GroovyMojo {

    @Component
    MavenProject project;

    @Component
    MavenSession session;

    @Parameter(required = true)
    String sourceProject;

    @Parameter
    String sourcePath;

    @Parameter
    String targetFolder;

    public void execute() throws MojoExecutionException, MojoFailureException {
        System.out.println(session);
        
//        for (File source : sources) {
//            this.project.addCompileSourceRoot(source.getAbsolutePath());
//            if (getLog().isInfoEnabled()) {
//                getLog().info("Source directory: " + source + " added.");
//            }
//        }

    }

    private MavenProject findSourceProject() {
//        for (MavenProject next : session.getProjects()) {
//            if (next.)
//        }
        return null;
    }
}
