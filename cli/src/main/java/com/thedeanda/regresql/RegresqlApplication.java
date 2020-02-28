package com.thedeanda.regresql;

import com.google.common.collect.ImmutableSet;
import com.thedeanda.regresql.datasource.DataSource;
import com.thedeanda.regresql.ui.RegresqlUiFrame;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.io.*;
import java.sql.*;
import java.util.Properties;
import java.util.Set;

@Slf4j
public class RegresqlApplication {
	private static final int DEFAULT_MAX_ERRORS = 10;
	private static final String OPTION_MAX_ERRORS = "maxErrors";
	private static final String OPTION_COMMAND = "command";
	private static final String OPTION_SOURCE = "source";
	private static final String OPTION_EXPECTED = "expected";
	private static final String OPTION_OUTPUT = "output";
	private static final String OPTION_PROPERTIES = "properties";
	private static final String OPTION_HELP = "help";
	private static final String OPTION_COMMAND_UPDATE = "update";
	private static final String OPTION_COMMAND_TEST = "test";
	private static final String OPTION_COMMAND_LIST = "list";
	private static final String OPTION_COMMAND_UI = "ui";

	private static final Set<String> VALID_COMMANDS = ImmutableSet.of(
			OPTION_COMMAND_UPDATE,
			OPTION_COMMAND_TEST,
			OPTION_COMMAND_LIST,
			OPTION_COMMAND_UI
	);


	public static void main(String[] args) throws Exception {
		Options options = new Options();

		options.addOption("h", OPTION_HELP, false, "show command line help");
		options.addOption("s", OPTION_SOURCE, true, "source folder, default is './src'");
		options.addOption("e", OPTION_EXPECTED, true, "expected folder, default is './expected'");
		options.addOption("o", OPTION_OUTPUT, true, "output folder, default is './output'");
		options.addOption("p", OPTION_PROPERTIES, true, "properties file to read db settings from");
		options.addOption("m", OPTION_MAX_ERRORS, true, "max number of errors per test, defaults to " + DEFAULT_MAX_ERRORS);

		options.addOption(Option.builder()
				.hasArg(true)
				.argName(OPTION_COMMAND)
				//.required(true)
				.longOpt(OPTION_COMMAND)
				.desc(OPTION_COMMAND + ": ["
						+ OPTION_COMMAND_UPDATE + ", "
						+ OPTION_COMMAND_TEST + ", "
						+ OPTION_COMMAND_LIST + ", "
						+ OPTION_COMMAND_UI + "]")
				.build());


		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse( options, args);

		if (!checkParams(cmd)) {
			showHelp(options);
			return;
		}


		int maxErrors = DEFAULT_MAX_ERRORS;
		if (cmd.hasOption(OPTION_MAX_ERRORS)) {
			maxErrors = Integer.parseInt(cmd.getOptionValue(OPTION_MAX_ERRORS));
		}
		File source = new File(cmd.getOptionValue(OPTION_SOURCE));
		File expected = new File(cmd.getOptionValue(OPTION_EXPECTED));
		File output = null;
		if (cmd.hasOption(OPTION_OUTPUT)) {
			output = new File(cmd.getOptionValue(OPTION_OUTPUT));
		}
		String command = cmd.getOptionValue(OPTION_COMMAND);
		Properties properties = readProperties(cmd);
		DataSource dataSource = new DataSource(properties);

		RegresqlService service = new RegresqlService(dataSource, source, expected);

		switch (command) {
			case OPTION_COMMAND_TEST:
				boolean passed = service.runAllTests(output, maxErrors);
				if (!passed) {
					System.exit(1);
				}
				break;
			case OPTION_COMMAND_UPDATE:
				service.updateAllTests();
				break;
			case OPTION_COMMAND_LIST:
				service.listTests();
				break;
			case OPTION_COMMAND_UI:
				showUi(service);
				break;
			default:
				showHelp(options);
		}
	}

	private static void showUi(RegresqlService service) {
		RegresqlUiFrame frame = new RegresqlUiFrame(service);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	private static boolean checkParams(CommandLine cmd) {
		if (!cmd.hasOption(OPTION_COMMAND)) {
			log.warn("missing command");
			return false;
		}

		String command = cmd.getOptionValue(OPTION_COMMAND);
		if (!VALID_COMMANDS.contains(command)) {
			log.warn("invalid command: {}", command);
			return false;
		}

		if (!cmd.hasOption(OPTION_SOURCE)) {
			log.warn("missing source");
			return false;
		}

		if (!cmd.hasOption(OPTION_EXPECTED)) {
			log.warn("missing expected");
			return false;
		}

		if (!cmd.hasOption(OPTION_OUTPUT) && (OPTION_COMMAND_TEST.equals(command) || OPTION_COMMAND_UI.equals(command))) {
			log.warn("missing output");
			return false;
		}

		if (cmd.hasOption(OPTION_HELP)) {
			return false;
		}

		return true;
	}

	private static Properties readProperties(CommandLine cmd) throws IOException {
		Properties props = new Properties();
		if (cmd.hasOption(OPTION_PROPERTIES)) {
			String fn = cmd.getOptionValue(OPTION_PROPERTIES);
			try (InputStream reader = new FileInputStream(fn)) {
				props.load(reader);
			}
		}

		override(props, DataSource.ENVPROP_DBURL, DataSource.PROP_DBURL);
		override(props, DataSource.ENVPROP_DBUSER, DataSource.PROP_DBUSER);
		override(props, DataSource.ENVPROP_DBPASS, DataSource.PROP_DBPASS);

		return props;
	}

	private static void override(Properties props, String envProp, String prop) {
		String envPropName = toEnvProperty(envProp);
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
