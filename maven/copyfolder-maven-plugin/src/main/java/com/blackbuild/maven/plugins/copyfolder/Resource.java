/* _____________________________________________________________________________
 *
 * Project: ACM
 * File:    Resource.java
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
public class Resource {
    
    File folder;
    
    String classifier;

    public File getFolder() {
        return this.folder;
    }

    public void setFolder(File folder) {
        this.folder = folder;
    }

    public String getClassifier() {
        return classifier != null ? this.classifier : this.folder.getName();
    }

    public void setClassifier(String classfier) {
        this.classifier = classfier;
    }
    
}
