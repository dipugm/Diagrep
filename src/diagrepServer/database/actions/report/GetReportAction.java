package diagrepServer.database.actions.report;

import java.util.ArrayList;

import diagrepServer.Utils.DatabaseUtility;
import diagrepServer.database.actions.BaseAction;
import diagrepServer.database.core.DatabaseCallParams;
import diagrepServer.database.core.DatabaseConnection;
import diagrepServer.database.core.DatabaseConnectionPool;
import diagrepServer.database.core.ModelObject;
import diagrepServer.database.model.ReportDetailsObject;
import diagrepServer.database.model.ReportObject;

public class GetReportAction extends BaseAction {

	String billNumber;
	public GetReportAction( String billNumber ) {
		this.billNumber 	= billNumber;
	}
	
	public Object doAction() {
		String fileName 	= DatabaseUtility.getDbFileNameForIdAndType(billNumber, 0);
		
		if( fileName != null && !fileName.isEmpty() ) {
			DatabaseConnection dc = DatabaseConnectionPool.getPool().getConnectionForDbFile(fileName);
			ReportObject ro	= new ReportObject();
			ro.billNumber	= billNumber;
			DatabaseCallParams params = ro.prepareForFetch();
			
			ArrayList<ModelObject> arr = dc.fetch( params );
			
			if( arr.size() > 0 ) {
				ro 	= (ReportObject)arr.get( 0 );
				
				// Get the report details now.
				ReportDetailsObject rdo = new ReportDetailsObject();
				rdo.billNumber 	= billNumber;
				params	= rdo.prepareForFetch();
				
				ro.fk_reportDetails	= new ArrayList<ReportDetailsObject>();
				arr	= dc.fetch(params);
				
				for( int i=0; i< arr.size(); i++ ) {
					ro.fk_reportDetails.add( (ReportDetailsObject)arr.get(i) );
				}
				
				return ro;
			}
		}
		
		return null;
	}
}
