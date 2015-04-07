package diagrepServer.database.model;

import diagrepServer.database.core.ModelObject;

public class DbFileMapping extends ModelObject {

	public String 	textPart		= null;
	
	public String 	databaseFileName 	= null;
	
	public Integer	startNumber		= null;
	
	public Integer	endNumber		= null;
	
	public Integer	type			= null;
	
	public String getDbFileName() {
		StringBuilder sb = new StringBuilder();
		
		if( type == 0 ) {
			sb.append( "dd_" );
		} else {
			sb.append( "cust_" );
		}
		sb.append( textPart );
		sb.append( "_" );
		sb.append( String.valueOf(startNumber) );
		sb.append( "_" );
		sb.append( String.valueOf(endNumber) );
		sb.append( ".db" );
		
		return sb.toString();
	}
}
