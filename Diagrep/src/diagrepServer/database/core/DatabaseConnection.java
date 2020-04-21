package diagrepServer.database.core;

import java.util.ArrayList;
import java.lang.reflect.Field;
import java.sql.*;

public class DatabaseConnection {

	private Connection dbConnection;
	
	public enum EnumDBResult {
		DB_SUCCESS,
		DB_FAILED_DUPLICATE_DATA
	}
	
	@SuppressWarnings("unused")
	private DatabaseConnection() {}
	
	public DatabaseConnection( String fileName ) {
		dbConnection = null;
	    try {
	    	Class.forName("org.sqlite.JDBC");
	    	dbConnection = DriverManager.getConnection("jdbc:sqlite:" + fileName);
	    	dbConnection.setReadOnly( false );
	    	dbConnection.setAutoCommit(true);
	    	
	    	System.out.println("Opened database successfully =>");
	    	System.out.println( fileName );
	    } catch ( Exception e ) {
	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	    	System.exit(0);
	    }
	}
	
	public void closeConnection() {
		if( this.dbConnection != null ) {
			try {
				this.dbConnection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public EnumDBResult execute( DatabaseCallParams params ) {
		 String sql	= params.getSqlStatement();
		 
		 EnumDBResult ret	= EnumDBResult.DB_SUCCESS;
		 try {
			 dbConnection.setReadOnly( false );
			 
			 Statement stmt = dbConnection.createStatement();
			 stmt.executeUpdate( sql );
			 stmt.close();
			 
		 } catch ( SQLException e) {
//			 e.printStackTrace();
			 
			 System.out.println( e.getMessage() );
			 System.out.println( e.getLocalizedMessage() );
			 System.out.println( e.toString() );
			 
			 if( e.toString().contains( "UNIQUE constraint failed") ) {
				 ret	= EnumDBResult.DB_FAILED_DUPLICATE_DATA ;
			 }
		 }
		 
		 return ret;
	}
	
	public ArrayList<ModelObject> fetch( DatabaseCallParams params ) {
		
		ArrayList<ModelObject> resultObjects = new ArrayList<ModelObject>();
		
		// Get the sql to fetch from the call params.
		String sql = params.getSqlStatement();
		
		try {
			
			Statement stmt = dbConnection.createStatement();
			ResultSet resultSet = stmt.executeQuery( sql );
			Class<?> classForModelObject = Class.forName( params.modelClassName );
			
			if( resultSet != null ) {
				
				ResultSetMetaData rsmd 	= resultSet.getMetaData();
				int columnCount 		= rsmd.getColumnCount();
				while( resultSet.next() ) {
					ModelObject mo = (ModelObject)classForModelObject.newInstance();
					
					for( int iColumn=1; iColumn <= columnCount; iColumn++ ) {
						int columnType 		= rsmd.getColumnType( iColumn );
						String columnName 	= rsmd.getColumnName( iColumn );
					
						try {
							Object value 	= resultSet.getObject( iColumn );
							
							// We ignore null fields to give the model field's default value to stay.
							if( value == null ) {
								continue;
							}
							
							Field field 	= classForModelObject.getField( columnName );
							
							switch( columnType ) {
							case java.sql.Types.DATE:
								value = new Date( resultSet.getLong( iColumn ) );
								break;
							}
							
							value = mo.doAnyTypeConversions( columnName, value );
							
							field.set( mo, value );
							
						} catch(  NoSuchFieldException nsfe ) {
							System.out.println( "No field " + columnName + " in class " + params.modelClassName );
						}
					}
					
					// Giving the model object a chance to fill any non column variables with some proper values.
					mo.fillFKs();
					
					resultObjects.add( mo );
					
				}
				
				resultSet.close();
			}
			
			stmt.close();
			
		} catch( ClassNotFoundException cnf ) {
			System.out.println( "No class for " + params.tableName + ". Table name and Class names should be one to one");
		} catch( Exception e ) {
			e.printStackTrace();
		}
		
		return resultObjects;
	}
}
