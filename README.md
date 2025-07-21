# File Data Filter

Application for file processing with statistics support.

## Running:

1. Build with Maven:
```sh
mvn clean package
```

2. Run template:
```sh
java -jar target/file-data-filter-1.0-SNAPSHOT-jar-with-dependencies.jar (-s | -f) [-a] [-o <path>] [-p <prefix>] <input_files>...
```

3. Usage Example:
Process files with full statistics:
```sh
java -jar target/file-data-filter-1.0-SNAPSHOT-jar-with-dependencies.jar -f -a -o directory -p prefix_ in1.txt in2.txt
```

## Available options:
- -s — short statistics (default)
- -f — full statistics
- -a — append mode (default: disabled)
- -o \<path\> — output directory (default: current)
- -p \<prefix\> — filename prefix (default: no prefix)

### Important Notes:

1. The -s and -f options are mutually exclusive. When both are specified, the last one takes precedence. For example:
- -s -f → Full statistics will be used  
- -f -s → Short statistics will be used

2. At least one input file must be provided after all options. The application will exit with an error if no input files are specified.

3. The output directory (-o) will be created automatically (current/output) if it doesn't exist.

## Documentation:

[Generated Javadoc:](https://AldarOchirovShift.github.io/FileDataFilter/)

## Technologies:

- Java 21
- Maven
- JUnit 5 (for testing)
- Log4j 2
- Lombok
- Mockito
