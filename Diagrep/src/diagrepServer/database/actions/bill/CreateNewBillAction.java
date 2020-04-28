package diagrepServer.database.actions.bill;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.simple.JSONArray;

import diagrepServer.CommonDefs;
import diagrepServer.Utils.DataDictionaryUtitlities;
import diagrepServer.Utils.DatabaseUtility;
import diagrepServer.database.actions.BaseAction;
import diagrepServer.database.actions.CommonDefs.EntityType;
import diagrepServer.database.actions.category.GetSingleCategoryAction;
import diagrepServer.database.actions.collection.GetSingleCollectionAction;
import diagrepServer.database.actions.customer.GetCustomerDetailsAction;
import diagrepServer.database.actions.packages.GetSinglePackageAction;
import diagrepServer.database.actions.reference.StoreReferenceAction;
import diagrepServer.database.actions.test.GetSingleTestAction;
import diagrepServer.database.core.DatabaseCallParams;
import diagrepServer.database.core.DatabaseConnection;
import diagrepServer.database.core.DatabaseConnectionPool;
import diagrepServer.database.core.ModelObject;
import diagrepServer.database.core.DatabaseConnection.EnumDBResult;
import diagrepServer.database.model.BillDetailsObject;
import diagrepServer.database.model.BillObject;
import diagrepServer.database.model.CategoryObject;
import diagrepServer.database.model.CollectionObject;
import diagrepServer.database.model.CustomerObject;
import diagrepServer.database.model.PackageObject;
import diagrepServer.database.model.ReportDetailsObject;
import diagrepServer.database.model.ReportObject;
import diagrepServer.database.model.TestObject;

public class CreateNewBillAction extends BaseAction {

	private BillObject bill;
	private JSONArray entities;
	
	public CreateNewBillAction (String customerId, double advance, long reportDate, 
			String reference, JSONArray entities) {
		
		this.bill = new BillObject();
		this.bill.customerId	= customerId;
		this.bill.referredBy	= reference;
		this.bill.advancePaid	= advance;
		this.bill.reportDate	= reportDate;
		this.bill.billDate 		= (new Date()).getTime();
		
		this.bill.billNumber	= DataDictionaryUtitlities.getNextBillNumber( null );
		
		this.entities			= entities;
	}
	
	@SuppressWarnings("unchecked")
	public Object doAction() {
		
		CustomerObject co = (CustomerObject)new GetCustomerDetailsAction( this.bill.customerId ).doAction();
		if( co == null ) {
			return null;
		}
		
		EnumDBResult ret = EnumDBResult.DB_SUCCESS;
		int tryCount = 0;
		
		do {
			
			String dbFileName = DatabaseUtility.getOrCreateDbFileNameForIdAndType( this.bill.billNumber,CommonDefs.BILLREPORT_TYPE );
			DatabaseConnection dc 	= DatabaseConnectionPool.getPool().getConnectionForDbFile( dbFileName );
			DatabaseCallParams params = this.bill.prepareForSave( false );
			
			ret = dc.execute(params);
			
			if( EnumDBResult.DB_SUCCESS == ret ) {
				
				// Make an entry into the report table.
				ReportObject ro = new ReportObject();
				ro.billNumber	= this.bill.billNumber;
				
				params = ro.prepareForSave( false );
				dc.execute( params );
				
				// Make entries into the bill details and report details table.
				for( int iEnt=0; iEnt < this.entities.size(); iEnt++ ) {
					HashMap<String, Object> entMap = (HashMap<String, Object>)this.entities.get( iEnt );
					
					BillDetailsObject bdo = new BillDetailsObject();
					bdo.billNumber	= this.bill.billNumber;
					bdo.entityId	= Integer.parseInt( String.valueOf(entMap.get( "id" )) );
					bdo.entityType  = Integer.parseInt( String.valueOf(entMap.get( "type")) );
					bdo.cost		= Double.parseDouble(entMap.get("cost").toString());
					
					DatabaseCallParams paramsDetails = bdo.prepareForSave( false );
					dc.execute( paramsDetails );
					
					if( bdo.entityType == EntityType.Test.ordinal() ) {
						TestObject to = (TestObject)new GetSingleTestAction( bdo.entityId ).doAction();
						if( to != null ) {
							ReportDetailsObject rdo = new ReportDetailsObject();
							rdo.billNumber	= this.bill.billNumber;
							rdo.entityId 	= to.id;
							
							paramsDetails	= rdo.prepareForSave( false );
							dc.execute( paramsDetails );	
						} else {
							System.out.println( "No Test with id : " + String.valueOf( bdo.entityId ) );
						}
						
					} else {
						ArrayList<ModelObject> tests = 
							getTestsForEntity( 
									bdo.entityId, 
									EntityType.fromValue( String.valueOf(bdo.entityType)) );
						
						for( int iTest=0; iTest < tests.size(); iTest++ ) {
							
							TestObject to 	= (TestObject)tests.get( iTest );
							ReportDetailsObject rdo = new ReportDetailsObject();
							rdo.billNumber	= this.bill.billNumber;
							rdo.entityId 	= to.id;
							
							paramsDetails	= rdo.prepareForSave( false );
							dc.execute( paramsDetails );
						}
					}
				}
				
				// Save the bill number back to the Db.
				DataDictionaryUtitlities.storeNextBillNumber( this.bill.billNumber );
				
				new StoreReferenceAction(this.bill.referredBy).doAction();
				
			} else {
				// Since we violated the unique bill number constraint, we try the next number. 
				this.bill.billNumber	= DataDictionaryUtitlities.getNextBillNumber( this.bill.billNumber );
			}
			
		} while( (ret != EnumDBResult.DB_SUCCESS) && (tryCount++ < 5) );
		
		return this.bill.billNumber;
	}
	
	private ArrayList<ModelObject> getTestsForEntity( int entityId, EntityType entityType ) {
		ArrayList<ModelObject> arr = new ArrayList<ModelObject>();
		switch( entityType ) {
		case Test:
			TestObject to = (TestObject)new GetSingleTestAction( entityId ).doAction();
			if( to != null ) {
				arr.add( to );
			}
			break;
			
		case Category:
			CategoryObject co = (CategoryObject)new GetSingleCategoryAction( entityId ).doAction();
			if( co != null ) {
				arr.addAll( co.getContainedTests() );
			}
			break;
			
		case Collection:
			CollectionObject clo = (CollectionObject)new GetSingleCollectionAction( entityId ).doAction();
			if( clo != null ) {
				arr.addAll( clo.getContainedTests() );
			}
			break;
			
		case Package:
			PackageObject po = (PackageObject)new GetSinglePackageAction( entityId ).doAction();
			if( po != null ) {
				arr.addAll( po.getContainedTests() );
			}
			break;

		default:
			break;
		}
		
		return arr;
	}
	
	
}
