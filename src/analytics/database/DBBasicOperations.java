package analytics.database;


import static analytics.database.DBCredentials.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.sql.PreparedStatement;


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
			ResultSet rs = statement.executeQuery("SELECT " + PROD_NAME + " FROM " + TABLE_PROD);
			
			while(rs.next())
				products.add(rs.getString(PROD_NAME));
			
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
				"SELECT SUM(bons." + BON_CANT + ") " +
				"FROM " + TABLE_PROD + " prods, " + TABLE_BON + " bons " + 
				"WHERE bons." +  BON_DATE + "='" + day + "' AND " +
						"bons." + BON_CODART + "=prods." + PROD_CODE + " AND " +
						"prods." + PROD_NAME + "='" + product + "'";
			
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
				"SELECT SUM(bons." + BON_CANT + ") " +
				"FROM " + TABLE_PROD + " prods, " + TABLE_BON + " bons " + 
				"WHERE bons." +  BON_DATE + " BETWEEN '" + ini + "' AND '" + fin + "' AND " +
						"bons." + BON_CODART + "=prods." + PROD_CODE + " AND " +
						"prods." + PROD_NAME + "='" + product + "'";
			
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
			String query = "SELECT " + BON_CODBON + ", " + BON_CODART + " FROM " + TABLE_BON;
			Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet resultSet = statement.executeQuery(query);
			
			while(resultSet.next()) {
				Integer nrBon = resultSet.getInt(BON_CODBON);
				Integer codArt = resultSet.getInt(BON_CODART);
				
				if(codArt == 0) {
					//THIS IS A DATABASE BUG!!!
					continue;
				}
				
				if(transactions.containsKey(nrBon)){
					if(!transactions.get(nrBon).contains(codArt)) {
						transactions.get(nrBon).add(codArt);
					}
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
	 * @return a HashMap that has as key the transaction's code and as value the list of consumed products' category
	 */
	public HashMap<Integer, List<String>> getCategories() {
		HashMap<Integer, List<String>> categories = new HashMap<Integer, List<String>>();
		HashMap<Integer, List<Integer>> transactions = this.getTransactions();
		
		try {
			String query = "SELECT a." + CLASS_NAME + ", b." + PROD_CATEGORY +  
						" FROM " + TABLE_CLASS + " a, " + TABLE_PROD + " b" +
						" WHERE b." + PROD_CODE + " = ? AND a." + CLASS_CODE + " = b." + PROD_CATEGORY;
			PreparedStatement statement = connection.prepareStatement(query);
			
			List<Integer> codes = new ArrayList<Integer>(transactions.keySet());
			Collections.sort(codes);
			for(int code : codes) {
				categories.put(code, new ArrayList<String>());
				List<Integer> prods = transactions.get(code);
				for(int prod : prods) {
					statement.setInt(1, prod);
					ResultSet resultSet = statement.executeQuery();
					if (resultSet.next()) {
						String category = resultSet.getString(CLASS_NAME);
						//this allows duplicates
						categories.get(code).add(category);
						/*this does not allow duplicates
						List<String> categs = categories.get(code);
						if(!categs.contains(category)) {
							categs.add(category);
						}
						*/
					}
				}
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return categories;
	}
	
	/**
	 * @return a HashMap that has as key the product's code and as value the product's name
	 */
	public HashMap<Integer,String> getProducts() {
		HashMap<Integer, String> products = new HashMap<Integer, String>();
		
		try {
			String query = "SELECT " + PROD_CODE + ", " + PROD_NAME + " FROM " + TABLE_PROD;
			Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet resultSet = statement.executeQuery(query);
			
			while(resultSet.next()) {
				Integer prodCode = resultSet.getInt(PROD_CODE);
				String prodName = resultSet.getString(PROD_NAME);
				
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
	
	public List<String> getNamesForProducts(Integer[] codes) {
		List<String> prods = new ArrayList<String>();
		
		try {
			String query = "SELECT " + PROD_NAME + " FROM " + TABLE_PROD + " WHERE " + PROD_CODE + " = ?";
			PreparedStatement statement = connection.prepareStatement(query);
			
			for(Integer code : codes) {
				statement.setInt(1, code);
				ResultSet resultSet = statement.executeQuery();
				if (resultSet.next()) {
					prods.add(resultSet.getString(PROD_NAME));
				}
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
