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
	
	String DATABASE = "disertatie";
	
	String TABLE_BON = "bonuri";
	String TABLE_PROD = "produse";
	String TABLE_CLASS = "clase";
	
	String PROD_CODE = "c";
	String PROD_NAME = "denumire";
	String PROD_CATEGORY = "clasa";
	
	String BON_CODBON = "codbonconsum";
	String BON_CODART = "codarticol";
	String BON_CANT = "cantitate";
	String BON_DATE = "data";
	
	String CLASS_CODE = "cod";
	String CLASS_NAME = "denumire";
	
	String LINK = "jdbc:mysql://" + IP + "/" + DATABASE + 
			"?user=" + USER + "&password=" + PASSWORD;
	
	String DRIVER = "com.mysql.jdbc.Driver";
}
