package com.thedeanda.regresql.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;


// https://dzone.com/articles/a-simple-maven-3-plugin
@Mojo(name = "hello", defaultPhase = LifecyclePhase.COMPILE)
public class RegresqlMojo extends AbstractMojo {

    //@Parameter(property = "msg",defaultValue = "from maven")
    private String msg = "test";

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Hello " + msg);
    }
}
