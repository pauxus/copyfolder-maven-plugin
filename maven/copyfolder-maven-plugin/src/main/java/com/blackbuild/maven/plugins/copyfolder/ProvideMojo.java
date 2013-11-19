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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.tools.ant.taskdefs.Zip;

/**
 * Provides one or more folders of the current module to be consumed by another module. The provided folder is packaged into a jar archived using the given classifiers.
 */
@Mojo(name = "provide", aggregator = false, defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true)
public class ProvideMojo extends AbstractResourceAwareMojo {

    @Component
    protected MavenProject project;

    /**
     * List of resources to provide. Each resource consists of a folder and an optional classifier. If no classifier is given, the last segment of the folder is used.
     */
    @Parameter
    private List<Resource> resources;

    @Component
    private MavenProjectHelper projectHelper;
    
    public void execute() throws MojoExecutionException, MojoFailureException {

        validateParameters();
        
        List<ResolvedResource> resolvedResources = resolveResources(resources, project.getBasedir());
        
        for (ResolvedResource resource : resolvedResources) {
            packageAndAddResource(resource);
        }
    }

    protected void validateParameters() throws MojoExecutionException {
        if (resources == null || resources.isEmpty()) {
            throw new MojoExecutionException("You must define at least one resource.");
        }
    }

    protected void packageAndAddResource(ResolvedResource resource) {
        File targetArchive = new File(project.getBuild().getDirectory(), project.getBuild().getFinalName() + "-"
                + resource.getClassifier() + ".jar");
    
        Zip zip = new Zip();
        zip.setProject(AntHelper.createProject());
        zip.setTaskName("PROVIDE");
        zip.setBasedir(resource.getFolder());
        zip.setDestFile(targetArchive);
        zip.execute();
    
        projectHelper.attachArtifact(project, "jar", resource.getClassifier(), targetArchive);
    }
}
