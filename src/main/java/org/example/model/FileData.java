package org.example.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class FileData {
    private final List<Long> integers;
    private final List<Double> floats;
    private final List<String> strings;
}
