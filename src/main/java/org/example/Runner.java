package org.example;

import org.example.cli.CommandLineArgsParser;
import org.example.config.AppConfigBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Runner {
    private final static Logger LOGGER = LoggerFactory.getLogger(Runner.class);

    public static void main(String[] args) {
        try {
            var parser = new CommandLineArgsParser();
            var rawArgs = parser.parse(args);

            var configBuilder = new AppConfigBuilder();
            var config = configBuilder.buildFromRaw(rawArgs);

            LOGGER.info("Application config: {}", config);

        } catch (IllegalArgumentException e) {
            LOGGER.error("Configuration error: {}", e.getMessage());
            System.exit(1);
        }
    }
}
