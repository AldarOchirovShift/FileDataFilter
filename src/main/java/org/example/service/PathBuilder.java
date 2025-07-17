package org.example.service;

import java.nio.file.Path;

public class PathBuilder {
    public Path build(String filename) {
        var path = Path.of(filename);
        path = !path.isAbsolute()
                ? Path.of(System.getProperty("user.dir")).resolve("texts").resolve(filename)
                : path;
        return path;
    }
}
