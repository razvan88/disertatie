package analytics.database;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
	
	public List<String> getAvailableProducts() {
		List<String> products = new ArrayList<String>();
		try{
			Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = statement.executeQuery("SELECT " + DBCredentials.PROD_NAME + " FROM " + DBCredentials.TABLE_PROD);
			
			while(rs.next())
				products.add(rs.getString(DBCredentials.PROD_NAME));
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return products;
	}

    public void closeConnection() {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
