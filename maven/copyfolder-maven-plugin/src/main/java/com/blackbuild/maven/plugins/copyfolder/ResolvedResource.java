/* _____________________________________________________________________________
 *
 * Project: ACM
 * File:    ResolvedResource.java
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

/**
 * TODO Replace with class description.
 */
public class ResolvedResource {

    private final File folder;
    
    private final String classifier;

    public ResolvedResource(File folder, String classifier) {
        super();
        this.folder = folder;
        this.classifier = classifier != null ? classifier : folder.getName();
    }

    public ResolvedResource(Resource resource, File basedir) {
        this(new File(basedir, resource.getFolder()), resource.getClassifier());
    }
    
    public File getFolder() {
        return this.folder;
    }

    public String getClassifier() {
        return this.classifier;
    }
}
