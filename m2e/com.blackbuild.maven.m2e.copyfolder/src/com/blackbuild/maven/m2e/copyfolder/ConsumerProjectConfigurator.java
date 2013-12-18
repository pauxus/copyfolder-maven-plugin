package com.blackbuild.maven.m2e.copyfolder;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.lifecyclemapping.model.IPluginExecutionMetadata;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.MavenProjectChangedEvent;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;
import org.eclipse.m2e.core.project.configurator.ILifecycleMappingConfiguration;
import org.eclipse.m2e.core.project.configurator.MojoExecutionKey;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.m2e.jdt.AbstractSourcesGenerationProjectConfigurator;

public class ConsumerProjectConfigurator extends AbstractSourcesGenerationProjectConfigurator {
    
    @Override
    public AbstractBuildParticipant getBuildParticipant( IMavenProjectFacade projectFacade,
                                                         MojoExecution execution,
                                                         IPluginExecutionMetadata executionMetadata )
    {
        return new CopyFolderBuildParticipant( execution );
    }
    
    @Override
    public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
        super.configure(request, monitor);
        IMavenProjectFacade mavenProject = request.getMavenProjectFacade();
        deleteOutputFolders(monitor, mavenProject);
    }

    private void deleteOutputFolders(IProgressMonitor monitor, IMavenProjectFacade mavenProject) throws CoreException {
        List<MojoExecution> executions = mavenProject.getMojoExecutions("com.blackbuild.maven.plugins", "copyfolder-maven-plugin", monitor, "consume-resources", "consume", "consume-sources");
        IProject project = mavenProject.getProject();
        
        for (MojoExecution mojoExecution : executions) {
            File generated = maven.getMojoParameterValue(mavenProject.getMavenProject(), mojoExecution, "outputDirectory", File.class, monitor);
            
            IPath relativePath = mavenProject.getProjectRelativePath(generated.getAbsolutePath());
            IFolder localFolder = project.getFolder(relativePath);
            
            localFolder.delete(true, monitor);
        }
    }
}
