package diagrepServer.database.actions.reference;

import java.util.ArrayList;

import diagrepServer.database.actions.BaseAction;
import diagrepServer.database.core.DatabaseCallParams;
import diagrepServer.database.core.DatabaseConnection;
import diagrepServer.database.core.DatabaseConnectionPool;
import diagrepServer.database.model.ReferenceObject;

public class StoreReferenceAction extends BaseAction {
	private String referenceName;
	
	public StoreReferenceAction( String referenceName) {
		super();
		
		this.referenceName = referenceName;
	}

	public Object doAction() {
		
		if( this.referenceName.toUpperCase().trim() == "SELF" ) {
			return false;
		}
		
		DatabaseConnection dc	= DatabaseConnectionPool.getPool().getMasterDbConnection();

		ReferenceObject ro = new ReferenceObject();
		DatabaseCallParams params = ro.prepareForFetch();
		params.addCondition( new DatabaseCallParams.ConditionEquals("name", this.referenceName) );
		
			
		ArrayList<?> references = dc.fetch(params);
		
		if( references.size() == 0 ) {
			ro.name = this.referenceName;
			params = ro.prepareForSave(false);
			
			dc.execute(params);
			return true;
		}
		
		return false;
	}
}
