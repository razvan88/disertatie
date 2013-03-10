package analytics.database;


import java.sql.*;

import static analytics.database.DBCredentials.*;


/**
 * Singleton class used for basic database operations
 * 
 * @author Razvan Nedelcu
 */
public class DBBasicOperations {
	
	static {
		dbConnection = new DBBasicOperations();
	}
	
	private static DBBasicOperations dbConnection;
	private static Connection connection;
	
	private DBBasicOperations() {}
	
	public static DBBasicOperations getInstance() {
		return dbConnection;
	}
	
	public void openConnection() {
        try {
			Class.forName(DRIVER).newInstance();
            connection = DriverManager.getConnection(LINK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
