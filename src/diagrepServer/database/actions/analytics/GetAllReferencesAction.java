package diagrepServer.database.actions.analytics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import diagrepServer.CommonDefs;
import diagrepServer.Utils.DatabaseUtility;
import diagrepServer.database.actions.BaseAction;
import diagrepServer.database.core.DatabaseCallParams;
import diagrepServer.database.core.DatabaseConnection;
import diagrepServer.database.core.DatabaseConnectionPool;
import diagrepServer.database.model.BillObject;

public class GetAllReferencesAction extends BaseAction {

	public Object doAction() {
	
		ArrayList<String> dbFileNames = DatabaseUtility.getDbFileNamesForType( CommonDefs.BILLREPORT_TYPE );
		
		Hashtable<String, Boolean> uniqueRefNames = new Hashtable<String, Boolean>();
		for( int i=0; i < dbFileNames.size(); i++ ) {
			
			DatabaseConnection dc	= DatabaseConnectionPool.getPool().getConnectionForDbFile( dbFileNames.get(i) );

			BillObject bo = new BillObject();
			DatabaseCallParams params = bo.prepareForFetch();
			params.addParam("referredBy", "");
			params.shouldFetchUniqueObjects = true;
			
			ArrayList<?> objects = dc.fetch(params);
			
			for( int iNameIndex=0; iNameIndex < objects.size(); iNameIndex++ ) {
				BillObject boInt = (BillObject)objects.get( iNameIndex );
				uniqueRefNames.put( boInt.referredBy, true);
			}
		}
		
		Object[] names = uniqueRefNames.keySet().toArray();  
		Arrays.sort( names );
		
		return names;
	}
}
