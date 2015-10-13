package diagrepServer.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

public class StringUtilities {
	
	public static void main( String[] args ) {
//		String[] szs = {"KDRC-1222", "KDD-FFF32123", "990-S", "DFFF" };
//		
//		for( int i=0; i < szs.length; i++ ) {
//			String s = szs[i];
//			if( s.matches( "[A-Z]+-[A-Z]+[0-9]+") ) {
//				System.out.println( s + " matches the pattern" );
//			} else {
//				System.out.println( s + " DOES NOT match the pattern" );
//			}
//		}
	}
	
	public static boolean isBillNumber( String billNumber ) {
		String pattern = "[A-Z]+[0-9]+";
		
		return billNumber.toUpperCase().matches( pattern );
	}
	
	public static boolean isCustomerId( String custId ) {
		String patternOld = "[A-Z]+-[0-9]+";	// KDRC-1002
		String patternNew = "[A-Z]+-[A-Z]+[0-9]+";	// KDRC-A1222;
		
		custId = custId.toUpperCase();
		return (custId.matches( patternOld ) || custId.matches( patternNew));
	}

	public static String[] splitTextAndNumberParts( String combinedString ) {
			
		String[] ret = {};
				
		Pattern pattern = Pattern.compile( "[0-9]+" );
		
		String textPart = "";
		String numPart	= "";
		String[] sp = pattern.split( combinedString );
		if( sp.length > 0 ) {
			textPart = pattern.split( combinedString )[0];
		
			Pattern numPattern = Pattern.compile( "[A-Z]+" );
			sp = numPattern.split( combinedString );
			
			if( sp.length > 1 ) {	// Has number part also
				// 	Converting to int gets rid of extra 0s
				int np = Integer.parseInt( sp[1] );
				numPart = String.valueOf( np );
			}
			
			ret = new String[] {textPart, numPart };
		}
		 
		return ret;
	}
	
	public static String addStringIfNotExisting( String stringToAdd , String commaSeparatedString ) {
		String resultString = "";
		if( commaSeparatedString == null ) {
			return stringToAdd;
		}
		
		String[] splitStrings = commaSeparatedString.split( "," );
		
		boolean exists = false;
		for( int i=0; i < splitStrings.length; i++ ) {
			if( stringToAdd.equalsIgnoreCase( splitStrings[i] ) ) {
				exists = true;
				break;
			}
		}
		
		if( !exists ) {
			if( splitStrings.length > 0 ) {
				commaSeparatedString += ",";
			}
			commaSeparatedString += stringToAdd;
			
			return commaSeparatedString;
		}
		
		return resultString;
	}
	
	public static String removeStringFromCommaSeparatedString( String stringToRemove, String commaSeparatedString ) {
		
		StringBuilder sb = new StringBuilder();
		String[] splitStrings = commaSeparatedString.split( "," );
		
		for( int i=0; i < splitStrings.length; i++ ) {
			if( !stringToRemove.equalsIgnoreCase( splitStrings[i] ) ) {
				if( sb.length() > 0 ) {
					sb.append( "," );
				}
				sb.append( splitStrings[i] );
			}
		}
		
		return sb.toString();
	}
	
	public static String decodeURLEncodedString( String input ) {
		try {
			input 	= URLDecoder.decode(input, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			System.err.println( "Exception converting url encoded string : " + input );
		}
		
		return input;
	}
		
}
