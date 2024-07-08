package ca.jrvs.apps.grep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.log4j.BasicConfigurator;

import java.io.IOException;

public class Program {
    final Logger logger = LoggerFactory.getLogger(Program.class);

    public static void main(String[] args) {
        if(args.length != 3){
            throw new IllegalArgumentException("USAGE : JavaGrep regex rootPath outFile");
        }

        BasicConfigurator.configure();

        JavaGrepImp javaGrepImp = new JavaGrepLambdaImp();
        javaGrepImp.setRegex(args[0]);
        javaGrepImp.setRootPath(args[1]);
        javaGrepImp.setOutFile(args[2]);

        try{
            javaGrepImp.process();
        }catch(IOException exception){
            javaGrepImp.logger.error("Error : Unable to process", exception);
        }

    }
}
