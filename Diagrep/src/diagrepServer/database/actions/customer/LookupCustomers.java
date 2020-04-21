package diagrepServer.database.actions.customer;

import java.util.ArrayList;
import java.util.HashMap;

import diagrepServer.Utils.DiagrepConfig;
import diagrepServer.database.actions.BaseAction;
import diagrepServer.database.core.DatabaseCallParams;
import diagrepServer.database.core.DatabaseCallParams.ConditionLike;
import diagrepServer.database.core.DatabaseConnection;
import diagrepServer.database.core.DatabaseConnectionPool;
import diagrepServer.database.core.ModelObject;
import diagrepServer.database.model.CustomerObject;
import diagrepServer.database.model.DbFileMapping;

public class LookupCustomers extends BaseAction {

	public enum SearchType {
		kCustomerId,
		kCustomerName
	}

	String searchString;
	SearchType searchType;
	boolean searchOldDbAlso;
	
	public LookupCustomers( String searchString, SearchType searchType, boolean searchOldDbAlso ) {
		this.searchString		= searchString;
		this.searchType			= searchType;
		this.searchOldDbAlso	= searchOldDbAlso;
	}
	
	public Object doAction() {
		
		HashMap<String, ArrayList<CustomerObject> > searchedCustomers = new HashMap<String, ArrayList<CustomerObject> >();
		
		if( this.searchOldDbAlso ) {
			// We need to search all the Db files to get a list of customers.
			String fileName = "custdata_imported.db";
			ArrayList<CustomerObject> cosOld = searchForCustomersInDbFile( fileName );
			searchedCustomers.put( "customers_legacy", cosOld);
		}
		
		DbFileMapping dfm = new DbFileMapping();
		dfm.type 	= 1;	// customers;
		
		DatabaseCallParams paramsFN = dfm.prepareForFetch();
		ArrayList<ModelObject> arrFileMappings = DatabaseConnectionPool.getPool().getMasterDbConnection().fetch(paramsFN);
		
		ArrayList<CustomerObject> arrCustomers = new ArrayList<CustomerObject>();
		for( int i=0; i < arrFileMappings.size(); i++ ) {
			DbFileMapping dfmInt = (DbFileMapping)arrFileMappings.get( i );
			arrCustomers.addAll( searchForCustomersInDbFile( dfmInt.databaseFileName ) );
		}
		
		int limit = 500;
		String sz = DiagrepConfig.getConfig().get( DiagrepConfig.CUSTOMER_SEARCH_LIMIT);
		if( sz != null ) {
			limit = Integer.parseInt( sz );
		}
		if( arrCustomers.size() > limit ) {
			arrCustomers = (ArrayList<CustomerObject>)arrCustomers.subList(0,  limit);
		}
		// Return only 100 search hits.
		searchedCustomers.put( "customers", arrCustomers );
		
		return searchedCustomers;
	}
	
	private ArrayList<CustomerObject> searchForCustomersInDbFile( String dbFile ) {
		
		DatabaseConnection dc 		= DatabaseConnectionPool.getPool().getConnectionForDbFile( dbFile );
		CustomerObject co 			= new CustomerObject();
		DatabaseCallParams params	= co.prepareForFetch();
		
		switch( this.searchType ) {
		case kCustomerId:
			params.addCondition( new ConditionLike( "customerId", searchString ) );
			break;
			
		case kCustomerName:
			params.addCondition( new ConditionLike( "name", searchString ) );
			break;
		}
		
		ArrayList<ModelObject> arr = dc.fetch(params);
		ArrayList<CustomerObject> arrOut = new ArrayList<CustomerObject>();
		
		int limit = 500;
		String sz = DiagrepConfig.getConfig().get( DiagrepConfig.CUSTOMER_SEARCH_LIMIT);
		if( sz != null ) {
			limit = Integer.parseInt( sz );
		}
		
		for( int i=0; i < arr.size(); i++ ) {
			arrOut.add( (CustomerObject) arr.get(i) );
			
			if( i > limit ) {
				break;
			}
		}
		
		return arrOut;
	}
}
