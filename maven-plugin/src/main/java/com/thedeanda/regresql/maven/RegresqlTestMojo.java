package com.thedeanda.regresql.maven;

import com.thedeanda.regresql.RegresqlService;
import com.thedeanda.regresql.datasource.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

import static org.apache.commons.lang3.StringUtils.isBlank;


// https://dzone.com/articles/a-simple-maven-3-plugin
@Mojo(name = "test")
public class RegresqlTestMojo extends AbstractMojo {

    @Parameter(property = "url")
    private String url;
    @Parameter(property = "username")
    private String username;
    @Parameter(property = "password")
    private String password;
    @Parameter(property = "source-dir")
    private String sourceDir;
    @Parameter(property = "expected-dir")
    private String expectedDir;
    @Parameter(property = "output-dir")
    private String outputDir;
    @Parameter(property = "max-errors", defaultValue = "10")
    private int maxErrors;


    File source;
    File expected;
    File output;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Running Regresql Test");

        checkParams();

        DataSource dataSource = new DataSource(url, username, password);
        RegresqlService service = new RegresqlService(dataSource, source, expected);
        try {
            service.runAllTests(output, maxErrors);
        } catch (Exception e) {
            //getLog().error(e);
            throw new MojoFailureException("Test failed", e);
        }
    }

    private void checkParams() throws MojoFailureException {
        getLog().info("Verifying parameters");

        if (isBlank(url))
            throw new MojoFailureException("Param 'url' is required to connect to datasource");
        if (isBlank(sourceDir))
            throw new MojoFailureException("Param 'source-dir' is required");
        if (isBlank(expectedDir))
            throw new MojoFailureException("Param 'expected-dir' is required");
        if (isBlank(outputDir))
            throw new MojoFailureException("Param 'output-dir' is required");

        source = new File(sourceDir);
        expected = new File(expectedDir);
        output = new File(outputDir);

        if (!source.exists())
            throw new MojoFailureException("Source folder '" + sourceDir + "' does not exist");
        if (!expected.exists())
            throw new MojoFailureException("Expected folder '" + expectedDir + "' does not exist");
    }
}
