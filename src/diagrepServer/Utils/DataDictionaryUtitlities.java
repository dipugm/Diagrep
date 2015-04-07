package diagrepServer.Utils;

import java.util.ArrayList;

import diagrepServer.database.core.DatabaseCallParams;
import diagrepServer.database.core.DatabaseConnection;
import diagrepServer.database.core.DatabaseConnectionPool;
import diagrepServer.database.core.ModelObject;
import diagrepServer.database.model.DataDictionary;

public class DataDictionaryUtitlities {

	public static void main( String[] args ) {
		String[] inputs = {"A00022", "A99999", "BZ99999", "BB37483"};
		
		for( int i=0; i < inputs.length; i++ ) {
			String billNum = DataDictionaryUtitlities .getNextStringInSequenceWithStringManipulation( inputs[i] );
			System.out.println( inputs[i] + " => " + billNum );
		}
		
	}
	
	public static String getNextBillNumber() {
		return getDataFromDb( "Last_Bill_Number" );
	}
	
	public static String getNextCustomerNumber() {
		return getDataFromDb( "Last_Customer_ID" );
	}
	
	public static void storeNextBillNumber( String billNumber ) {
		storeDataInDb( "Last_Bill_Number", billNumber );
	}
	
	public static void storeNextCustomerId( String customerIdSuffixPart ) {
		storeDataInDb( "Last_Customer_ID", customerIdSuffixPart );
	}
	
	private static String getNextStringInSequenceWithStringManipulation( String inputString ) {
		
		// Lets get the alphabet and number parts.
		String[] splitStrings = StringUtilities.splitTextAndNumberParts( inputString );
		
		String alpPart = splitStrings[0];
		long num = Long.parseLong( splitStrings[1] );
		
		if( ++num > 99999 ) {
			num = 1;

			// Reverse the string in alphabet part.
			StringBuffer sb = new StringBuffer( splitStrings[0] );
			sb.reverse();

			StringBuffer sbOut = new StringBuffer();
			
			// go through each alphabet checking if it needs to be changed to the next alphabet.
			// If the current character is more than Z, then that character will reset to A while
			// the next character will more on to next character. The same check is again done for 
			// the next character also.
			boolean shouldIncrement = true;
			for( int i=0; i < sb.length(); i++) {
				
				char ch = sb.charAt(i);
				if( shouldIncrement ) {
					ch++;
					shouldIncrement = false;
				}
				
				if( ch > 'Z' ) {
					ch = 'A';
					shouldIncrement = true;
				} else {
					shouldIncrement = false;
				}
				
				sbOut.append( ch );
			}
			
			// All characters in the existing string have reached Z, so we append
			// a new A to the string.
			if( shouldIncrement ) {
				sbOut.append( 'A' );
			}
			
			alpPart = sbOut.reverse().toString();
		} 
		
		String numPartAsString = String.valueOf(num+100000).substring(1);
		
		return alpPart + numPartAsString;
	}
	
	private static String getDataFromDb( String key ) {
		// Get the master Db connection.
		DatabaseConnection dc	= DatabaseConnectionPool.getPool().getMasterDbConnection();
		DataDictionary dd = new DataDictionary();
		dd.paramName = key;
		DatabaseCallParams params = dd.prepareForFetch();
		
		ArrayList<ModelObject> arr = dc.fetch(params);
		
		if( arr.size() > 0 ) {
			DataDictionary ddOut = (DataDictionary)arr.get(0);
			return getNextStringInSequenceWithStringManipulation( ddOut.paramValue );
		}
		
		return "";
	}
	
	private static void storeDataInDb( String key, String value ) {
		DatabaseConnection dc	= DatabaseConnectionPool.getPool().getMasterDbConnection();
		DataDictionary dd = new DataDictionary();
		dd.paramName = key;
		DatabaseCallParams params = dd.prepareForFetch();
		
		ArrayList<ModelObject> arr = dc.fetch(params);
		dd = (DataDictionary)arr.get(0);
		dd.paramValue	= value;
		
		params = dd.prepareForSave( false );

		dc.execute( params );
		
	}
}
