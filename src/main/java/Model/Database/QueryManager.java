package Model.Database;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

public class QueryManager {


    static Properties props;

    //Retrieve the queries from the property file
    //while ensuring the properties variable is a singleton
    private static Properties getQueries() throws SQLException {
        try {
            InputStream inStream = new FileInputStream("src/main/java/Model/Database/queries.properties");
            if (inStream == null) {
                throw new SQLException("Unable to load query file");
            }
            if (props == null) {
                props = new Properties();
                try {
                    props.load(inStream);
                } catch (IOException e) {
                    throw new SQLException("Unable to load query file");
                }
            }
            return props;
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }


    //Returns an sql query based on the properties within the property file
    public static String getQuery(String query) throws SQLException{
        return getQueries().getProperty(query);
    }
}
