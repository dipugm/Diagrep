package diagrepServer.database.actions.test;

import java.util.ArrayList;

import diagrepServer.database.actions.BaseAction;
import diagrepServer.database.core.DatabaseCallParams;
import diagrepServer.database.core.DatabaseConnection;
import diagrepServer.database.core.DatabaseConnectionPool;
import diagrepServer.database.model.TestObject;

public class GetSingleTestAction extends BaseAction {

	private int testId;
	
	public GetSingleTestAction( int testId ) {
		this.testId		= testId;
	}
	
	public Object doAction() {
		DatabaseConnection dc 	= DatabaseConnectionPool.getPool().getMasterDbConnection();
		
		TestObject toTemp 	= new TestObject();
		toTemp.id			= this.testId;
		DatabaseCallParams params = toTemp.prepareForFetch();
		params.deriveConditions( true );
		
		ArrayList<?> tests 	= dc.fetch(params);
		if( tests.size() > 0 ) {
			return tests.get(0);
		}
		
		return null;
	}
}
