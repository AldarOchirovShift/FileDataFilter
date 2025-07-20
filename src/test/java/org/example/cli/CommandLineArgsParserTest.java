package org.example.cli;

import org.example.exception.ConfigurationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class CommandLineArgsParserTest {
    private final CommandLineArgsParser parser = new CommandLineArgsParser();

    @ParameterizedTest
    @MethodSource("provideStatsFlagsCases")
    void parse_whenStatsFlagProvided_thenShouldSetCorrectFlags(String[] args, boolean expectedShort, boolean expectedFull) {
        var result = parser.parse(args);
        assertEquals(expectedShort, result.isShortStats());
        assertEquals(expectedFull, result.isFullStats());
    }

    private static Stream<Arguments> provideStatsFlagsCases() {
        return Stream.of(
                Arguments.of(new String[]{"in.txt"}, true, false),
                Arguments.of(new String[]{"-s", "in.txt"}, true, false),
                Arguments.of(new String[]{"-s", "-s", "in.txt"}, true, false),
                Arguments.of(new String[]{"-f", "in.txt"}, false, true),
                Arguments.of(new String[]{"-f", "-f", "in.txt"}, false, true),
                Arguments.of(new String[]{"-s", "-a", "in.txt"}, true, false),
                Arguments.of(new String[]{"-f", "-a", "in.txt"}, false, true)
        );
    }

    @ParameterizedTest
    @MethodSource("provideAppendFlagCases")
    void parse_whenAppendFlagProvided_thenShouldSetAppendMode(String[] args, boolean expectedAppend) {
        var result = parser.parse(args);
        assertEquals(expectedAppend, result.isAppendMode());
    }

    private static Stream<Arguments> provideAppendFlagCases() {
        return Stream.of(
                Arguments.of(new String[]{"in.txt"}, false),
                Arguments.of(new String[]{"-a", "in.txt"}, true),
                Arguments.of(new String[]{"-s", "-a", "in.txt"}, true),
                Arguments.of(new String[]{"-a", "-s", "in.txt"}, true),
                Arguments.of(new String[]{"-a", "-a", "in.txt"}, true)
        );
    }

    @ParameterizedTest
    @MethodSource("provideOutputPathCases")
    void parse_whenOutputPathProvided_thenShouldSetPath(String[] args, String expectedPath) {
        var result = parser.parse(args);
        assertEquals(expectedPath, result.getOutputPath());
    }

    private static Stream<Arguments> provideOutputPathCases() {
        return Stream.of(
                Arguments.of(new String[]{"-o", "out.txt", "in.txt"}, "out.txt"),
                Arguments.of(new String[]{"-o", "test dir/out.txt", "in.txt"}, "test dir/out.txt"),
                Arguments.of(new String[]{"-s", "-a", "-o", "test dir/out.txt", "in.txt"}, "test dir/out.txt"),
                Arguments.of(new String[]{"in.txt"}, null)
        );
    }

    @Test
    void parse_whenOutputPathMissing_thenShouldThrow() {
        assertThrows(ConfigurationException.class, () -> parser.parse(new String[]{"-o"}));
    }

    @Test
    void parse_whenFilePrefixProvided_thenShouldSetPrefix() {
        var result = parser.parse(new String[]{"-p", "prefix_", "in.txt"});
        assertEquals("prefix_", result.getFilePrefix());
    }

    @Test
    void parse_whenFilePrefixMissing_thenShouldThrow() {
        assertThrows(ConfigurationException.class, () -> parser.parse(new String[]{"-p"}));
    }

    @ParameterizedTest
    @MethodSource("provideInputFilesCases")
    void parse_whenInputFilesProvided_thenShouldAddThem(String[] args, List<String> expectedFiles) {
        var result = parser.parse(args);
        assertEquals(expectedFiles, result.getInputFiles());
    }

    private static Stream<Arguments> provideInputFilesCases() {
        return Stream.of(
                Arguments.of(new String[]{"in.txt"}, List.of("in.txt")),
                Arguments.of(new String[]{"-s", "in1.txt", "in2.txt"}, List.of("in1.txt", "in2.txt")),
                Arguments.of(new String[]{"-o", "path/out", "in1.txt"}, List.of("in1.txt")),
                Arguments.of(new String[]{"-p", "prefix_", "in1.txt"}, List.of("in1.txt")),
                Arguments.of(new String[]{"file with spaces.txt"}, List.of("file with spaces.txt"))
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidArgsCases")
    void parse_whenInvalidArgs_thenShouldThrow(String[] args) {
        assertThrows(ConfigurationException.class, () -> parser.parse(args));
    }

    private static Stream<Arguments> provideInvalidArgsCases() {
        return Stream.of(
                Arguments.of((Object) new String[]{"-z"}),
                Arguments.of((Object) new String[]{"-o"}),
                Arguments.of((Object) new String[]{"-p"})
        );
    }

    @Test
    void parse_whenNoFlags_thenShouldSetDefaults() {
        var result = parser.parse(new String[]{"in.txt"});
        assertAll(
                () -> assertTrue(result.isShortStats()),
                () -> assertFalse(result.isFullStats()),
                () -> assertFalse(result.isAppendMode()),
                () -> assertNull(result.getOutputPath()),
                () -> assertNull(result.getFilePrefix()),
                () -> assertEquals(List.of("in.txt"), result.getInputFiles())
        );
    }
}
