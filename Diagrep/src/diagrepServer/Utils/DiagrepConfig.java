package diagrepServer.Utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.util.Properties;


public class DiagrepConfig {

	public final static String DB_FOLDER_PATH			= "databaseFolderPath";
	public final static String BILL_DATE_FORMAT			= "billDateFormat";
	public final static String REPORT_DATE_FORMAT		= "reportDateFormat";
	public final static String CUSTOMER_ID_PREFIX		= "customerIdPrefix";
	public final static String CUSTOMER_DB_IMPORTED		= "customerDbImportedName";
	public final static String CUSTOMER_SEARCH_LIMIT 	= "customerSearchResultsLimit";
	public final static String TEMPLATES_FOLDER_PATH	= "templatesFolderPath";
	
	private static DiagrepConfig instance = null;
	private static String lock = "config-lock";
	public static synchronized DiagrepConfig getConfig() {
		synchronized(DiagrepConfig.lock) {
			if( instance == null ) {
				instance 	= new DiagrepConfig();
			}
		}
		return instance;
	}
	
	public synchronized void loadConfig( String rootFolder ) {
		if( properties != null ) {
			// Configuration properties file already loaded.
			return;
		}
				
		rootFolder = rootFolder + "/WEB-INF/";
		
		properties 	= new Properties();
		
		loadGeneralConfig(rootFolder);
		loadLocalConfig(rootFolder);
		
		System.out.println(properties.toString());

	}
		
	private void loadGeneralConfig( String rootFolder ) {
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
	
	private void loadLocalConfig( String rootFolder ) {
		String tmpdir = System.getProperty("java.io.tmpdir");
		if( !tmpdir.endsWith(File.separator) ) {
			tmpdir += File.separator;
		}
		String tmpPropFile = tmpdir + String.format("local_%s.properties", this.appName.toLowerCase());
		
		System.out.println("Temp directory : " + tmpdir);
		System.out.println("Local properties file : " + tmpPropFile);
		
		File tmpFile = new File(tmpPropFile);
		
		if( ! tmpFile.exists() ) {
			System.out.println("Copying local config file from " + rootFolder);
			copyFile( rootFolder + "local.properties", tmpPropFile );
		}
		
		try {
			properties.load( new FileInputStream( tmpPropFile ) );
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println( "local.properties file not found and could not be created either - " + tmpdir );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean copyFile(String src, String dest ) {
		return copyFile(src, dest, false);
	}
	
	private boolean copyFile(String src, String dest, boolean shouldOverwrite) {
		try { 
	         
	        File outFile = new File(dest);
	        if( outFile.exists() && !shouldOverwrite ) {
	        	return false;
	        }
	        
	        FileInputStream fis = new FileInputStream(src);
	        
	        /* assuming that the file exists and need not to be 
	           checked */
	        FileOutputStream fos = new FileOutputStream(dest); 
	  
	        int b; 
	        while  ((b=fis.read()) != -1) 
	            fos.write(b); 
	  
	        fis.close(); 
	        fos.close(); 
	        
	    } catch(Exception e) {
	    	return false;
	    }
		return true;
	}
	
	private Properties properties;
	private String appName;
	
	private DiagrepConfig() {}
	
	public String get( String key ) {
		return this.properties.getProperty( key ); 
	}
	
	public void setAppName(String appName) { 
		System.out.println("App name : " + appName );
		this.appName = appName; 
	}
	public String getAppName() { return this.appName; }
	
}
