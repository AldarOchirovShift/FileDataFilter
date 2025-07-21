package org.example.service;

import org.example.statistics.CompositeStatistics;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FileWriterTest {
    @TempDir
    Path tempDir;

    private PathBuilder pathBuilder;
    private FileWriter fileWriter;
    private CompositeStatistics statistics;

    @BeforeEach
    void setUp() {
        pathBuilder = new PathBuilder();
        statistics = new CompositeStatistics(true);

        ExecutorService mockExecutor = mock(ExecutorService.class);
        when(mockExecutor.submit(any(Runnable.class))).thenAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return CompletableFuture.completedFuture(null);
        });

        Map<String, ExecutorService> mockExecutors = Map.of(
                "integers.txt", mockExecutor,
                "floats.txt", mockExecutor,
                "strings.txt", mockExecutor
        );

        fileWriter = new FileWriter(pathBuilder, tempDir.toString(), "test_", false, statistics) {
            @Override
            protected Map<String, ExecutorService> createFileExecutors() {
                return mockExecutors;
            }
        };
    }

    @AfterEach
    void tearDown() {
        if (fileWriter != null) {
            fileWriter.close();
        }
    }

    @Test
    void addInteger_whenSingleValue_thenWritesToIntegerFile() throws Exception {
        fileWriter.addInteger(42);
        fileWriter.close();

        var outputFile = tempDir.resolve("test_integers.txt");
        assertTrue(Files.exists(outputFile));
        assertEquals(List.of("42"), Files.readAllLines(outputFile));
    }

    @Test
    void addFloat_whenScientificNotation_thenWritesCorrectFormat() throws Exception {
        fileWriter.addFloat(1.23e-5);
        fileWriter.close();

        var outputFile = tempDir.resolve("test_floats.txt");
        assertTrue(Files.exists(outputFile));
        assertEquals(List.of("1.23E-5"), Files.readAllLines(outputFile));
    }

    @Test
    void addString_whenUnicodeCharacters_thenWritesCorrectly() throws Exception {
        fileWriter.addString("test");
        fileWriter.close();

        var outputFile = tempDir.resolve("test_strings.txt");
        assertTrue(Files.exists(outputFile));
        assertEquals(List.of("test"), Files.readAllLines(outputFile));
    }

    @Test
    void addInteger_whenAppendMode_thenPreservesExistingContent() throws Exception {
        var dir = tempDir.toString();
        var prefix = "app_";
        var outputFile = tempDir.resolve(prefix + "integers.txt");

        var writer1 = new FileWriter(pathBuilder, dir, prefix, true, statistics);
        writer1.addInteger(1);
        writer1.close();

        var attempts = 0;
        while (attempts++ < 10 && !Files.exists(outputFile)) {
            Thread.sleep(100);
        }

        assertTrue(Files.exists(outputFile), "File should exist after first write");
        assertEquals(List.of("1"), Files.readAllLines(outputFile));

        var writer2 = new FileWriter(pathBuilder, dir, prefix, true, statistics);
        writer2.addInteger(2);
        writer2.close();

        attempts = 0;
        while (attempts++ < 10 && Files.readAllLines(outputFile).size() < 2) {
            Thread.sleep(100);
        }

        assertEquals(List.of("1", "2"), Files.readAllLines(outputFile));
    }

    @Test
    void addInteger_whenMultipleValues_thenWritesAllToFile() throws Exception {
        fileWriter.addInteger(1);
        fileWriter.addInteger(2);
        fileWriter.addInteger(3);
        fileWriter.close();

        var outputFile = tempDir.resolve("test_integers.txt");
        assertEquals(List.of("1", "2", "3"), Files.readAllLines(outputFile));
    }

    @Test
    void addInteger_whenBatchLimitReached_thenAutoFlush() throws Exception {
        var realBatch = new DataBatch<Long>();
        var mockBatch = spy(realBatch);

        when(mockBatch.isFull())
                .thenReturn(false)
                .thenReturn(false)
                .thenReturn(true);

        var batchField = FileWriter.class.getDeclaredField("integerBatch");
        batchField.setAccessible(true);
        batchField.set(fileWriter, mockBatch);

        fileWriter.addInteger(1);
        fileWriter.addInteger(2);
        fileWriter.addInteger(3);
        fileWriter.close();

        var outputFile = tempDir.resolve("test_integers.txt");
        assertEquals(List.of("1", "2", "3"), Files.readAllLines(outputFile));
    }
}
