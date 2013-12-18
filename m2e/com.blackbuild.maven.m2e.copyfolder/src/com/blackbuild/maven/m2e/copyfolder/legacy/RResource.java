package com.blackbuild.maven.m2e.copyfolder.legacy;

 
public class RResource {
    
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

    public static RResource fromObject(Object rawResource) {
        Class<? extends Object> otherClass = rawResource.getClass();
        
        RResource result = new RResource();
        try {
            result.setFolder((String) otherClass.getMethod("getFolder").invoke(rawResource));
            result.setClassifier((String) otherClass.getMethod("getClassifier").invoke(rawResource));
        } catch (Exception e) {
            
        }
                       
        return result;
    }
}
