package analytics.database;


import static analytics.database.DBCredentials.DRIVER;
import static analytics.database.DBCredentials.LINK;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


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
	
	/**
	 * @return an instance of the DBBasicOperations class
	 */
	public static DBBasicOperations getInstance() {
		return dbConnection;
	}
	
	/**
	 * This method is the first one that should be called after obtaining an instance of the DBBasicOperations class
	 */
	public void openConnection() {
        try {
			Class.forName(DRIVER).newInstance();
            connection = DriverManager.getConnection(LINK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	/**
	 * @return a list containing all the products' names
	 */
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
	
	/**
	 * @return a HashMap that has as key the transaction's code and as value the list of consumed products' codes
	 */
	public HashMap<Integer, List<Integer>> getTransactions() {
		HashMap<Integer, List<Integer>> transactions = new HashMap<Integer, List<Integer>>();
		
		try {
			String query = "SELECT " + DBCredentials.BON_CODBON + ", " + 
							DBCredentials.BON_CODART + " FROM " + DBCredentials.TABLE_BON;
			Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet resultSet = statement.executeQuery(query);
			
			while(resultSet.next()) {
				Integer nrBon = resultSet.getInt(DBCredentials.BON_CODBON);
				Integer codArt = resultSet.getInt(DBCredentials.BON_CODART);
				
				if(codArt == 0) {
					//THIS IS A DATABASE BUG!!!
					continue;
				}
				
				if(transactions.containsKey(nrBon) &&
						!transactions.get(nrBon).contains(codArt)) {
					transactions.get(nrBon).add(codArt);
				} else {
					ArrayList<Integer> list = new ArrayList<Integer>();
					list.add(codArt);
					transactions.put(nrBon, list);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return transactions;
	}
	
	/**
	 * @return a HashMap that has as key the product's code and as value the product's name
	 */
	public HashMap<Integer,String> getProducts() {
		HashMap<Integer, String> products = new HashMap<Integer, String>();
		
		try {
			String query = "SELECT " + DBCredentials.PROD_CODE + ", " + 
							DBCredentials.PROD_NAME + " FROM " + DBCredentials.TABLE_PROD;
			Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet resultSet = statement.executeQuery(query);
			
			while(resultSet.next()) {
				Integer prodCode = resultSet.getInt(DBCredentials.PROD_CODE);
				String prodName = resultSet.getString(DBCredentials.PROD_NAME);
				
				if(prodName.startsWith("*")){
					continue;
				}
				
				if(!products.containsKey(prodCode)) {
					products.put(prodCode, prodName);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return products;
	}
	
	public List<String> getNamesForFroducts(List<Integer> codes) {
		List<String> prods = new ArrayList<String>();
		
		//TODO - prepared statements
		try {
			String query = "SELECT " + DBCredentials.PROD_NAME + " FROM " + DBCredentials.TABLE_PROD +
					" WHERE " + DBCredentials.PROD_CODE + " = ";
			Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet resultSet = statement.executeQuery(query);
			
			while(resultSet.next()) {
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return prods;
	}

	/**
	 * Closes the connection with the database.
	 * This is the last function that should be called.
	 */
    public void closeConnection() {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
