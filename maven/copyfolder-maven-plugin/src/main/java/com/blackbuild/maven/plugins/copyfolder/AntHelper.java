/* _____________________________________________________________________________
 *
 * Project: ACM
 * File:    AntHelper.java
 * _____________________________________________________________________________
 *
 * Description:       See class comment
 * _____________________________________________________________________________
 *
 * Copyright: (C) DAIMLER 2013, all rights reserved
 * _____________________________________________________________________________
 */
package com.blackbuild.maven.plugins.copyfolder;

import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.NoBannerLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

/**
 * TODO Replace with class description.
 */
public class AntHelper {

    static Project createProject() {
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
