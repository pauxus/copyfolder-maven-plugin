/* _____________________________________________________________________________
 *
 * Project: ACM
 * File:    ProviderMojo.java
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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.taskdefs.Zip.WhenEmpty;

/**
 * Provides one or more folders of the current module to be consumed by another module. The provided folder is packaged into a jar archived using the given classifiers.
 */
@Mojo(name = "provide", aggregator = false, defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true)
public class ProviderMojo extends AbstractProviderMojo {

    protected void packageAndAddResource(ResolvedResource resource) throws MojoExecutionException {
        if (!resource.getFolder().isDirectory()) {
            if (allowMissing) {
                resource.getFolder().mkdirs();
            } else {
                throw new MojoExecutionException(resource.getFolder() + " does not exist");
            }
        }
        
        File targetArchive = new File(project.getBuild().getDirectory(), project.getBuild().getFinalName() + "-"
                + resource.getClassifier() + ".jar");
    
        Zip zip = new Zip();
        zip.setProject(AntHelper.createProject());
        zip.setTaskName("PROVIDE");
        zip.setBasedir(resource.getFolder());
        zip.setDestFile(targetArchive);
        zip.setWhenempty((WhenEmpty) WhenEmpty.getInstance(WhenEmpty.class, "create"));
        zip.execute();
    
        projectHelper.attachArtifact(project, "jar", resource.getClassifier(), targetArchive);
    }

}
