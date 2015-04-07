package diagrepServer.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseImporter {

	public static void main( String[] args ) {
		
		System.out.println( "Starting Database import!" );
		
		DatabaseImporter di = new DatabaseImporter();

		di.importEntities("Tests", "Test");
		di.importEntities("Categories", "Category");
		di.importEntities("Collections", "Collection");
		di.importEntities("Packages", "Package");
		di.importEntities("Customer_Details", "Customer", di.dbCustDestConnection);
		di.importEntities("Data_Dictionary", "DataDictionary");
		di.importCategoryDetails();
		di.importCollectionDetails();
		di.importPackageDetails();
				
		di.closeConnections();
		
		System.out.println( "Database import complete!" );
	}
	
	private String sourceDbFile	= "/Users/tavant/Shared/output_22_09_2014/Diagrep-22.09.2014/database/master.db";
	private String destDbFile	= "/Users/tavant/Documents/Diagrep_dbFiles/master.db";
	private String destCustomerDbFie = "/Users/tavant/Documents/Diagrep_dbFiles/custdata_imported.db";
	private Connection dbSourceConnection = null;
	private Connection dbDestConnection = null;
	private Connection dbCustDestConnection = null;
	
	public DatabaseImporter() {
		
	    try {
	    	Class.forName("org.sqlite.JDBC");
	    	dbSourceConnection = DriverManager.getConnection("jdbc:sqlite:" + sourceDbFile);
	    	dbSourceConnection.setAutoCommit(false);
	    	
	    	dbDestConnection = DriverManager.getConnection("jdbc:sqlite:" + destDbFile);
	    	dbDestConnection.setAutoCommit(false);
	    	
	    	dbCustDestConnection = DriverManager.getConnection("jdbc:sqlite:" + destCustomerDbFie);
	    	dbCustDestConnection.setAutoCommit(false);
	    	
	    } catch ( Exception e ) {
	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	    	System.exit(0);
	    }
	    System.out.println("Opened database successfully");
	    
	}
	
	public void closeConnections() {
		try {
			dbSourceConnection.close();
			dbDestConnection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void alterColumnName( String tableName, String fromColumnName, String toColumnName ) {
		try {
			String sql = "ALTER TABLE " + tableName + " RENAME COLUMN " + fromColumnName + " TO " + toColumnName;
			dbDestConnection.createStatement().execute( sql );
			dbDestConnection.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public void importEntities( String sourceTable, String destTable ) {
		importEntities( sourceTable, destTable, dbDestConnection );
	}
	
	public void importEntities( String sourceTable, String destTable, Connection destDbConn  ) {
		
		System.out.print( "Importing data from " + sourceTable + " into " + destTable + " ... ");
		String sql	= "SELECT * FROM " + sourceTable + " ORDER BY rowid ASC";
		try {
			Statement stmt = dbSourceConnection.createStatement();
			ResultSet rs 	= stmt.executeQuery( sql );
			
			Integer rowCount	= 0;
			
			if( rs != null ) {
				
				ResultSetMetaData rsmd = rs.getMetaData();
				
				// Clear the destination table first.
				destDbConn.createStatement().execute( "DELETE FROM " + destTable );
				destDbConn.commit();
				
				while( rs.next() ) {
					
					StringBuilder columnNamesBuilder = new StringBuilder();
					StringBuilder valuesBuilder = new StringBuilder();
					
					for( int iCol=1; iCol <= rsmd.getColumnCount(); iCol++ ) {
						String columnName = rsmd.getColumnName( iCol ).toLowerCase();
						
						if( columnName.indexOf( "_" ) >= 0 ) {
							String[] sz = columnName.split("_");
							columnName = "";
							for( int i=0; i < sz.length; i++ ) {
								if( i == 0 ) {
									columnName += sz[i];
								} else {
									columnName += sz[i].substring(0, 1).toUpperCase();
									columnName += sz[i].substring(1);
								}
							}
						}
						
						if( iCol > 1 ) {
							columnNamesBuilder.append( ",");
							valuesBuilder.append( "," );
						}
						columnNamesBuilder.append( columnName );
						
						switch( rsmd.getColumnType( iCol ) ) {
						case java.sql.Types.NUMERIC:
						case java.sql.Types.INTEGER:
						case java.sql.Types.FLOAT:
						case java.sql.Types.SMALLINT:
						case java.sql.Types.DOUBLE:
						case java.sql.Types.REAL:
						case java.sql.Types.DECIMAL:
							valuesBuilder.append( rs.getString(iCol ) );
							break;
							
						case java.sql.Types.VARCHAR:
						case java.sql.Types.CHAR:
							valuesBuilder.append( "'" );
							valuesBuilder.append( rs.getString( iCol ) );
							valuesBuilder.append( "'" );
							break;
						}
						
					}
					
					StringBuilder sqlWrite = new StringBuilder();
					sqlWrite.append( "INSERT INTO " );
					sqlWrite.append( destTable );
					sqlWrite.append( " (" );
					sqlWrite.append( columnNamesBuilder.toString() );
					sqlWrite.append( ") VALUES (");
					sqlWrite.append( valuesBuilder.toString() );
					sqlWrite.append( ")");
					
					destDbConn.createStatement().execute( sqlWrite.toString() );
					destDbConn.commit();
					
					++rowCount;
					
				}
			}
			
			System.out.println( "Success! [" + String.valueOf( rowCount ) + " records]");
		}
		catch ( Exception e ) {
			System.out.println( "Failed!");
			e.printStackTrace();
		}
	}
	
	public void importCategoryDetails() {
		
		System.out.print( "Importing Categories ... ");
		
		// Query the source Db for category details.
		String sql	= "SELECT GroupID,TestId,SortId FROM Category_Details ORDER BY GroupID,rowid ASC";
		try {
			Statement stmt = dbSourceConnection.createStatement();
			ResultSet rs 	= stmt.executeQuery( sql );
			
			if( rs != null ) {
				
				dbDestConnection.createStatement().execute( "DELETE FROM EntitiesRelation WHERE parentEntityType=1" );
				dbDestConnection.commit();
				
				while( rs.next() ) {
					Integer groupId = rs.getInt(1);
					Integer testId	= rs.getInt(2);
					
					String sqlM = "INSERT INTO EntitiesRelation( parentEntityType, parentEntityId, subEntityType, subEntityId)";
					sqlM += " VALUES( 1," + String.valueOf(groupId) + ",0," + String.valueOf(testId) + ")";
					
					dbDestConnection.createStatement().execute( sqlM );
					dbDestConnection.commit();
				}
				
			}
			
			System.out.println( " Success!");
			
		} catch (SQLException e) {
			
			System.out.println( " Failed. ");
			
			e.printStackTrace();
		}
	}
	
	public void importCollectionDetails() {
				
		System.out.print( "Importing Collections ... ");
		// Query the source Db for category details.
		String sql	= "SELECT CollectionID,ContaineeType,ContaineeID,SortId FROM Collection_Details ORDER BY CollectionID,rowid ASC";
		try {
			Statement stmt = dbSourceConnection.createStatement();
			ResultSet rs 	= stmt.executeQuery( sql );
			
			if( rs != null ) {
				
				dbDestConnection.createStatement().execute( "DELETE FROM EntitiesRelation WHERE parentEntityType=2" );
				dbDestConnection.commit();
				
				while( rs.next() ) {
					Integer entityId 	= rs.getInt(1);
					Integer contType 	= rs.getInt(2) - 1;
					Integer contId		= rs.getInt(3);
					
					String sqlM = "INSERT INTO EntitiesRelation( parentEntityType, parentEntityId, subEntityType, subEntityId)";
					sqlM += " VALUES( 2," + String.valueOf(entityId) + ","+ String.valueOf(contType) + "," + String.valueOf(contId) + ")";
					
					dbDestConnection.createStatement().execute( sqlM );
					dbDestConnection.commit();
				}
			}
			
			System.out.println( " Success!");
			
		} catch (SQLException e) {
			
			System.out.println( " Failed!");
			e.printStackTrace();
		}
	}
	
	public void importPackageDetails() {
		
		System.out.print( "Importing Packages ... ");
		// Query the source Db for package details.
		String sql	= "SELECT PackageID,ContaineeType,ContaineeID,SortId FROM Package_Details ORDER BY PackageID,rowid ASC";
		try {
			Statement stmt = dbSourceConnection.createStatement();
			ResultSet rs 	= stmt.executeQuery( sql );
			
			if( rs != null ) {
				
				dbDestConnection.createStatement().execute( "DELETE FROM EntitiesRelation WHERE parentEntityType=3" );
				dbDestConnection.commit();
				
				while( rs.next() ) {
					Integer entityId 	= rs.getInt(1);
					Integer contType 	= rs.getInt(2) - 1;
					Integer contId		= rs.getInt(3);

					String sqlM = "INSERT INTO EntitiesRelation( parentEntityType, parentEntityId, subEntityType, subEntityId)";
					sqlM += " VALUES( 3," + String.valueOf(entityId) + ","+ String.valueOf(contType) + "," + String.valueOf(contId) + ")";
					
					dbDestConnection.createStatement().execute( sqlM );
					dbDestConnection.commit();

				}
				
			}
			
			System.out.println( " Success!");
			
		} catch (SQLException e) {
			
			System.out.println( " Failed!");
			e.printStackTrace();
		}
	}
	

}
