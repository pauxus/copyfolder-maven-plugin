/* _____________________________________________________________________________
 *
 * Project: ACM
 * File:    SourceConsumerMojo.java
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
import java.util.Collections;

import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProjectHelper;

/**
 * Consumes files provided by the provided goal in a different project.
 */
@Mojo(name = "consume-resource", aggregator = false, defaultPhase = LifecyclePhase.GENERATE_RESOURCES, requiresDependencyResolution = ResolutionScope.TEST, threadSafe = true)
public class ResourceConsumerMojo extends AbstractConsumerMojo {

    /**
     * Where should the files be copied to.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-resources/consumer")
    private File outputDirectory;

    @Parameter(defaultValue = "true")
    private boolean linkFolders;

    @Override
    protected File getTargetFolder() {
        return outputDirectory;
    }

    @Override
    protected boolean linkFoldersIfPossible() {
        return linkFolders;
    }

    @Component
    private MavenProjectHelper projectHelper;

    @Override
    protected void addNewFolderToMavenModel() {
        if (realTargetFolder == null) {
            getLog().warn("Target folder is null, not adding anything!");
            return;
        }
        
        projectHelper.addResource(project, realTargetFolder.getPath(), Collections.EMPTY_LIST, Collections.EMPTY_LIST);
        getLog().info("Resource directory: '" + outputDirectory + "' added.");
    }

}
