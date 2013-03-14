package analytics.database;

/**
 * Interface used for storing as final strings
 * the credentials used for database operations
 * 
 * @author Razvan Nedelcu
 */
public interface DBCredentials {
	String IP = "localhost";
	String USER = "root";
	String PASSWORD = "";
	String DATABASE = "vectsoftges";
	String TABLE_BON = "iesbonconsumfield";
	String TABLE_PROD = "articolenom";
	String PROD_NAME = "denumire";
	
	String LINK = "jdbc:mysql://" + IP + "/" + DATABASE + 
			"?user=" + USER + "&password=" + PASSWORD;
	
	String DRIVER = "com.mysql.jdbc.Driver";
}
