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

public class GetMonthlyReportAction extends BaseAction {

	long referenceMonth;
	long referenceYear;
	boolean shouldGetBills;
	
	public GetMonthlyReportAction( long month, long year ) {
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
		
        HashMap<String, Double> mapBillToCost = new HashMap<String, Double>();
        ArrayList<String> arrayDays = new ArrayList<String>();
        
        String currentBill = "";
        Double currentCost = 0.0;
        
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
				
				if( false == currentBill.isEmpty() && false == currentBill.equalsIgnoreCase( billDate ) ) {
					// Bill changed...should save the current one and move to the new one.
					mapBillToCost.put( currentBill, currentCost );
					currentCost = 0.0;
					
					arrayDays.add( currentBill );
				}

				currentCost += b1.getCost();
				currentBill = billDate;
				
			}
			
			if( currentCost > 0.0 ) {
				mapBillToCost.put( currentBill, currentCost );
				currentCost = 0.0;
				arrayDays.add( currentBill );
			}
		}

		// Prepare the Html
		return ReportAsHtml( arrayDays, mapBillToCost ); 
		
	}
	
	private String ReportAsHtml( ArrayList<String> arrayDays, HashMap<String, Double> mapBillToCost ) {

		String refReportTempl = DiagrepTemplates.getInstance().getTemplateFor( TemplateType.kMonthlyReport );
		String refReportRowTempl = DiagrepTemplates.getInstance().getTemplateFor( TemplateType.kMonthlyReportRow ); 
		
		SimpleDateFormat monthFormatter = new SimpleDateFormat( "MMM yyyy" );
		
		Calendar calendar = Calendar.getInstance();
        calendar.set((int)this.referenceYear, (int)this.referenceMonth, 1);
        
		refReportTempl = refReportTempl.replace("!@#$Month$#@!", monthFormatter.format( calendar.getTime() ) );
		
		StringBuilder sb = new StringBuilder();
		Double grandTotal = 0.0;
		for( int i=0; i < arrayDays.size(); i++ ) {
			
			String key = (String)arrayDays.get(i);
			
			String row = new String(refReportRowTempl);
			row = row.replace("%SlNo%", String.valueOf( i+1 ));
			row = row.replace("%Date%", key );

			Double d = (Double)mapBillToCost.get(key);
			grandTotal += d;
			row = row.replace("%Cost%", String.valueOf( d ) );
			
			sb.append( row );
			sb.append( "\n" );
		}
		
		refReportTempl = refReportTempl.replace( "!@#$ReportDetails$#@!", sb.toString() );
		refReportTempl = refReportTempl.replace( "!@#$Total$#@!", String.valueOf( grandTotal ));
		
		return refReportTempl;
	}
}
