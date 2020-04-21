package diagrepServer.database.actions.test;

import java.util.ArrayList;

import diagrepServer.database.actions.BaseAction;
import diagrepServer.database.core.DatabaseCallParams;
import diagrepServer.database.core.DatabaseConnection;
import diagrepServer.database.core.DatabaseConnectionPool;
import diagrepServer.database.model.TestObject;

public class GetTestsAction extends BaseAction {

	public Object doAction() {
		// TODO Auto-generated method stub
		DatabaseConnection dc 	= DatabaseConnectionPool.getPool().getMasterDbConnection();
		
		TestObject toTemp = new TestObject();
		DatabaseCallParams params = toTemp.prepareForFetch();
		params.orderByClause = "name";
		
		ArrayList<?> tests 	= dc.fetch(params);
		return tests;
	}
	
	public ArrayList<TestObject> getTestsFromIds( String testIds ) {
		String[] tests = testIds.split(",");

		ArrayList<TestObject> arrayTests 	= new ArrayList<TestObject>();
		for( int iTest=0; iTest < tests.length; iTest++ ) {
			TestObject to = (TestObject) new GetSingleTestAction( Integer.valueOf(tests[iTest]) ).doAction();
			if( to != null ) {
				arrayTests.add( to );
			}
		}
		
		return arrayTests;
	}
}
