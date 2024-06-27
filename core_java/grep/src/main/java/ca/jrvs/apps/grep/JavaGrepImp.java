package ca.jrvs.apps.grep;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaGrepImp implements JavaGrep{

    final Logger logger = LoggerFactory.getLogger(JavaGrepImp.class);

    private String regex;
    private String rootPath;
    private String outFile;

    /**
     * Process the read of .txt files under a specific directory,
     * match the lines with a specific regex pattern
     * and write all the matched lines inside a new file that is automatically created, if it doesn't exist.
     * @throws IOException
     */
    @Override
    public void process() throws IOException {
        Stream<File> filesUnderRootDir = listFiles(rootPath);
        List<String> matchedLines = filesUnderRootDir.flatMap(this::readLinesSafe).collect(Collectors.toList());
        writeToFile(matchedLines);
    }

    @Override
    public Stream<File> listFiles(String rootDir) {
        return listFilesRecursively(rootDir, new ArrayList<>()).stream();
    }

    /**
     * Recursively fills a list with all .txt files found in the specified
     * root directory and its subdirectories.
     * @param rootDir the root directory path to start the search
     * @param files the list to be filled with .txt files
     * @return the list of .txt files
     */
    public List<File> listFilesRecursively(String rootDir, List<File> files) {
        File currentDirectory = new File(rootDir);
        File[] currentFiles =  Optional.ofNullable(currentDirectory.listFiles()).orElse(new File[0]);

        for(File currentFile : currentFiles){
            if(currentFile.isDirectory()){
                files = listFilesRecursively(currentFile.getPath(), files);
            }else{
                if(isTxtFile(currentFile.getName()))
                    files.add(currentFile);
            }
        }

        return files;
    }

    /**
     * Reads all the lines in the given file and retains only those
     * that match the specified regex pattern.
     * @param inputFiles the file to read lines from
     * @return a stream of lines
     * @throws IOException
     */
    @Override
    public Stream<String> readLines(File inputFiles) throws IOException {
        List<String> lines = new ArrayList<>();
        Scanner scanner = new Scanner(inputFiles);
        while(scanner.hasNextLine()){
            String currentLine = scanner.nextLine();
            if(containsPattern(currentLine))
                lines.add(currentLine);
        }
        scanner.close();
        return lines.stream();
    }

    private Stream<String> readLinesSafe(File inputFiles){
        try {
            return readLines(inputFiles);
        }catch (IOException ex){
            throw new RuntimeException("Error : Unable to process", ex);
        }
    }

    @Override
    public boolean containsPattern(String line) {
        return Pattern.matches(regex, line);
    }

    protected boolean isTxtFile(String fileName){
        return fileName.toLowerCase().endsWith(".txt");
    }

    @Override
    public void writeToFile(List<String> lines) throws IOException {
        FileWriter writer = new FileWriter(outFile);
        for(String line : lines){
            writer.write(line.trim()+'\n');
        }
        writer.close();
    }

    @Override
    public String getRootPath() {
        return rootPath;
    }

    @Override
    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public String getRegex() {
        return regex;
    }

    @Override
    public void setRegex(String regex) {
        this.regex = regex;
    }

    @Override
    public String getOutFile() {
        return outFile;
    }

    @Override
    public void setOutFile(String outFile) {
        this.outFile = outFile;
    }
}
