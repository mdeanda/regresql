package com.thedeanda.regresql;

import com.thedeanda.regresql.datasource.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.sql.*;
import java.util.Properties;

@Slf4j
public class RegresqlApplication {

	public static void main(String[] args) throws Exception {
		Options options = new Options();

		options.addOption("h", "help", false, "show command line help");
		options.addOption("s", "source", true, "source folder, default is './src'");
		options.addOption("e", "expected", true, "expected folder, default is './expected'");
		options.addOption("o", "output", true, "output folder, default is './output'");
		options.addOption("p", "properties", true, "properties file to read db settings from");

		options.addOption(Option.builder()
				.hasArg(true)
				.argName("command")
				//.required(true)
				.longOpt("command")
				.desc("command: [update, test]")
				.build());


		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse( options, args);

		if (!checkParams(cmd)) {
			showHelp(options);
			return;
		}


		File source = new File(cmd.getOptionValue("source"));
		File expected = new File(cmd.getOptionValue("expected"));
		File output = null;
		if (cmd.hasOption("output")) {
			output = new File(cmd.getOptionValue("output"));
		}
		String command = cmd.getOptionValue("command");
		Properties properties = readProperties(cmd);
		DataSource dataSource = new DataSource(properties);

		RegresqlService service = new RegresqlService(dataSource);

		switch (command) {
			case "test":
				boolean passed = service.runAllTests(source, expected, output);
				if (!passed) {
					System.exit(1);
				}
				break;
			case "update":
				service.updateAllTests(source, expected);
				break;
		}

	}

	private static boolean checkParams(CommandLine cmd) {
		if (!cmd.hasOption("command")) {
			log.warn("missing command");
			return false;
		}

		String command = cmd.getOptionValue("command");
		if (!"update".equals(command) && !"test".equals(command)) {
			log.warn("invalid command: {}", command);
			return false;
		}

		if (!cmd.hasOption("source")) {
			log.warn("missing source");
			return false;
		}

		if (!cmd.hasOption("expected")) {
			log.warn("missing expected");
			return false;
		}

		if (!cmd.hasOption("output") && "test".equals(command)) {
			log.warn("missing output");
			return false;
		}

		if (cmd.hasOption("help")) {
			return false;
		}

		return true;
	}

	private static Properties readProperties(CommandLine cmd) throws IOException {
		Properties props = new Properties();
		if (cmd.hasOption("properties")) {
			String fn = cmd.getOptionValue("properties");
			try (InputStream reader = new FileInputStream(fn)) {
				props.load(reader);
			}
		}

		override(props, DataSource.PROP_DBURL);
		override(props, DataSource.PROP_DBUSER);
		override(props, DataSource.PROP_DBPASS);

		return props;
	}

	private static void override(Properties props, String prop) {
		String envPropName = toEnvProperty(prop);
		String value = System.getenv(envPropName);
		if (!StringUtils.isBlank(value)) {
			props.setProperty(prop, value);
		}
	}

	private static String toEnvProperty(String propName) {
		return propName.toUpperCase().replaceAll("\\.", "_");
	}

	private static void showHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( " ", options );
	}
}
