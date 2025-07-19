package org.example.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.FileSystems;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PathBuilderTest {
    private final PathBuilder pathBuilder = new PathBuilder();

    @TempDir
    Path tempDir;

    @Test
    void buildInput_shouldResolveRelativePath() {
        var testFile = "test.txt";
        var result = pathBuilder.buildInput(testFile);

        assertTrue(result.isAbsolute());
        assertTrue(result.toString().contains("input"));
        assertTrue(result.toString().endsWith(testFile));
    }

    @Test
    void buildInput_shouldPreserveAbsolutePath() {
        var absolutePath = tempDir.resolve("absolute.txt").toAbsolutePath();
        var result = pathBuilder.buildInput(absolutePath.toString());

        assertTrue(result.isAbsolute());
        assertEquals(absolutePath, result);
    }

    @Test
    void buildOutput_shouldResolveRelativePath() {
        var testFile = "output.txt";
        var result = pathBuilder.buildOutput(testFile);

        assertTrue(result.isAbsolute());
        assertTrue(result.toString().contains("output"));
        assertTrue(result.toString().endsWith(testFile));
    }

    @Test
    void buildOutput_shouldPreserveAbsolutePath() {
        var absolutePath = tempDir.resolve("absolute_output.txt").toAbsolutePath();
        var result = pathBuilder.buildOutput(absolutePath.toString());

        assertTrue(result.isAbsolute());
        assertEquals(absolutePath, result);
    }

    @Test
    void build_shouldHandleNestedPaths() {
        var nestedPath = "subdir/file.txt";
        var result = pathBuilder.buildInput(nestedPath);

        assertTrue(result.toString().replace("\\", "/").contains("input" + "/subdir"));
        assertTrue(result.toString().endsWith("file.txt"));
    }

    @Test
    void buildInput_shouldHandleUnixStylePath() {
        var unixStylePath = "subdir/file.txt";
        var result = pathBuilder.buildInput(unixStylePath);

        assertTrue(result.isAbsolute());
        assertTrue(result.toString().replace("\\", "/").contains("input" + "/subdir/file.txt"));
    }

    @Test
    void buildInput_shouldHandleWindowsStylePath() {
        var windowsStylePath = "subdir\\file.txt";
        var result = pathBuilder.buildInput(windowsStylePath);

        assertTrue(result.isAbsolute());
        assertTrue(result.toString().contains("subdir" + FileSystems.getDefault().getSeparator() + "file.txt"));
        assertTrue(result.toString().replace("\\", "/").contains("input" + "/subdir/file.txt"));
    }

    @Test
    void buildInput_shouldHandlePathsWithSpaces() {
        var pathWithSpaces = "my docs/data file.csv";
        var result = pathBuilder.buildInput(pathWithSpaces);

        assertTrue(result.isAbsolute());
        assertTrue(result.toString().contains("my docs" + FileSystems.getDefault().getSeparator() + "data file.csv"));
    }

    @Test
    void buildInput_shouldHandlePathsWithSpecialChars() {
        var specialPath = "dir#name/файл@v1.2.txt";
        var result = pathBuilder.buildInput(specialPath);

        assertTrue(result.isAbsolute());
        assertTrue(result.toString().contains("dir#name"));
        assertTrue(result.toString().endsWith("файл@v1.2.txt"));
    }

    @Test
    void buildInput_shouldHandleVeryLongPaths() {
        var longPath = "a/".repeat(50) + "file.txt";
        var result = pathBuilder.buildInput(longPath);

        assertTrue(result.isAbsolute());
        assertTrue(result.toString().endsWith("file.txt"));
        assertTrue(result.toString().length() > longPath.length());
    }
}
