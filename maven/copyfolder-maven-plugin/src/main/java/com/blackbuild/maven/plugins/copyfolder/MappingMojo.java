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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Provides one or more folders of the current module to be consumed by another module. The provided folder is packaged
 * into a jar archived using the given classifiers.
 */
@Mojo(name = "create-mapping", aggregator = false, defaultPhase = LifecyclePhase.PREPARE_PACKAGE, threadSafe = true)
public class MappingMojo extends AbstractProviderMojo {

    private Properties mapping;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        mapping = new Properties();

        if (mappingFile.isFile()) {
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(mappingFile);
                mapping.load(fileInputStream);
            } catch (IOException e) {
                throw new MojoExecutionException("Could not load existing mapping file", e);
            } finally {
                IOUtils.closeQuietly(fileInputStream);
            }
        } else {
            mappingFile.getParentFile().mkdirs();
        }

        super.execute();

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(mappingFile);
            mapping.store(fileOutputStream, "");
        } catch (IOException e) {
            throw new MojoExecutionException("Could not create mapping file", e);
        } finally {
            IOUtils.closeQuietly(fileOutputStream);
        }
    }

    protected void packageAndAddResource(ResolvedResource resource) throws MojoExecutionException {
        if (!resource.getFolder().isDirectory() && !allowMissing) {
            throw new MojoExecutionException(resource.getFolder() + " does not exist");
        }

        String relative = ResourceUtils.getRelativePath(resource.getFolder().toURI().toString(), classesDir.toURI()
                .toString());

        mapping.setProperty(resource.getClassifier(), relative);
    }
}
