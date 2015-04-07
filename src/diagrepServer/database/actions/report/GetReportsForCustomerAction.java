package diagrepServer.database.actions.report;

import java.util.ArrayList;

import diagrepServer.CommonDefs;
import diagrepServer.Utils.DatabaseUtility;
import diagrepServer.database.actions.BaseAction;
import diagrepServer.database.core.DatabaseCallParams;
import diagrepServer.database.core.DatabaseConnection;
import diagrepServer.database.core.DatabaseConnectionPool;
import diagrepServer.database.core.ModelObject;
import diagrepServer.database.model.BillObject;
import diagrepServer.database.model.DbFileMapping;

public class GetReportsForCustomerAction extends BaseAction {

	String customerId;
	
	public GetReportsForCustomerAction( String custId ) {
		this.customerId		= custId;
	}
	
	public Object doAction() {
		
		// Get file names of all dd_ db files.
		DatabaseConnection dcMaster = DatabaseConnectionPool.getPool().getMasterDbConnection();
		DbFileMapping dfm = new DbFileMapping();
		dfm.type = CommonDefs.BILLREPORT_TYPE;
		DatabaseCallParams params = dfm.prepareForFetch();

		ArrayList<ModelObject> arr = dcMaster.fetch( params );
		ArrayList<BillObject> arrayBills = new ArrayList<BillObject>(); 
		// Is each, search if there are any bills for the given customer id.
		for( int i=0; i < arr.size(); i++ ) {
			dfm  = (DbFileMapping)arr.get( i );
			
			if( DatabaseUtility.doesDbFileExist( dfm.databaseFileName ) ) {
				DatabaseConnection dc = DatabaseConnectionPool.getPool().getConnectionForDbFile( dfm.databaseFileName );
				BillObject bo = new BillObject();
				bo.customerId = this.customerId;
				DatabaseCallParams dcp = bo.prepareForFetch();
				
				ArrayList<ModelObject> bills = dc.fetch( dcp );
				for( int j=0; j < bills.size(); j++ ) {
					arrayBills.add( (BillObject)bills.get( j ));
				}
			}
		}
		
		return arrayBills;
		
	}
}
