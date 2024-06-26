package ca.jrvs.apps.grep;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.apache.log4j.BasicConfigurator;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JavaGrepImp implements JavaGrep{

    final Logger logger = LoggerFactory.getLogger(JavaGrepImp.class);

    private String regex;
    private String rootPath;
    private String outFile;

    public static void main(String[] args) {
        if(args.length != 3){
            throw new IllegalArgumentException("USAGE : JavaGrep regex rootPath outFile");
        }

        BasicConfigurator.configure();

        JavaGrepImp javaGrepImp = new JavaGrepImp();
        javaGrepImp.setRegex(args[0]);
        javaGrepImp.setRootPath(args[1]);
        javaGrepImp.setOutFile(args[2]);

        try{
            javaGrepImp.process();
        }catch(IOException exception){
            javaGrepImp.logger.error("Error : Unable to process", exception);
        }

    }

    @Override
    public void process() throws IOException {

    }

    @Override
    public List<File> listFiles(String rootDir) {
        return List.of();
    }

    @Override
    public List<String> readLines(File inputFiles) {
        return List.of();
    }

    @Override
    public boolean containsPattern(String line) {
        return false;
    }

    @Override
    public void writeToFile(List<String> lines) throws IOException {

    }

    @Override
    public String getRootPath() {
        return "";
    }

    @Override
    public void setRootPath(String rootPath) {

    }

    @Override
    public String getRegex() {
        return "";
    }

    @Override
    public void setRegex(String regex) {

    }

    @Override
    public String getOutFile() {
        return "";
    }

    @Override
    public void setOutFile(String outFile) {

    }
}
