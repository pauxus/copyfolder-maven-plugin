package com.blackbuild.maven.m2e.copyfolder;

import java.lang.reflect.InvocationTargetException;
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

    public static Resource fromObject(Object rawResource) {
        Class<? extends Object> otherClass = rawResource.getClass();
        
        Resource result = new Resource();
        try {
            result.setFolder((String) otherClass.getMethod("getFolder").invoke(rawResource));
            result.setClassifier((String) otherClass.getMethod("getClassifier").invoke(rawResource));
        } catch (Exception e) {
            
        }
                       
        return result;
    }
}
