package org.example.service;

import org.example.model.FileData;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FileProcessor {
    private static final Pattern INTEGER_PATTERN = Pattern.compile("^-?(0|[1-9][0-9]{0,18}(_[0-9]{3})*)$");
    private static final Pattern FLOAT_PATTERN = Pattern.compile(
            "^-?(0|[1-9]\\d*)[.,]\\d+([eE][-+]?\\d+)?$|" +
                    "^-?Infinity$|" +
                    "^NaN$",
            Pattern.CASE_INSENSITIVE
    );
    private final PathBuilder pathBuilder;
    private final List<Long> integers = new ArrayList<>();
    private final List<Double> floats = new ArrayList<>();
    private final List<String> strings = new ArrayList<>();

    public FileProcessor(PathBuilder pathBuilder) {
        this.pathBuilder = pathBuilder;
    }

    public FileData processFile(String inputFile, String outputDir) throws IOException {
        var inputPath = pathBuilder.build(inputFile);
        try (var reader = Files.newBufferedReader(inputPath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                processLine(line.trim());
            }
        }

        return FileData.builder()
                .integers(integers)
                .floats(floats)
                .strings(strings)
                .build();
    }

    private void processLine(String line) {
        if (line.isEmpty()) return;

        if (isInteger(line)) {
            integers.add(parseLong(line));
        } else if (isFloat(line)) {
            floats.add(parseDouble(line));
        } else {
            strings.add(line);
        }
    }

    private boolean isInteger(String s) {
        return INTEGER_PATTERN.matcher(s).matches();
    }

    private boolean isFloat(String s) {
        return FLOAT_PATTERN.matcher(s).matches();
    }

    private long parseLong(String s) {
        return Long.parseLong(s.replace("_", ""));
    }

    private double parseDouble(String s) {
        s = s.replace(",", ".");
        if (s.equalsIgnoreCase("Infinity")) {
            return Double.POSITIVE_INFINITY;
        } else if (s.equalsIgnoreCase("-Infinity")) {
            return Double.NEGATIVE_INFINITY;
        } else if (s.equalsIgnoreCase("NaN")) {
            return Double.NaN;
        }
        return Double.parseDouble(s);
    }
}
