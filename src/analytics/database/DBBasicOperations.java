package analytics.database;


import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

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
	
	public int getEntryTotalValuePerDay(String product, Date day) {
		int value = 0;
		try {
			String query =
				"SELECT SUM(bons." + DBCredentials.BON_CANT + ") " +
				"FROM " + DBCredentials.TABLE_PROD + " prods, " + DBCredentials.TABLE_BON + " bons " + 
				"WHERE bons." +  DBCredentials.BON_DATE + "='" + day + "' AND " +
						"bons." + DBCredentials.BON_CODART + "=prods." + DBCredentials.PROD_CODE + " AND " +
						"prods." + DBCredentials.PROD_NAME + "='" + product + "'";
			
			Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet resultSet = statement.executeQuery(query);
			
			if(resultSet.next())
				value = resultSet.getInt(1);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	public int getEntryTotalValuePerInterval(String product, Date ini, Date fin) {
		int value = 0;
		try {
			String query =
				"SELECT SUM(bons." + DBCredentials.BON_CANT + ") " +
				"FROM " + DBCredentials.TABLE_PROD + " prods, " + DBCredentials.TABLE_BON + " bons " + 
				"WHERE bons." +  DBCredentials.BON_DATE + " BETWEEN '" + ini + "' AND '" + fin + "' AND " +
						"bons." + DBCredentials.BON_CODART + "=prods." + DBCredentials.PROD_CODE + " AND " +
						"prods." + DBCredentials.PROD_NAME + "='" + product + "'";
			
			Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet resultSet = statement.executeQuery(query);
			
			if(resultSet.next())
				value = resultSet.getInt(1);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return value;
	}

    public void closeConnection() {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
