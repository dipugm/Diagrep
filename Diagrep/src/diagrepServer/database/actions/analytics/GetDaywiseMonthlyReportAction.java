package diagrepServer.database.actions.analytics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import diagrepServer.CommonDefs;
import diagrepServer.Utils.DatabaseUtility;
import diagrepServer.Utils.DiagrepConfig;
import diagrepServer.Utils.DiagrepTemplates;
import diagrepServer.Utils.DiagrepTemplates.TemplateType;
import diagrepServer.database.actions.BaseAction;
import diagrepServer.database.actions.bill.GetBillContentsAction;
import diagrepServer.database.core.DatabaseCallParams;
import diagrepServer.database.core.DatabaseConnection;
import diagrepServer.database.core.DatabaseConnectionPool;
import diagrepServer.database.model.BillObject;

public class GetDaywiseMonthlyReportAction extends BaseAction {

	long referenceMonth;
	long referenceYear;
	
	public GetDaywiseMonthlyReportAction( long month, long year ) {
		this.referenceMonth = month;
		this.referenceYear 	= year;
	}
	
	public Object doAction() {
		ArrayList<String> dbFilenames = DatabaseUtility.getDbFileNamesForType( CommonDefs.BILLREPORT_TYPE );
		
		String dateFormat = DiagrepConfig.getConfig().get( DiagrepConfig.BILL_DATE_FORMAT ) ;
		SimpleDateFormat formatter = new SimpleDateFormat( dateFormat );
		
		// Use the Calendar class to subtract one day
        Calendar calendar = Calendar.getInstance();
        calendar.set((int)this.referenceYear, (int)this.referenceMonth, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        Date dt1 = calendar.getTime();
        
        // Next month for the upper limit
        calendar.add( Calendar.MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        Date dt2 = calendar.getTime();
		
        ArrayList<String> arrayDays = new ArrayList<String>();
        HashMap<String, ArrayList<BillObject>> mapDayToBills = new HashMap<String, ArrayList<BillObject>>();
                
		for( int iFile=0; iFile < dbFilenames.size(); iFile++ ) {
			DatabaseConnection dc = DatabaseConnectionPool.getPool().getConnectionForDbFile( dbFilenames.get(iFile));
			
			BillObject bo = new BillObject();
			
			DatabaseCallParams param = bo.prepareForFetch();
			param.orderByClause = "billDate";
			param.addCondition( new DatabaseCallParams.ConditionGreaterThanOrEqual("billDate", dt1.getTime()) );
			param.addCondition( new DatabaseCallParams.ConditionLessThan("billDate", dt2.getTime()) );
			
			ArrayList<?> bills = dc.fetch(param);
			
			for( int iBill=0; iBill < bills.size(); iBill++ ) {
				BillObject b1 = (BillObject)bills.get( iBill );
				b1 = (BillObject)new GetBillContentsAction( b1.billNumber ).doAction();
				
				String billDate = formatter.format(b1.billDate);
				
				if( false == mapDayToBills.containsKey(billDate) ){
					mapDayToBills.put(billDate, new ArrayList<BillObject>());
					arrayDays.add( billDate );
				}

				mapDayToBills.get(billDate).add(b1);
			}
			
		}

		// Prepare the Html
		return ReportAsHtml( arrayDays, mapDayToBills ); 
		
	}
	
	private String ReportAsHtml( ArrayList<String> arrayDays, HashMap<String, ArrayList<BillObject>> mapDayToBills ) {

		String refReportTempl = DiagrepTemplates.getInstance().getTemplateFor( TemplateType.kDaywiseMonthlyReport );
		String refReportRowTempl = DiagrepTemplates.getInstance().getTemplateFor( TemplateType.kDaywiseMonthlyReportRow ); 
		
		SimpleDateFormat monthFormatter = new SimpleDateFormat( "MMM yyyy" );
		
		Calendar calendar = Calendar.getInstance();
        calendar.set((int)this.referenceYear, (int)this.referenceMonth, 1);
        
		refReportTempl = refReportTempl.replace("!@#$Month$#@!", monthFormatter.format( calendar.getTime() ) );
		
		StringBuilder sb = new StringBuilder();
		Double grandTotal = 0.0;
		Integer slNo = 0;
		for( int i=0; i < arrayDays.size(); i++ ) {
			
			String key = (String)arrayDays.get(i);
			ArrayList<BillObject> bills = mapDayToBills.get(key);
			
			for( int j=0; j < bills.size(); j++ ) {
				BillObject bo = bills.get(j);
			
				String row = new String(refReportRowTempl);
				row = row.replace("%SlNo%", String.valueOf( ++slNo ));
				row = row.replace("%Date%", key );
				row = row.replace("%BillNumber%", bo.billNumber);
				
				StringBuilder sbDetails = new StringBuilder();
				for( int bds=0; bds < bo.fk_arrayBillDetails.size(); bds++ ) {
					sbDetails.append( bo.fk_arrayBillDetails.get(bds).getEntityName() );
					sbDetails.append(",");
				}
				row = row.replace("%BillDetails%", sbDetails.toString());
				row = row.replace("%Cost%", String.valueOf(bo.getCost()));
	
				grandTotal += bo.getCost();
				
				sb.append( row );
				sb.append( "\n" );
			}
		}
		
		refReportTempl = refReportTempl.replace( "!@#$ReportDetails$#@!", sb.toString() );
		refReportTempl = refReportTempl.replace( "!@#$Total$#@!", String.valueOf( grandTotal ));
		
		return refReportTempl;
	}
}
