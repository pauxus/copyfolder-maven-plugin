/* _____________________________________________________________________________
 *
 * Project: ACM
 * File:    ProvideMojo.java
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
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.NoBannerLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.taskdefs.Zip;

/**
 * Provides one or more folders of the current module to be consumed by another module. The provided folder is packaged into a jar archived using the given classifiers.
 */
@Mojo(name = "provide", aggregator = false, defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.COMPILE, threadSafe = true)
public class ProvideMojo extends AbstractMojo {

    @Component
    private MavenProject project;

    @Component
    private MavenProjectHelper projectHelper;

    /**
     * List of resources to provide. Each resource consists of a folder and an optional classifier. If no classifier is given, the last segment of the folder is used.
     */
    @Parameter
    private List<Resource> resources;

    public void execute() throws MojoExecutionException, MojoFailureException {

        for (Resource resource : resources) {
            File targetArchive = new File(project.getBuild().getDirectory(), project.getBuild().getFinalName() + "-"
                    + resource.getClassifier() + ".jar");

            Zip zip = new Zip();
            zip.setProject(createProject());
            zip.setBasedir(resource.getFolder());
            zip.setDestFile(targetArchive);
            zip.execute();

            projectHelper.attachArtifact(project, "jar", resource.getClassifier(), targetArchive);
        }
    }

    protected static Project createProject() {
        Project project = new Project();

        ProjectHelper helper = ProjectHelper.getProjectHelper();
        project.addReference("ant.projectHelper", helper);
        helper.getImportStack().addElement("AntBuilder");

        BuildLogger logger = new NoBannerLogger();

        logger.setMessageOutputLevel(2);
        logger.setOutputPrintStream(System.out);
        logger.setErrorPrintStream(System.err);

        project.addBuildListener(logger);

        project.init();
        project.getBaseDir();
        return project;
    }

}
