package diagrepServer.Utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class DiagrepConfig {

	public final static String DB_FOLDER_PATH		= "databaseFolderPath";
	public final static String DATE_FORMAT			= "dateFormat";
	public final static String CUSTOMER_ID_PREFIX	= "customerIdPrefix";
	public final static String CUSTOMER_DB_IMPORTED	= "customerDbImportedName";
	
	private static DiagrepConfig instance = null;
	public static synchronized DiagrepConfig getConfig() {
		if( instance == null ) {
			instance 	= new DiagrepConfig();
		}
		return instance;
	}
	
	public void loadConfig( String rootFolder ) {
		properties 	= new Properties();
		
		try {
			properties.load( new FileInputStream( rootFolder + "general.properties" ) );
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println( "general.properties file not found in path : " + rootFolder );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private Properties properties;
	
	private DiagrepConfig() {}
	
	public String get( String key ) {
		return this.properties.getProperty( key ); 
	}
	
}
