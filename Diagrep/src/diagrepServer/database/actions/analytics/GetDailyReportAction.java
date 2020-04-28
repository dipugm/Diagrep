package diagrepServer.database.actions.analytics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import diagrepServer.CommonDefs;
import diagrepServer.Utils.DatabaseUtility;
import diagrepServer.Utils.DiagrepConfig;
import diagrepServer.Utils.DiagrepTemplates;
import diagrepServer.Utils.DiagrepTemplates.TemplateType;
import diagrepServer.database.actions.BaseAction;
import diagrepServer.database.actions.bill.GetBillContentsAction;
import diagrepServer.database.actions.category.GetSingleCategoryAction;
import diagrepServer.database.actions.collection.GetSingleCollectionAction;
import diagrepServer.database.actions.customer.GetCustomerDetailsAction;
import diagrepServer.database.actions.packages.GetSinglePackageAction;
import diagrepServer.database.actions.test.GetSingleTestAction;
import diagrepServer.database.core.DatabaseCallParams;
import diagrepServer.database.core.DatabaseConnection;
import diagrepServer.database.core.DatabaseConnectionPool;
import diagrepServer.database.model.BillDetailsObject;
import diagrepServer.database.model.BillObject;
import diagrepServer.database.model.CategoryObject;
import diagrepServer.database.model.CollectionObject;
import diagrepServer.database.model.CustomerObject;
import diagrepServer.database.model.PackageObject;
import diagrepServer.database.model.TestObject;

public class GetDailyReportAction extends BaseAction {

	Long referenceDate;
	
	public GetDailyReportAction( Long date ) {
		this.referenceDate = date;
	}
	
	@SuppressWarnings("unchecked")
	public Object doAction() {
		ArrayList<String> dbFilenames = DatabaseUtility.getDbFileNamesForType( CommonDefs.BILLREPORT_TYPE );
		
		String dateFormat = DiagrepConfig.getConfig().get( DiagrepConfig.BILL_DATE_FORMAT ) ;
		SimpleDateFormat formatter = new SimpleDateFormat( dateFormat );
		
		System.out.print( "Generating daily report for " + formatter.format( new Date(this.referenceDate) ) );
		
		// Use the Calendar class to subtract one day
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( new Date( this.referenceDate ) );
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        
        Date refDate = calendar.getTime();
        
        // Use the date formatter to produce a formatted date string
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        Date previousDate = calendar.getTime();
		
		ArrayList<BillObject> matchingBills = new ArrayList<BillObject>();
		for( int iFile=0; iFile < dbFilenames.size(); iFile++ ) {
			DatabaseConnection dc = DatabaseConnectionPool.getPool().getConnectionForDbFile( dbFilenames.get(iFile));
			
			BillObject bo = new BillObject();
			
			DatabaseCallParams param = bo.prepareForFetch();
			param.orderByClause = "billDate";
			param.addCondition( new DatabaseCallParams.ConditionGreaterThanOrEqual("billDate", previousDate.getTime()) );
			param.addCondition( new DatabaseCallParams.ConditionLessThanOrEqual("billDate", refDate.getTime()) );
			
			ArrayList<?> bills = dc.fetch(param);
			matchingBills.addAll( (ArrayList<BillObject>)bills );
		}

		// Prepare the Html
		return ReportAsHtml( matchingBills ); 
		
	}
	
	private String ReportAsHtml( ArrayList<BillObject> bills ) {
		
		String refReportTempl = DiagrepTemplates.getInstance().getTemplateFor( TemplateType.kDailyReport );
		String refReportRowTempl = DiagrepTemplates.getInstance().getTemplateFor( TemplateType.kDailyReportRow ); 
		
		String dateFormat = DiagrepConfig.getConfig().get( DiagrepConfig.REPORT_DATE_FORMAT ) ;
		SimpleDateFormat formatter = new SimpleDateFormat( dateFormat );
		
		refReportTempl = refReportTempl.replace("!@#$Date$#@!", formatter.format( new Date(this.referenceDate)));
		
		double totalCost = 0.0;
		StringBuilder sb = new StringBuilder();
		for( int i=0; i < bills.size(); i++ ) {
			BillObject bo = bills.get(i);

			bo = (BillObject)new GetBillContentsAction( bo.billNumber ).doAction();

			String row = new String(refReportRowTempl);
			row = row.replace("%SlNo%", String.valueOf( i+1 ));
			row = row.replace("%BillNumber%", bo.billNumber );
			
			CustomerObject co = (CustomerObject)new GetCustomerDetailsAction( bo.customerId ).doAction();
			row = row.replace("%PatientName%", co.name);
			
			double cost = bo.getCost();
			row = row.replace("%Cost%", String.valueOf( cost ) );
			
			totalCost += cost;
			
			StringBuilder sb1 = new StringBuilder();
			for( int j=0; j < bo.fk_arrayBillDetails.size(); j++ ) {
				BillDetailsObject bdo = bo.fk_arrayBillDetails.get( j );
				
				if( j > 0 ) {
					sb1.append( "," );
				}
				String entityName = "-";
				switch( bdo.entityType ) {
				case 0:
					TestObject to = (TestObject)(new GetSingleTestAction( bdo.entityId ).doAction());
					if( to != null ) {
						entityName = to.name;
					}
					break;
				case 1:
					CategoryObject cao = (CategoryObject)(new GetSingleCategoryAction( bdo.entityId ).doAction());
					if( cao != null ) {
						entityName = cao.name;
					}
					break;
				case 2:
					CollectionObject coo = (CollectionObject)(new GetSingleCollectionAction( bdo.entityId ).doAction());
					if( coo != null ) {
						entityName = coo.name;
					}
					break;
				case 3:
					PackageObject po = (PackageObject)(new GetSinglePackageAction( bdo.entityId ).doAction());
					if( po != null ) {
						entityName = po.name;
					}
					break;
				}
				
				sb1.append( entityName );
			}
						
			sb.append( row );
			sb.append("\n");
		}
		
		refReportTempl = refReportTempl.replace( "!@#$ReportDetails$#@!", sb.toString() );
		refReportTempl = refReportTempl.replace( "!@#$Total$#@!", String.valueOf( totalCost ));
		return refReportTempl;
	}
}
