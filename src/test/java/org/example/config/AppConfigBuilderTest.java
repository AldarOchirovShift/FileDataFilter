package org.example.config;

import org.example.exception.ConfigurationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class AppConfigBuilderTest {
    private final AppConfigBuilder builder = new AppConfigBuilder();

    @Test
    void buildFromRaw_whenShortStatsTrue_shouldSetCorrectFlags() {
        var args = new RawCommandLineArgs();
        args.setShortStats(true);
        args.setInputFiles(List.of("in.txt"));
        var config = builder.buildFromRaw(args);

        assertAll(
                () -> assertTrue(config.isShortStats()),
                () -> assertFalse(config.isFullStats())
        );
    }

    @Test
    void buildFromRaw_whenFullStatsTrue_shouldSetCorrectFlags() {
        var args = new RawCommandLineArgs();
        args.setFullStats(true);
        args.setInputFiles(List.of("in.txt"));
        var config = builder.buildFromRaw(args);

        assertAll(
                () -> assertFalse(config.isShortStats()),
                () -> assertTrue(config.isFullStats())
        );
    }

    @Test
    void buildFromRaw_whenAppendModeTrue_shouldSetAppendMode() {
        var args = new RawCommandLineArgs();
        args.setAppendMode(true);
        args.setInputFiles(List.of("in.txt"));

        var config = builder.buildFromRaw(args);
        assertTrue(config.isAppendMode());
    }

    @Test
    void buildFromRaw_whenOutputPathProvided_shouldSetCorrectPath() {
        var args = new RawCommandLineArgs();
        args.setShortStats(true);
        args.setOutputPath("custom/path");
        args.setInputFiles(List.of("in.txt"));

        var config = builder.buildFromRaw(args);
        assertEquals("custom/path", config.getOutputPath());
    }

    @Test
    void buildFromRaw_whenOutputPathNull_shouldUseDefaultPath() {
        var args = new RawCommandLineArgs();
        args.setShortStats(true);
        args.setInputFiles(List.of("in.txt"));

        var config = builder.buildFromRaw(args);
        assertEquals("", config.getOutputPath());
    }

    @Test
    void buildFromRaw_whenFilePrefixProvided_shouldSetPrefix() {
        var args = new RawCommandLineArgs();
        args.setShortStats(true);
        args.setFilePrefix("prefix_");
        args.setInputFiles(List.of("in.txt"));

        var config = builder.buildFromRaw(args);
        assertEquals("prefix_", config.getFilePrefix());
    }

    @Test
    void buildFromRaw_whenFilePrefixNull_shouldUseEmptyString() {
        var args = new RawCommandLineArgs();
        args.setShortStats(true);
        args.setFilePrefix(null);
        args.setInputFiles(List.of("in.txt"));

        var config = builder.buildFromRaw(args);
        assertEquals("", config.getFilePrefix());
    }

    @ParameterizedTest
    @MethodSource("provideInputFilesCases")
    void buildFromRaw_whenInputFilesProvided_shouldSetFiles(List<String> inputFiles) {
        var args = new RawCommandLineArgs();
        args.setShortStats(true);
        args.setInputFiles(inputFiles);

        var config = builder.buildFromRaw(args);
        assertEquals(inputFiles, config.getInputFiles());
    }

    private static Stream<Arguments> provideInputFilesCases() {
        return Stream.of(
                Arguments.of(List.of("in1.txt")),
                Arguments.of(List.of("in1.txt", "in2.txt")),
                Arguments.of(List.of("path with spaces/in1.txt"))
        );
    }

    @Test
    void buildFromRaw_whenNoInputFiles_shouldThrow() {
        var args = new RawCommandLineArgs();
        args.setShortStats(true);

        assertThrows(ConfigurationException.class, () -> builder.buildFromRaw(args));
    }
}
