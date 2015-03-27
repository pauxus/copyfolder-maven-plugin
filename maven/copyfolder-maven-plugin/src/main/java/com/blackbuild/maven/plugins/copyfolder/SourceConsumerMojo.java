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
@Mojo(name = "consume-source", aggregator = false, defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.TEST, threadSafe = true)
public class SourceConsumerMojo extends AbstractConsumerMojo {
    
    /**
     * Where should the files be copied to.
     */
    @Parameter(defaultValue="${project.build.directory}/generated-sources/consumer")
    private File outputDirectory;
    
    @Parameter(defaultValue="true")
    private boolean linkFolders;
    
    /**
     * Does the copied folder contain both sources and resources?
     */
    @Parameter(defaultValue="false")
    private boolean combined;
    
    @Component
    private MavenProjectHelper projectHelper;
    
    @Override
    protected File getTargetFolder() {
        return outputDirectory;
    }
    
    @Override
    protected boolean linkFoldersIfPossible() {
        return linkFolders;
    }
    
    @Override
    protected void addNewFolderToMavenModel() {
        if (realTargetFolder == null) {
            getLog().warn("Target folder is null, not adding anything!");
            return;
        }
        
        this.project.addCompileSourceRoot(realTargetFolder.getPath());
        getLog().info("Source directory: '" + outputDirectory + "' added.");
        
        if (combined) {
            getLog().info("Combined folder, adding as resource as well.");
            projectHelper.addResource(project, realTargetFolder.getPath(), Collections.EMPTY_LIST, Collections.singletonList("**/*.java"));
            getLog().info("Resource directory: '" + outputDirectory + "' added.");
        }
    }
}
