package org.example.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileProcessorTest {

    @Mock
    private PathBuilder pathBuilder;

    @Mock
    private FileWriter fileWriter;

    @InjectMocks
    private FileProcessor processor;

    @Test
    void processFile_whenMixedContent_shouldProcessCorrectly() throws IOException {
        Path testFile = Files.createTempFile("test", ".txt");
        Files.write(testFile, List.of(
                "123",
                "3.14",
                "-Infinity",
                "1_000",
                "plain text",
                ""
        ));

        when(pathBuilder.buildInput("data.txt")).thenReturn(testFile);
        processor.processFile("data.txt");

        var inOrder = inOrder(fileWriter);
        inOrder.verify(fileWriter).addInteger(123L);
        inOrder.verify(fileWriter).addFloat(3.14);
        inOrder.verify(fileWriter).addFloat(Double.NEGATIVE_INFINITY);
        inOrder.verify(fileWriter).addInteger(1000L);
        inOrder.verify(fileWriter).addString("plain text");

        verify(fileWriter, never()).addString("");
    }

    @Test
    void processFile_whenEmptyFile_shouldNotAddAnyItems() throws IOException {
        var testFile = Files.createTempFile("empty", ".txt");
        when(pathBuilder.buildInput("empty.txt")).thenReturn(testFile);

        processor.processFile("empty.txt");

        verify(fileWriter, never()).addInteger(anyLong());
        verify(fileWriter, never()).addFloat(anyDouble());
        verify(fileWriter, never()).addString(anyString());
    }

    @Test
    void processFile_whenFileNotFound_shouldThrow() {
        var missingPath = Path.of("nonexistent_file.txt");
        when(pathBuilder.buildInput("missing.txt")).thenReturn(missingPath);
        assertThrows(IOException.class, () ->
                processor.processFile("missing.txt"));

        verify(fileWriter, never()).addInteger(anyLong());
    }

    @Test
    void processFile_whenSpecialNumberFormats_shouldProcessCorrectly() throws IOException {
        Path testFile = Files.createTempFile("special", ".txt");
        Files.write(testFile, List.of(
                "1,23",
                "1.23e5",
                "NaN"
        ));

        when(pathBuilder.buildInput("special.txt")).thenReturn(testFile);
        processor.processFile("special.txt");

        verify(fileWriter).addFloat(1.23);
        verify(fileWriter).addFloat(123000.0);
        verify(fileWriter).addFloat(Double.NaN);
    }

    @Test
    void processFile_whenLargeFile_shouldProcessAllLines() throws IOException {
        var testFile = Files.createTempFile("large", ".txt");
        List<String> lines = new ArrayList<>();
        for (var i = 0; i < 10000; i++) {
            lines.add(String.valueOf(i));
        }
        Files.write(testFile, lines);

        when(pathBuilder.buildInput("large.txt")).thenReturn(testFile);
        processor.processFile("large.txt");

        verify(fileWriter, times(10000)).addInteger(anyLong());
    }

    @Test
    void processFile_whenEdgeCaseNumbers_shouldProcessCorrectly() throws IOException {
        var testFile = Files.createTempFile("edge", ".txt");
        Files.write(testFile, List.of(
                String.valueOf(Long.MAX_VALUE),
                String.valueOf(Long.MIN_VALUE),
                "1.7976931348623157E308",
                "4.9E-324"
        ));

        when(pathBuilder.buildInput("edge.txt")).thenReturn(testFile);
        processor.processFile("edge.txt");

        verify(fileWriter).addInteger(Long.MAX_VALUE);
        verify(fileWriter).addInteger(Long.MIN_VALUE);
        verify(fileWriter).addFloat(Double.MAX_VALUE);
        verify(fileWriter).addFloat(Double.MIN_VALUE);
    }
}