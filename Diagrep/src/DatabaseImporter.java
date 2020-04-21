

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseImporter {

	public static void main( String[] args ) {

		// Check the arguments
		if( args.length != 2 ) {
			printUsage();
			return;
		}
		
		String sz1 = args[0].contains( "from:") ? args[0] : args[1];
		String sz2 = args[1].contains( "to:") ? args[1] : args[0];
		
		String szSourceFolder = "";
		String szDestFolder = "";
		if( sz1.contains( "from:" ) ) {
			szSourceFolder = sz1.replace( "from:", "");
		}
		if( sz2.contains( "to:" ) ) {
			szDestFolder = sz2.replace( "to:", "");
		}
		
		if( szSourceFolder.isEmpty() || szDestFolder.isEmpty() ) {
			printUsage(); 
			return;
		}
		
		// Check if the folders exist.
		if( (false == (new File(szSourceFolder)).exists()) ||
				(false == (new File(szDestFolder)).exists()) ) {
			
			printUsage(); 
			return;
		}
		
		System.out.println( "Starting Database import!" );
		
		DatabaseImporter di = new DatabaseImporter( szSourceFolder, szDestFolder );

		di.importEntities("Tests", "Test");
		di.importEntities("Categories", "Category");
		di.importEntities("Collections", "Collection");
		di.importEntities("Packages", "Package");
		di.importEntities("Customer_Details", "Customer", di.dbCustDestConnection);
		di.importEntities("Data_Dictionary", "DataDictionary");
		
		di.importCategoryDetails();
		di.importCollectionDetails();
		di.importPackageDetails();
		
		di.importDbFilesWithMapping();
				
		di.closeConnections();
		
		System.out.println( "Database import complete!" );
	}
	
	public static void printUsage() {
		System.out.println( "Usage : \n");
		System.out.println( "DatabaseImporter from:<db folder path> to:<db folder path> ");
		System.out.println( " ");
		System.out.println( "Please make sure you provide the path to folder and not the file");
	}
	
	
	private String sourceFolderPath;
	private String destFolderPath;
	private String sourceDbFile;
	private String destDbFile;
	private String destCustomerDbFie;
	
	private Connection dbSourceConnection = null;
	private Connection dbDestConnection = null;
	private Connection dbCustDestConnection = null;
	
	public DatabaseImporter( String sourceFolder, String destFolder ) {
		
		this.sourceFolderPath 	= sourceFolder;
		this.destFolderPath 	= destFolder;
		
		this.sourceDbFile 		= this.sourceFolderPath + "/master.db";
		this.destDbFile			= this.destFolderPath + "/master.db";
		this.destCustomerDbFie  = this.destFolderPath + "/custdata_imported.db";
		
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
					
					++rowCount;
					
				}
				
				destDbConn.commit();
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
				}
					dbDestConnection.commit();
				
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
				}
					dbDestConnection.commit();
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
				}
				
					dbDestConnection.commit();
				
			}
			
			System.out.println( " Success!");
			
		} catch (SQLException e) {
			
			System.out.println( " Failed!");
			e.printStackTrace();
		}
	}
	
	public void importDbFilesWithMapping() {
		System.out.println( "Importing Db File mappings and the actual dd files... ");
		
		// First get the mapping.
		String sql	= "SELECT BillTextPart,DatabaseFileName,StartBillNumber,LastBillNumber FROM Db_File_Mapping ORDER BY rowid ASC";
		try {
			Statement stmt = dbSourceConnection.createStatement();
			ResultSet rs 	= stmt.executeQuery( sql );
			
			if( rs != null ) {
				
				dbDestConnection.createStatement().execute( "DELETE FROM DbFileMapping" );
				dbDestConnection.commit();
				
				while( rs.next() ) {
					String billTextPart 	= rs.getString(1);
					String dbFileName	 	= rs.getString(2);
					Integer startNum		= rs.getInt(3);
					Integer endNum			= rs.getInt(4);

					String sqlM = "INSERT INTO DbFileMapping( textPart, databaseFileName, startNumber, endNumber, type)";
					sqlM += " VALUES('" + billTextPart + "','"+ dbFileName + "'," + String.valueOf(startNum);
					sqlM += "," + String.valueOf( endNum ) + ",0)";
					
					dbDestConnection.createStatement().execute( sqlM );
					dbDestConnection.commit();
					
					// Now check if we have the physical file and copy the contents to a new db file of the current schema
					importDynamicData( dbFileName );
				}
				
			}
			
			System.out.println( " Success!");
			
		} catch (SQLException e) {
			
			System.out.println( " Failed!");
			e.printStackTrace();
		}
	}
	
	public void importDynamicData( String sourceFileName ) {
		String sourceFilePath = this.sourceFolderPath + "/" + sourceFileName;
		String destFilePath = this.destFolderPath + "/" + sourceFileName;
		
		if( (new File(sourceFilePath)).exists() == false ) {
			System.err.println( "Source dd file does not exist : " + sourceFilePath );
			return;
		}
		
		if( (new File(destFilePath)).exists() == false ) {
			createNewDDFile( sourceFileName ); 
		}
		
		// Open Db connections to both these files.
		Connection cnSource = null;
		try {
			cnSource = DriverManager.getConnection("jdbc:sqlite:" + sourceFilePath);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Connection cnDest = null;
		try {
			cnDest = DriverManager.getConnection("jdbc:sqlite:" + destFilePath);
			cnDest.setAutoCommit(false);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println( "");
		System.out.println( "Importing from " + sourceFileName + " ... ");
		
		System.out.print( "  -> Importing Bills ... ");
		importBills( cnSource, cnDest );
		
		System.out.print( "  -> Importing Bill Details ... ");
		importBillDetails( cnSource, cnDest );
		
		System.out.print( "  -> Importing Reorts ... ");
		importReports( cnSource, cnDest );
		
		System.out.print( "  -> Importing Report Details ... ");
		importReportDetails( cnSource, cnDest );
		
		System.out.println( "");
		
		try {
			cnSource.close();
			cnDest.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
	}
	
	public void importBills( Connection cnSource, Connection cnDest ) {
		
		// First get the mapping.
		String sql	= "SELECT BillNumber, PatientId, ReferredBy, BillDate, ReportDate, AdvancePaid FROM Bill_Details ORDER BY BillNumber ASC";
		try {
			Statement stmt = cnSource.createStatement();
			ResultSet rs 	= stmt.executeQuery( sql );
			
			if( rs != null ) {
				
				cnDest.createStatement().execute( "DELETE FROM Bill" );
				cnDest.commit();
				
				while( rs.next() ) {
					String billNumber 	= rs.getString(1);
					String patientId	= rs.getString(2);
					String refBy		= rs.getString(3);
					Long billDate		= rs.getLong(4) * 1000;
					Long reportDate		= rs.getLong(5) * 1000;
					Double advance		= rs.getDouble(6);

					String sqlM = "INSERT INTO Bill( billNumber, customerId, referredBy, billDate, reportDate, advancePaid)";
					sqlM += " VALUES('" + billNumber + "','"+ patientId + "','" + refBy + "'," + String.valueOf(billDate);
					sqlM += "," + String.valueOf( reportDate ) + "," + String.valueOf(advance) + ")";
					
					cnDest.createStatement().execute( sqlM );
				}
					cnDest.commit();
				}
			
			System.out.println( " Success!");
			
		} catch (SQLException e) {
			
			System.out.println( " Failed!");
			e.printStackTrace();
		}
	}
	
	public void importBillDetails( Connection cnSource, Connection cnDest ) {
		
		// First get the mapping.
		String sql	= "SELECT BillNumber, EntityType, EntityId FROM Bill_Test_Details ORDER BY BillNumber ASC";
		try {
			Statement stmt = cnSource.createStatement();
			ResultSet rs 	= stmt.executeQuery( sql );
			
			if( rs != null ) {
				
				cnDest.createStatement().execute( "DELETE FROM BillDetails" );
				cnDest.commit();
				
				while( rs.next() ) {
					String billNumber 	= rs.getString(1);
					Integer entityType	= rs.getInt(2) - 1;
					Integer entityId	= rs.getInt(3);

					String sqlM = "INSERT INTO BillDetails( billNumber, entityType, entityId, cost)";
					sqlM += " VALUES('" + billNumber + "',"+ String.valueOf(entityType) + "," + String.valueOf(entityId);
					sqlM += ",0.0)";
					
					cnDest.createStatement().execute( sqlM );
				}
					cnDest.commit();
				}
			
			System.out.println( " Success!");
			
		} catch (SQLException e) {
			
			System.out.println( " Failed!");
			e.printStackTrace();
		}
	}

	public void importReports( Connection cnSource, Connection cnDest ) {
		
		// First get the mapping.
		String sql	= "SELECT BillNumber, PreConditions, Recommendations FROM Report_Details ORDER BY BillNumber ASC";
		try {
			Statement stmt = cnSource.createStatement();
			ResultSet rs 	= stmt.executeQuery( sql );
			
			if( rs != null ) {
				
				cnDest.createStatement().execute( "DELETE FROM Report" );
				cnDest.commit();
				
				while( rs.next() ) {
					String billNumber 	= rs.getString(1);
					String preCond		= rs.getString(2);
					String reco			= rs.getString(3);
					if( reco != null ) {
						reco = javax.xml.bind.DatatypeConverter.printBase64Binary( reco.getBytes() );
					} else {
						reco = "";
					}
	
					String sqlM = "INSERT INTO Report( billNumber, recommendations, preConditions)";
					sqlM += " VALUES('" + billNumber + "','"+ reco + "','" + preCond + "')";
					
					cnDest.createStatement().execute( sqlM );
				}
					cnDest.commit();
				}
			
			System.out.println( " Success!");
			
		} catch (SQLException e) {
			
			System.out.println( " Failed!");
			e.printStackTrace();
		}
	}

	public void importReportDetails( Connection cnSource, Connection cnDest ) {
	
	// First get the mapping.
	String sql	= "SELECT BillNumber, Description, EntityId, EntityValue, IsHighlighted FROM Report_Test_Details ORDER BY BillNumber ASC";
	try {
		Statement stmt = cnSource.createStatement();
		ResultSet rs 	= stmt.executeQuery( sql );
		
		if( rs != null ) {
			
			cnDest.createStatement().execute( "DELETE FROM ReportDetails" );
			cnDest.commit();
			
			while( rs.next() ) {
				String billNumber 	= rs.getString(1);
				String desc			= rs.getString(2);
				if( desc != null ) {
					desc = javax.xml.bind.DatatypeConverter.printBase64Binary( desc.getBytes() );
				} else {
					desc = "";
				}
				
				Integer entityId	= rs.getInt(3);
				String entityValue	= rs.getString(4);
				if( entityValue != null ) {
					entityValue = javax.xml.bind.DatatypeConverter.printBase64Binary( entityValue.getBytes() );
				} else {
					entityValue = "";
				}
				
				Integer isHigh		= rs.getInt(5);

				String sqlM = "INSERT INTO ReportDetails( billNumber, description, entityId, entityValue, isHighlighted)";
				sqlM += " VALUES('" + billNumber + "','"+ desc + "'," + String.valueOf(entityId);
				sqlM += ",'" + entityValue + "'," + String.valueOf(isHigh) + ")";
				
				cnDest.createStatement().execute( sqlM );
			}
				cnDest.commit();
			}
		
		System.out.println( " Success!");
		
	} catch (SQLException e) {
		
		System.out.println( " Failed!");
		e.printStackTrace();
	}
}
	
	public void createNewDDFile( String fileName ) {
		String sourcePath 	= this.destFolderPath + "/dyndata_templ.db";
		String destPath 	= this.destFolderPath + "/" + fileName;

		System.out.println( "Db file does not exist, so createing it -> " + destPath );
		
		FileChannel src	= null;
		FileChannel dest = null;
		try {
			FileInputStream srcFI = new FileInputStream(sourcePath); 
			src = srcFI.getChannel();
			FileInputStream destFI = new FileInputStream(destPath);
			dest = destFI.getChannel();
			
			dest.transferFrom(src, 0, src.size());
			
			srcFI.close();
			destFI.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
