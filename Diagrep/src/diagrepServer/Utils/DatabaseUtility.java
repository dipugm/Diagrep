package diagrepServer.Utils;

import java.util.ArrayList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import diagrepServer.CommonDefs;
import diagrepServer.database.core.*;
import diagrepServer.database.core.DatabaseCallParams.*;
import diagrepServer.database.core.DatabaseConnection.EnumDBResult;
import diagrepServer.database.model.DataDictionary;
import diagrepServer.database.model.DbFileMapping;


/*
 * Utility class to get some collated data from the Db.
 */
public class DatabaseUtility {
	
//	public static void main( String[] args ) {
//		String billNumber = "A00003";
//		String fileName = DatabaseUtility.getDbFileNameForBillNumber( billNumber );
//		System.out.println( "File for bill " + billNumber + " is " + fileName );
//	}
	
	public static ArrayList<String> getDbFileNamesForType( int type ) {
		
		// Get the master Db connection.
		DatabaseConnection dc	= DatabaseConnectionPool.getPool().getMasterDbConnection();
		
		DbFileMapping dfm = new DbFileMapping();
		DatabaseCallParams params = dfm.prepareForFetch();
		
		params.addCondition( new ConditionEquals( "type", type ) );
		if( type == CommonDefs.BILLREPORT_TYPE ) {
			params.addCondition( new ConditionEquals( "appName", DiagrepConfig.getConfig().getAppName()));
		}
		
		ArrayList<?> objects = dc.fetch(params);
		ArrayList<String> filenames = new ArrayList<String>();
		
		// Go through the returned array and extract the file names.
		for( int i=0; i < objects.size(); i++ ) {
			dfm = (DbFileMapping)objects.get(i);
			
			// Only if the DB file exists, we will consider this file name.
			File f = new File( DatabaseConnectionPool.getPool().getDbFolderPath() + "/" + dfm.databaseFileName );
			if( f.exists() ) {
				filenames.add( dfm.databaseFileName );
			}
		}
		
		return filenames;
	}

	public static String getDbFileNameForIdAndType( String identifier, int type ) {

		if( type == CommonDefs.CUSTOMER_TYPE ) {
			identifier = identifier.replace( DiagrepConfig.getConfig().get( DiagrepConfig.CUSTOMER_ID_PREFIX ) + "-", "");
		}
		
		String[] idSplit = StringUtilities.splitTextAndNumberParts( identifier );
		String fileName = "";
		
		// Get the master Db connection.
		DatabaseConnection dc	= DatabaseConnectionPool.getPool().getMasterDbConnection();
		
		DbFileMapping dfm = new DbFileMapping();
		dfm.textPart 	= idSplit[0];
		
		DatabaseCallParams params = dfm.prepareForFetch();
		
		params.addCondition( new ConditionLessThanOrEqual( "startNumber", idSplit[1]) );
		params.addCondition( new ConditionGreaterThanOrEqual( "endNumber", idSplit[1]) );
		params.addCondition( new ConditionEquals( "type", type ) );
		if( type == CommonDefs.BILLREPORT_TYPE ) {
			params.addCondition( new ConditionEquals( "appName", DiagrepConfig.getConfig().getAppName()));
		}
		
		ArrayList<?> objects = dc.fetch(params);
		if( objects.size() > 0 ) {
			dfm = (DbFileMapping)objects.get(0);
			fileName = dfm.databaseFileName;
		}
		
		return fileName;
	}
	
