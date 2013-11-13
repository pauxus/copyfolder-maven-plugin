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

/**
 * TODO Replace with class description.
 */
@Mojo(name = "consume", aggregator = false, defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE, threadSafe = true)
public class ConsumeMojo extends AbstractMojo {

    @Component
    private MavenProject project;

    @Component
    private MavenSession session;

    @Parameter(required = true)
    private String sourceProject;

    @Parameter
    private String sourcePath;

    @Parameter
    private String targetFolder;

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
