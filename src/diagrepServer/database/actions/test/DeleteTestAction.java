package diagrepServer.database.actions.test;

import diagrepServer.database.actions.BaseAction;
import diagrepServer.database.actions.CommonDefs.EntityType;
import diagrepServer.database.core.DatabaseCallParams;
import diagrepServer.database.core.DatabaseConnection;
import diagrepServer.database.core.DatabaseConnectionPool;
import diagrepServer.database.model.TestObject;

public class DeleteTestAction extends BaseAction {

	private Integer testId;
	public DeleteTestAction( Integer testId ) {
		this.testId		= testId;
	}
	
	public Object doAction() {
		TestObject to = new TestObject();
		to.id 	= testId;
		
		DatabaseConnection dc 	= DatabaseConnectionPool.getPool().getMasterDbConnection();
		DatabaseCallParams params = to.prepareForDelete();
		
		if( dc.execute( params ) ) {
			
			// Clear entities relation table to remove all entries where the sub category is
			// this id.
			super.clearEntitiesRelationsForSubEntity( EntityType.Test, testId );
						
			return true;
		}
		
		return false;
	}
}
