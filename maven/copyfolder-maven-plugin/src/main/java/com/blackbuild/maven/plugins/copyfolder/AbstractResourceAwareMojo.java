/* _____________________________________________________________________________
 *
 * Project: ACM
 * File:    ResourceUtils.java
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
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.component.annotations.Component;

/**
 * TODO Replace with class description.
 */
public abstract class AbstractResourceAwareMojo extends AbstractMojo {

    public List<ResolvedResource> resolveResources(List<Resource> resources, File basedir) throws MojoExecutionException {
        List<ResolvedResource> result = new LinkedList<ResolvedResource>();
        
        for (Resource resource : resources) {
            if (resource.isSimpleResource()) {
                result.add(new ResolvedResource(resource, basedir));
                continue;
            }
            
            File matchdir = new File(basedir, resource.getBasePath());
            
            for (String matchedResource : matchdir.list()) {
                File actualFolder = new File(basedir, resource.getFolder().replace("*", matchedResource));
                
                if (!actualFolder.exists()) {
                    getLog().warn("Matched folder " + actualFolder + " does not exist.");
                    continue;
                }
                
                if (actualFolder.isFile()) {
                    getLog().error("Matched folder " + actualFolder + " must be a directory.");
                    throw new MojoExecutionException("Matched folder " + actualFolder + " must be a directory.");
                }
                
                result.add(new ResolvedResource(actualFolder, resource.getClassifier().replace("*", matchedResource)));
            }
        }
        
        return result;
    }
}
