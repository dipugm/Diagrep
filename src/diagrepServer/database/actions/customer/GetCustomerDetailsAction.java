package diagrepServer.database.actions.customer;

import java.util.ArrayList;

import diagrepServer.CommonDefs;
import diagrepServer.Utils.DatabaseUtility;
import diagrepServer.Utils.DiagrepConfig;
import diagrepServer.database.actions.BaseAction;
import diagrepServer.database.core.DatabaseCallParams;
import diagrepServer.database.core.DatabaseConnection;
import diagrepServer.database.core.DatabaseConnectionPool;
import diagrepServer.database.core.ModelObject;

import diagrepServer.database.model.CustomerObject;

public class GetCustomerDetailsAction extends BaseAction {

	String customerId;
	public GetCustomerDetailsAction( String customerId ) {
		this.customerId = customerId;
	}
	
	public Object doAction() {
		
		if( checkCustomerIdValidity() == false ) {
			return null;
		}
		
		String fileName = DatabaseUtility.getDbFileNameForIdAndType( this.customerId, CommonDefs.CUSTOMER_TYPE );
		// If there is no bill for this customer Id, we try to search the imported customer Db.
		if( fileName == null || fileName.isEmpty() ) {
			fileName = DiagrepConfig.getConfig().get( DiagrepConfig.CUSTOMER_DB_IMPORTED );
		}
		// We first get the bill information from Bill table.
		DatabaseConnection dc 	= DatabaseConnectionPool.getPool().getConnectionForDbFile(fileName);
		CustomerObject co = new CustomerObject();
		co.customerId	= this.customerId;
		DatabaseCallParams params = co.prepareForFetch();
		
		ArrayList<ModelObject> arr = dc.fetch(params);
		if( arr.size() > 0 ) {
			co = (CustomerObject)arr.get(0);
			return co;
		}
		
		return null;
	}
	
	private boolean checkCustomerIdValidity() {
		return (this.customerId.matches("[A-Z]+-[0-9]+"));
	}
}
