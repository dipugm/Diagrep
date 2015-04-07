package diagrepServer.database.core;

import java.util.HashMap;

public class DatabaseConnectionPool {

	private static DatabaseConnectionPool singletonInstance = null;
	public static synchronized DatabaseConnectionPool getPool() {
		if( singletonInstance == null ) {
			singletonInstance = new DatabaseConnectionPool();
		}
		return singletonInstance;
	}
	
	private String dbFolderPath = null;
	private DatabaseConnection masterDbConnection;
	private DatabaseConnection savedDataDbConnection;
	private HashMap<String, DatabaseConnection> mapDbFileToConnection;
	
	private DatabaseConnectionPool() {
	}
	
	public synchronized void initializeDbConnections( String dbFolder ) {
		if( this.dbFolderPath == null ) {
			dbFolderPath	= new String(dbFolder);
			
			masterDbConnection		= new DatabaseConnection( dbFolderPath + "/master.db" );
			savedDataDbConnection 	= new DatabaseConnection( dbFolderPath + "/saved_data.db" );
			mapDbFileToConnection	= new HashMap<String, DatabaseConnection>();
		}
	}
	
	public String getDbFolderPath() {
		return dbFolderPath;
	}
	
	public DatabaseConnection getMasterDbConnection() {
		return masterDbConnection;
	}
	
	public DatabaseConnection getSavedDataDbConnection() {
		return savedDataDbConnection;
	}
	
	public DatabaseConnection getConnectionForDbFile( String fileName ) {
		DatabaseConnection dbConn = null;
		fileName = this.dbFolderPath + "/" + fileName; 
		if( mapDbFileToConnection.containsKey( fileName ) ) {
			dbConn	= mapDbFileToConnection.get( fileName );
		} else {
			dbConn	= new DatabaseConnection( fileName );
			mapDbFileToConnection.put( fileName, dbConn );
		}
		
		return dbConn;
	}
}
