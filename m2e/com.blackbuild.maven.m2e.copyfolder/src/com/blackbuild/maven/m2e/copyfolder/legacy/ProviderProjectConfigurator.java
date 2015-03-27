package com.blackbuild.maven.m2e.copyfolder.legacy;

import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.lifecyclemapping.model.IPluginExecutionMetadata;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;

public class ProviderProjectConfigurator extends AbstractProjectConfigurator {
    
    @Override
    public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
    }
    
    @Override
    public AbstractBuildParticipant getBuildParticipant( IMavenProjectFacade projectFacade,
                                                         MojoExecution execution,
                                                         IPluginExecutionMetadata executionMetadata )
    {
        return new ProviderBuildParticipant( execution );
    }

    
//    @Override
//    public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
//        
//        List<MojoExecution> mojoExecutions = request.getMavenProjectFacade().getMojoExecutions("com.blackbuild.maven.plugins", "copyfolder-maven-plugin", monitor, "provide");
//        
//        System.out.println(mojoExecutions);
//    
//    }

}
