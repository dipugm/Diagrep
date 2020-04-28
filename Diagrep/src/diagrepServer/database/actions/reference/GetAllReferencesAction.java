package diagrepServer.database.actions.reference;

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
import diagrepServer.database.model.ReferenceObject;

public class GetAllReferencesAction extends BaseAction {

	public Object doAction() {
	
		DatabaseConnection dc	= DatabaseConnectionPool.getPool().getMasterDbConnection();

		ReferenceObject ro = new ReferenceObject();
		DatabaseCallParams params = ro.prepareForFetch();
		params.orderByClause = "name";
			
		ArrayList<?> references = dc.fetch(params);
		
		return references;
	}
}
