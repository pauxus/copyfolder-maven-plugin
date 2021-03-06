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

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Consumes files provided by the provided goal in a different project.
 */
@Mojo(name = "consume", aggregator = false, defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.TEST, threadSafe = true)
public class FileConsumerMojo extends AbstractConsumerMojo {
    
    /**
     * Where should the files be copied to.
     */
    @Parameter(defaultValue = "${project.build.directory}/consumer")
    private File outputDirectory;
    
    @Override
    protected File getTargetFolder() {
        return outputDirectory;
    }
    
    @Parameter(defaultValue = "false", readonly=true)
    private boolean linkFolders;

    @Override
    protected boolean linkFoldersIfPossible() {
        return false;
    }
    
    @Override
    protected void addNewFolderToMavenModel() {
    }
    
}
