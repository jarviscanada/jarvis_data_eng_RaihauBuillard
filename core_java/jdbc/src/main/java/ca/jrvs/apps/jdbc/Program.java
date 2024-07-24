package ca.jrvs.apps.jdbc;

import ca.jrvs.apps.jdbc.controllers.StockQuoteController;
import ca.jrvs.apps.jdbc.helpers.QuoteHttpHelper;
import ca.jrvs.apps.jdbc.repositories.PositionDAO;
import ca.jrvs.apps.jdbc.repositories.QuoteDAO;
import ca.jrvs.apps.jdbc.services.PositionService;
import ca.jrvs.apps.jdbc.services.QuoteService;
import okhttp3.OkHttpClient;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Program {

    private static final Logger logger = LoggerFactory.getLogger(Program.class);
    private static final String PROPERTIES_FILE = "src/main/resources/app.properties";
    private static final String SECRET_API_FIELD = "SECRET_API_KEY";
    private static final String SERVER_FIELD = "SERVER";

    public static void main(String[] args) {
        BasicConfigurator.configure();
        Properties properties = new Properties();
        String url = getUrl(properties);
        setUpClassName(properties);

        try(Connection connection = DriverManager.getConnection(url, properties)){
            StockQuoteController stockQuoteController = initProgram(connection);
            stockQuoteController.initClient();
        } catch (SQLException e) {
            logger.error("Failed to connect to database : "+e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


    private static String getUrl(Properties properties) {
        try(FileInputStream file = new FileInputStream(PROPERTIES_FILE)){
            properties.load(file);
            String server = System.getenv(SERVER_FIELD);
            String databaseName = properties.getProperty("database");
            String port = properties.getProperty("port");
            return "jdbc:postgresql://"+server+":"+port+"/"+databaseName;
        }catch(IOException ioException){
            logger.error("Failed to read app.properties file : "+ioException.getMessage(), ioException);
            throw new RuntimeException(ioException.getMessage(), ioException);
        }
    }

    private static void setUpClassName(Properties properties) {
        try {
            Class.forName(properties.getProperty("db-class"));
        } catch (ClassNotFoundException classNotFoundException) {
            logger.error(classNotFoundException.getMessage(), classNotFoundException);
        }
    }

    private static StockQuoteController initProgram(Connection connection) {
        QuoteDAO quoteDAO = new QuoteDAO(connection);
        PositionDAO positionDAO = new PositionDAO(connection);
        QuoteHttpHelper helper = new QuoteHttpHelper(System.getenv(SECRET_API_FIELD), new OkHttpClient());
        QuoteService quoteService = new QuoteService(quoteDAO, helper);
        PositionService positionService = new PositionService(positionDAO);
        return new StockQuoteController(quoteService, positionService);
    }

}
