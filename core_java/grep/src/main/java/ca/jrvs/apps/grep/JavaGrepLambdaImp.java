package ca.jrvs.apps.grep;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class JavaGrepLambdaImp extends JavaGrepImp{

    @Override
    public Stream<File> listFiles(String rootDir) {
        File currentDirectory = new File(rootDir);
        File[] currentFiles =  Optional.ofNullable(currentDirectory.listFiles()).orElse(new File[0]);

        return Arrays.stream(currentFiles)
                .flatMap(file -> file.isDirectory()
                        ? listFiles(file.getPath())
                        : Stream.of(file))
                .filter(file -> !file.isDirectory() && isTxtFile(file.getName()));
    }

    @Override
    public Stream<String> readLines(File inputFiles) throws IOException {
        return Files.lines(inputFiles.toPath()).filter(this::containsPattern);
    }

}
