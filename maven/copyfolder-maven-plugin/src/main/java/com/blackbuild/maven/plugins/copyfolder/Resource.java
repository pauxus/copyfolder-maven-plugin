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
    
    String classfier;

    public File getFolder() {
        return this.folder;
    }

    public void setFolder(File folder) {
        this.folder = folder;
    }

    public String getClassfier() {
        return this.classfier;
    }

    public void setClassfier(String classfier) {
        this.classfier = classfier;
    }
    
}
