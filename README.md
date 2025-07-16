# File Data Filter

Application for file processing with statistics support.

## Running

1. Build with Maven:
```sh
   mvn clean package
```

2. Run with arguments:
```sh
   java -jar util.jar (-s | -f) [-a] [-o <path>] [-p <prefix>] <input_files>...
```

### Available options:
- -s — short statistics (default)
- -f — full statistics
- -a — append mode
- -o \<path\> — output directory (default: current)
- -p \<prefix\> — filename prefix

## Documentation

[Generated Javadoc:](https://AldarOchirovShift.github.io/FileDataFilter/)

## Technologies

- Java 21
- Maven
- JUnit 5 (for testing)
- Log4j 2
- Lombok

## Usage Example

Process files with short statistics:
```sh
java -jar target/util.jar -s -o ./output input1.txt input2.txt
```
