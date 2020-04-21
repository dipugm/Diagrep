package diagrepServer.database.actions.report;

import diagrepServer.CommonDefs;
import diagrepServer.Utils.DatabaseUtility;
import diagrepServer.database.actions.BaseAction;
import diagrepServer.database.core.DatabaseCallParams;
import diagrepServer.database.core.DatabaseCallParams.*;
import diagrepServer.database.core.DatabaseConnection.EnumDBResult;
import diagrepServer.database.core.DatabaseConnection;
import diagrepServer.database.core.DatabaseConnectionPool;
import diagrepServer.database.model.ReportDetailsObject;
import diagrepServer.database.model.ReportObject;

public class ModifyReportAction extends BaseAction {

	String billNumber;
	String recommendation;
	String value;
	int testId;
	int isHighlighted;
	String description;
	boolean updateTestAlso;
	
	public ModifyReportAction( String billNumber, String reco ) {
		this.billNumber 	= billNumber;
		this.recommendation = reco;
		this.updateTestAlso = false;
	}
	
	public ModifyReportAction( String billNumber, String reco , int testid, String value, 
			String description, int isHighlighted) {
		this.billNumber		= billNumber;
		this.recommendation	= reco;
		this.testId			= testid;
		this.value			= value;
		this.isHighlighted	= isHighlighted;
		this.description	= description;
		
		this.updateTestAlso	= true;
	}
	
	public Object doAction() {
		
		String fileName = DatabaseUtility.getDbFileNameForIdAndType( billNumber, CommonDefs.BILLREPORT_TYPE );
		
		String error	= "";
		if( fileName == null || fileName.isEmpty() ) {
			error = "Bill archived.";
			
		} else {
			DatabaseConnection dc = DatabaseConnectionPool.getPool().getConnectionForDbFile( fileName );
			
			
			if( this.recommendation != null ) {
				
				ReportObject report = new ReportObject();
				
				// Store the recommendation as base 64 string in the Db.
				report.recommendations	= javax.xml.bind.DatatypeConverter.printBase64Binary( this.recommendation.getBytes() );
				DatabaseCallParams params = report.prepareForSave( true );
				params.addCondition( new ConditionEquals("billNumber", this.billNumber) );
				
				if( EnumDBResult.DB_SUCCESS != dc.execute( params ) ) {
					error 	= "Failed to update recommendations. ";
				}
			}
			
			if( updateTestAlso ) {
				ReportDetailsObject rdo = new ReportDetailsObject();
				rdo.entityValue		= javax.xml.bind.DatatypeConverter.printBase64Binary( this.value.getBytes() );
				rdo.isHighlighted	= this.isHighlighted;
				rdo.description		= javax.xml.bind.DatatypeConverter.printBase64Binary( this.description.getBytes() );
				
				DatabaseCallParams params = rdo.prepareForSave( true );
				
				params.addCondition( new ConditionEquals("billNumber", this.billNumber) );
				params.addCondition( new ConditionEquals("entityId", this.testId) );
				
				if( EnumDBResult.DB_SUCCESS != dc.execute( params ) ) {
					error += "Failed to update report details.";
				}
			}
		}
		
		if( error.isEmpty() ) {
			return null;
		}
		return error;
	}
}
