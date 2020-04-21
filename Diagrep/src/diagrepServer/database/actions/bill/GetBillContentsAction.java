package diagrepServer.database.actions.bill;

import java.util.ArrayList;

import diagrepServer.CommonDefs;
import diagrepServer.Utils.DatabaseUtility;
import diagrepServer.database.actions.BaseAction;
import diagrepServer.database.core.DatabaseCallParams;
import diagrepServer.database.core.DatabaseConnection;
import diagrepServer.database.core.DatabaseConnectionPool;
import diagrepServer.database.core.ModelObject;
import diagrepServer.database.model.BillDetailsObject;
import diagrepServer.database.model.BillObject;

public class GetBillContentsAction extends BaseAction {

	String billNumber;
	public GetBillContentsAction( String billNumber ) {
		this.billNumber	= billNumber;
	}
	
	public Object doAction() {
		
		String fileName = DatabaseUtility.getDbFileNameForIdAndType(billNumber, CommonDefs.BILLREPORT_TYPE);
		if( fileName != null && ! fileName.isEmpty()) {
			// We first get the bill information from Bill table.
			DatabaseConnection dc 	= DatabaseConnectionPool.getPool().getConnectionForDbFile(fileName);
			BillObject bo = new BillObject();
			bo.billNumber	= billNumber;
			DatabaseCallParams params = bo.prepareForFetch();
			
			ArrayList<ModelObject> arr = dc.fetch(params);
			if( arr.size() > 0 ) {
				
				// Now just get the HTML version of the bill
				bo = (BillObject)arr.get(0);
			
				BillDetailsObject bdo = new BillDetailsObject();
				bdo.billNumber = billNumber;
				params = bdo.prepareForFetch();
				
				arr = dc.fetch(params);
				
				bo.fk_arrayBillDetails = new ArrayList<BillDetailsObject>();
				for( int i=0; i < arr.size(); i++ ) {
					bo.fk_arrayBillDetails.add( (BillDetailsObject)arr.get(i) );
				}
				
				return bo;
			}
		}
		
		return null;
	}
}
