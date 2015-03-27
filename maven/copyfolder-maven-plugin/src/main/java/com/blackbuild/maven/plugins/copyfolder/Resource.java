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


/**
 * TODO Replace with class description.
 */
public class Resource {
    
    private String folder;
    
    private String classifier;

    public String getFolder() {
        return this.folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getClassifier() {
        if (classifier != null) return this.classifier;
        
        return isSimpleResource() ? null : "*";
    }

    public void setClassifier(String classfier) {
        this.classifier = classfier;
    }

    public boolean isSimpleResource() {
        return !folder.contains("*");
    }
    
    public String getBasePath() {
        return folder.substring(0, folder.indexOf('*'));
    }
}
