package org.example;

import org.example.cli.CommandLineArgsParser;
import org.example.config.AppConfigBuilder;
import org.example.service.FileProcessor;
import org.example.service.FileWriter;
import org.example.service.PathBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Runner {
    private final static Logger LOGGER = LoggerFactory.getLogger(Runner.class);

    public static void main(String[] args) {
        try {
            var parser = new CommandLineArgsParser();
            var rawArgs = parser.parse(args);

            var configBuilder = new AppConfigBuilder();
            var config = configBuilder.buildFromRaw(rawArgs);

            LOGGER.info("Application config: {}", config);

            var pathBuilder = new PathBuilder();
            var fileWriter = new FileWriter(pathBuilder, config.getOutputPath(), config.getFilePrefix(), config.isAppendMode());
            var fileProcessor = new FileProcessor(pathBuilder, fileWriter);
            fileProcessor.processFile(config.getInputFiles().getFirst());
        } catch (IllegalArgumentException e) {
            LOGGER.error("Configuration error: {}", e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            LOGGER.error("Process error: {}", e.getMessage());
            System.exit(1);
        }
    }
}