	public static String getOrCreateDbFileNameForIdAndType( String identifier, int type ) {
		
		// Check if we already have a file for this bill number
		String fileName = DatabaseUtility.getDbFileNameForIdAndType( identifier, type );
		
		if( type == CommonDefs.CUSTOMER_TYPE ) {
			identifier = identifier.replace( DiagrepConfig.getConfig().get( DiagrepConfig.CUSTOMER_ID_PREFIX ) + "-", "");
		}
		
		boolean fileDoesNotExist = false;
		if( fileName == null || fileName.isEmpty() ) {
			fileDoesNotExist = true;
			
			if( type == 0 ) {
				fileName = "dd_" + DiagrepConfig.getConfig().getAppName() + "_";
			} else {
				fileName = "custdata_";
			}
			
			// Since this is no file, we need to create one.
			String[] billSplit = StringUtilities.splitTextAndNumberParts( identifier );
			
			DbFileMapping dfm = new DbFileMapping();
			dfm.textPart 	= billSplit[0];
			dfm.startNumber = Integer.parseInt( billSplit[1] );
			dfm.endNumber	= dfm.startNumber + 999;	// One Db for every 1000 bills 
			dfm.type		= type;
			if( type == CommonDefs.BILLREPORT_TYPE ) {
				dfm.appName = DiagrepConfig.getConfig().getAppName();
			}
			
			fileName += dfm.textPart + "_" + dfm.startNumber + "_" + dfm.endNumber + ".db";
			
			dfm.databaseFileName = fileName; 
			
			// Save this file name in the master db
			DatabaseConnection dc	= DatabaseConnectionPool.getPool().getMasterDbConnection();
			DatabaseCallParams params = dfm.prepareForSave( false );

			if( EnumDBResult.DB_SUCCESS != dc.execute(params) ) {
				return "";
			}
		} else {
			// Check if the file exists.
			File f = new File( DatabaseConnectionPool.getPool().getDbFolderPath() + "/" + fileName );
			fileDoesNotExist = !f.exists();
		}
		
		// create a file by copying from the template file.
		if( fileDoesNotExist ) {
			String folder = DatabaseConnectionPool.getPool().getDbFolderPath();
			String sourcePath = folder + "/";
			if( type == 0 ) {
				sourcePath += "dyndata_templ.db";
			} else {
				sourcePath += "custdata_templ.db";
			}
			String destPath = folder + "/" + fileName;

			System.out.println( "Db file does not exist, so createing it -> " + destPath );
			
			FileChannel src	= null;
			FileChannel dest = null;
			try {
				FileInputStream srcFI = new FileInputStream(sourcePath); 
				src = srcFI.getChannel();
				FileOutputStream destFI = new FileOutputStream(destPath);
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
		return fileName;
	}
	
	public static boolean doesDbFileExist( String fileName ) {
		String path = DatabaseConnectionPool.getPool().getDbFolderPath() + "/" + fileName;
		
		File fl = new File( path );
		return fl.exists();
	}
	
	public static synchronized Integer getNextTestId() {
		return getNextEntityId( "Test" );
	}
	
	public static synchronized Integer getNextCategoryId() {
		return getNextEntityId( "Category" );
	}

	public static synchronized Integer getNextCollectionId() {
		return getNextEntityId( "Collection" );
	}
	
	public static synchronized Integer getNextPackageId() {
		return getNextEntityId( "Package" );
	}
	
	public static synchronized void saveNextTestId( Integer newId ) {
		saveNextEntityId( "Test", newId );
	}
	
	public static synchronized void saveNextCategoryId( Integer newId ) {
		saveNextEntityId( "Category", newId );
	}

	public static synchronized void saveNextCollectionId( Integer newId ) {
		saveNextEntityId( "Collection", newId );
	}
	
	public static synchronized void saveNextPackageId( Integer newId ) {
		saveNextEntityId( "Package", newId );
	}
	
	
	private static Integer getNextEntityId( String entity ) {
		DatabaseConnection dc	= DatabaseConnectionPool.getPool().getMasterDbConnection();
		
		DataDictionary dd	= new DataDictionary();
		dd.paramName	= "Last_" + entity + "ID";
		
		DatabaseCallParams params = dd.prepareForFetch();
		ArrayList<ModelObject> arr = dc.fetch(params);
		
		if( arr.size() > 0 ) {
			DataDictionary ddFetched = (DataDictionary)arr.get(0);
			
			return (Integer.parseInt(ddFetched.paramValue) + 1);
		}
		
		return -1;
	
	}	
	
	private static void saveNextEntityId( String entity, Integer newId ) {
		DatabaseConnection dc	= DatabaseConnectionPool.getPool().getMasterDbConnection();
		
		DataDictionary dd	= new DataDictionary();
		dd.paramValue	= String.valueOf(newId);
		
		DatabaseCallParams params = dd.prepareForSave(true);
		params.addCondition( new DatabaseCallParams.ConditionEquals("paramName", "Last_" + entity + "ID") );

		dc.execute(params);
	}
}
