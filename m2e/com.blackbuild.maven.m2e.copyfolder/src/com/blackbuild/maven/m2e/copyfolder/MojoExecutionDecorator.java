/* _____________________________________________________________________________
 *
 * Project: ACM
 * File:    MojoExecutionDecorator.java
 * _____________________________________________________________________________
 *
 * Description:       See class comment
 * _____________________________________________________________________________
 *
 * Copyright: (C) DAIMLER 2013, all rights reserved
 * _____________________________________________________________________________
 */
package com.blackbuild.maven.m2e.copyfolder;

import java.util.List;
import java.util.Map;

import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecution.Source;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.codehaus.plexus.util.xml.Xpp3Dom;

/**
 * TODO Replace with class description.
 */
class MojoExecutionDecorator extends MojoExecution {
    private MojoExecution delegate;
    private String goal;

    public MojoExecutionDecorator(MojoExecution delegate, String goal) {
        super(delegate.getMojoDescriptor());
        this.delegate = delegate;
        this.goal = goal;
    }
    
    public int hashCode() {
        return delegate.hashCode();
    }

    public Source getSource() {
        return delegate.getSource();
    }

    public String getExecutionId() {
        return delegate.getExecutionId();
    }

    public Plugin getPlugin() {
        return delegate.getPlugin();
    }

    public MojoDescriptor getMojoDescriptor() {
        return delegate.getMojoDescriptor();
    }

    public Xpp3Dom getConfiguration() {
        return delegate.getConfiguration();
    }

    public void setConfiguration(Xpp3Dom configuration) {
        delegate.setConfiguration(configuration);
    }

    public String identify() {
        return delegate.identify();
    }

    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    public String getLifecyclePhase() {
        return delegate.getLifecyclePhase();
    }

    public void setLifecyclePhase(String lifecyclePhase) {
        delegate.setLifecyclePhase(lifecyclePhase);
    }

    public String toString() {
        return delegate.toString();
    }

    public String getGroupId() {
        return delegate.getGroupId();
    }

    public String getArtifactId() {
        return delegate.getArtifactId();
    }

    public String getVersion() {
        return delegate.getVersion();
    }

    public String getGoal() {
        return this.goal;
    }

    public void setMojoDescriptor(MojoDescriptor mojoDescriptor) {
        delegate.setMojoDescriptor(mojoDescriptor);
    }

    public Map<String, List<MojoExecution>> getForkedExecutions() {
        return delegate.getForkedExecutions();
    }

    public void setForkedExecutions(String projectKey, List<MojoExecution> forkedExecutions) {
        delegate.setForkedExecutions(projectKey, forkedExecutions);
    }
}