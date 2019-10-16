package com.thedeanda.regresql.maven;

import com.thedeanda.regresql.RegresqlService;
import com.thedeanda.regresql.datasource.DataSource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;
import java.util.Properties;

import static org.apache.commons.lang3.StringUtils.isBlank;


@Mojo(name = "update")
public class RegresqlUpdateMojo extends AbstractMojo {

    public static final String PROP_URL = "url";
    public static final String PROP_USERNAME = "username";
    public static final String PROP_PASSWORD = "password";
    public static final String PROP_SOURCE = "source-dir";
    public static final String PROP_EXPECTED = "expected-dir";

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


    File source;
    File expected;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Updating Regresql Tests");

        checkParams();

        prepDriver();

        DataSource dataSource = new DataSource(url, username, password);
        RegresqlService service = new RegresqlService(dataSource, source, expected);
        try {
            service.updateAllTests();
        } catch (MojoFailureException e) {
            throw e;
        } catch (Exception e) {
            throw new MojoFailureException("Failed to update tests", e);
        }
    }

    private void prepDriver() {
        // fixes postgres when run from maven, might need for other db's later.
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
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

        source = new File(sourceDir);
        expected = new File(expectedDir);

        if (!source.exists())
            throw new MojoFailureException("Source folder '" + sourceDir + "' does not exist");
        if (!expected.exists())
            throw new MojoFailureException("Expected folder '" + expectedDir + "' does not exist");

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

    }
}
