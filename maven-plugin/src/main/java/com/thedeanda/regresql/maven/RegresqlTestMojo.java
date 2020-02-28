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

import java.io.*;
import java.util.Properties;

import static org.apache.commons.lang3.StringUtils.isBlank;


// https://dzone.com/articles/a-simple-maven-3-plugin
@Mojo(name = "test")
public class RegresqlTestMojo extends AbstractMojo {

    public static final String PROP_URL = "url";
    public static final String PROP_USERNAME = "username";
    public static final String PROP_PASSWORD = "password";
    public static final String PROP_SOURCE = "source-dir";
    public static final String PROP_EXPECTED = "expected-dir";
    public static final String PROP_OUTPUT = "output-dir";
    public static final String PROP_MAX_ERRORS = "max-errors";

    public static final int DEFAULT_MAX_ERRORS = 10;

    @Parameter(property = "propertyFile")
    private String propertyFile;
    @Parameter(property = "propertyFileWillOverride")
    private boolean propertyFileWillOverride;

    @Parameter(property = PROP_URL)
    private String url;
    @Parameter(property = PROP_USERNAME)
    private String username;
    @Parameter(property = PROP_PASSWORD)
    private String password;
    @Parameter(property = PROP_SOURCE)
    private String sourceDir = "./src/test/regresql/source";
    @Parameter(property = PROP_EXPECTED)
    private String expectedDir = "./src/test/regresql/expected";
    @Parameter(property = PROP_OUTPUT)
    private String outputDir = "./target/regresql";
    @Parameter(property = PROP_MAX_ERRORS, defaultValue = "0")
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
            if (!service.runAllTests(output, maxErrors)) {
                throw new MojoFailureException("Some tests failed, see logs for more info");
            }
        } catch (MojoFailureException e) {
            throw e;
        } catch (Exception e) {
            throw new MojoFailureException("Test failed", e);
        }
    }

    private void checkParams() throws MojoFailureException {
        getLog().info("Verifying parameters");

        loadPropertiesFile();

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

        if (maxErrors <= 0)
            maxErrors = DEFAULT_MAX_ERRORS;
    }

    private void loadPropertiesFile() throws MojoFailureException {
        Properties props = new Properties();
        if (isBlank(propertyFile))
            return;

        File f = new File(propertyFile);
        try (InputStream is = new FileInputStream(f)) {
            props.load(is);
        } catch (FileNotFoundException e) {
            throw new MojoFailureException("Properties file '" + propertyFile + "' not found");
        } catch (IOException e) {
            throw new MojoFailureException("Error loading properties: " + e.getMessage(), e);
        }

        if (isBlank(url) && props.containsKey(PROP_URL) || propertyFileWillOverride)
            url = props.getProperty(PROP_URL);
        if (isBlank(username) && props.containsKey(PROP_USERNAME) || propertyFileWillOverride)
            username = props.getProperty(PROP_USERNAME);
        if (isBlank(password) && props.containsKey(PROP_PASSWORD) || propertyFileWillOverride)
            password = props.getProperty(PROP_PASSWORD);
        if (isBlank(sourceDir) && props.containsKey(PROP_SOURCE) || propertyFileWillOverride)
            sourceDir = props.getProperty(PROP_SOURCE);
        if (isBlank(expectedDir) && props.containsKey(PROP_EXPECTED) || propertyFileWillOverride)
            expectedDir = props.getProperty(PROP_EXPECTED);
        if (isBlank(outputDir) && props.containsKey(PROP_OUTPUT) || propertyFileWillOverride)
            outputDir = props.getProperty(PROP_OUTPUT);
        if (isBlank(outputDir) && props.containsKey(PROP_OUTPUT) || propertyFileWillOverride)
            outputDir = props.getProperty(PROP_OUTPUT);

        if (maxErrors == 0 && props.containsKey(PROP_OUTPUT) || propertyFileWillOverride)
            maxErrors = Integer.parseInt(props.getProperty(PROP_MAX_ERRORS));
    }
}
