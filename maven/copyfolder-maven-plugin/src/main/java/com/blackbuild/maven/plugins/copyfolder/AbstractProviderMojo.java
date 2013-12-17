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

import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

public abstract class AbstractProviderMojo extends AbstractResourceAwareMojo {

    @Component
    protected MavenProject project;

    /**
     * List of resources to provide. Each resource consists of a folder and an optional classifier. If no classifier is given, the last segment of the folder is used.
     */
    @Parameter
    private List<Resource> resources;
    
    /**
     * Should this execution allow missing sources?
     */
    @Parameter(defaultValue = "false")
    protected boolean allowMissing;

    @Component
    protected MavenProjectHelper projectHelper;
    
    public void execute() throws MojoExecutionException, MojoFailureException {

        validateParameters();
        
        List<ResolvedResource> resolvedResources = resolveResources(resources, project.getBasedir());
        
        for (ResolvedResource resource : resolvedResources) {
            packageAndAddResource(resource);
        }
    }

    protected abstract void packageAndAddResource(ResolvedResource resource) throws MojoExecutionException;

    protected void validateParameters() throws MojoExecutionException {
        if (resources == null || resources.isEmpty()) {
            throw new MojoExecutionException("You must define at least one resource.");
        }
    }
}
