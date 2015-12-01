package diagrepServer.database.actions.test;

import diagrepServer.Utils.DatabaseUtility;
import diagrepServer.database.actions.BaseAction;
import diagrepServer.database.core.DatabaseCallParams;
import diagrepServer.database.core.DatabaseCallParams.ConditionEquals;
import diagrepServer.database.core.DatabaseConnection.EnumDBResult;
import diagrepServer.database.core.DatabaseConnection;
import diagrepServer.database.core.DatabaseConnectionPool;
import diagrepServer.database.model.TestObject;

public class AddOrModifyTestAction extends BaseAction {

	private TestObject testObject;
	boolean isForModify;
	private Integer testIdForModify;
	
	public AddOrModifyTestAction( String name, Double cost, String method, String normalValue, String unit, Integer testId ) {
		this.testObject	= new TestObject();
		this.testObject.name	= name;
		this.testObject.cost	= cost;
		this.testObject.method	= method;
		this.testObject.normalValue	= normalValue;
		this.testObject.unit	= unit;
		
		this.testIdForModify 	= testId;
		
		if( testId == null ) {
			this.testObject.id 	= DatabaseUtility.getNextTestId(); 
		} else {
			this.isForModify	= true;	// valid if this is a modify
		}
	}
	
	public Object doAction() {
		
		DatabaseConnection dc 	= DatabaseConnectionPool.getPool().getMasterDbConnection();
		DatabaseCallParams params = this.testObject.prepareForSave( this.isForModify );
		if( this.isForModify ) {
			params.addCondition( new ConditionEquals("id", this.testIdForModify ) );
		}
		
		if( EnumDBResult.DB_SUCCESS == dc.execute( params ) ) {
			if( !this.isForModify ) {
				DatabaseUtility.saveNextTestId( this.testObject.id );
				return this.testObject.id;
			} else {
				return true;
			}
		}
		
		return null;
	}
}
