package diagrepServer.database.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import diagrepServer.Utils.DiagrepConfig;
import diagrepServer.Utils.DiagrepTemplates;
import diagrepServer.Utils.DiagrepTemplates.TemplateType;
import diagrepServer.Utils.StringUtilities;
import diagrepServer.database.actions.customer.GetCustomerDetailsAction;
import diagrepServer.database.core.ModelObject;

public class BillObject extends ModelObject implements IEntityObject {

	public String billNumber;
	public String customerId;
	public String referredBy;
	public Long	reportDate;
	public Long	billDate;
	public Double advancePaid;
	
	public ArrayList<BillDetailsObject> fk_arrayBillDetails;
	
	public BillObject( ) {
	}
	
	public Object doAnyTypeConversions( String columnName, Object value ) {
		if( columnName.equalsIgnoreCase("billDate") && (value.getClass().equals( Integer.class)) ) {
			return ((Integer)value).longValue();
		}
		if( columnName.equalsIgnoreCase("reportDate") && (value.getClass().equals( Integer.class)) ) {
			return ((Integer)value).longValue();
		}
		if( columnName.equalsIgnoreCase("advancePaid") && (value.getClass().equals( Float.class)) ) {
			return ((Float)value).doubleValue();
		}
		return value;
	}
	
	@Override
	public double getCost() {
		double totalCost = 0.0;
		
		for( int i=0; i < this.fk_arrayBillDetails.size(); i++ ) {
			BillDetailsObject bdo = this.fk_arrayBillDetails.get( i );
			totalCost += bdo.getCost();
		}
		
		return totalCost;
	}
	
	@Override
	public String getAsJson() {
		
		StringBuilder sb = new StringBuilder();
		sb.append( "{" );
		sb.append( "\"billNumber\":\"");
		sb.append( billNumber );
		sb.append( "\",\"customerId\":\"");
		sb.append( customerId );
		sb.append( "\",\"billDate\":");
		sb.append( String.valueOf( billDate ) );
		sb.append( ",\"reportDate\":");
		sb.append( String.valueOf( reportDate ) );
		sb.append( ",\"referredBy\":\"");
		sb.append( referredBy );
		sb.append( "\"}" );
		
		return sb.toString();
	}

	@Override
	public String getAsHtml() {
		
		if( this.referredBy == null ) { this.referredBy = ""; }
		/*
		 *  We use template files to create the final Html.
		 *  Templates used :
		 *  1. BillTempl
		 *  
		 *  Strings we look for :
		 *  1. !@#$Name$#@!,
		 *  2. !@#$BillNumber$#@!
		 *  3. !@#$Age$#@!
		 *  4. !@#$Sex$#@!
		 *  5. !@#$BillDate$#@!
		 *  6. !@#$ReferedBy$#@!
		 *  7. !@#$ReportDate$#@!
		 *  8. !@#$BillDetails$#@!
		 *  9. !@#$Total$#@!
		 *  10. !@#$Advance$#@!
		 *  11. !@#$Balance$#@!
		 *  12. !@#$CustomerID$#@!
		 */
		
		CustomerObject cust	= (CustomerObject)new GetCustomerDetailsAction(customerId).doAction();
		
		String dateFormat = DiagrepConfig.getConfig().get( DiagrepConfig.BILL_DATE_FORMAT ) ;
		SimpleDateFormat formatter = new SimpleDateFormat( dateFormat );
		
		String templ = DiagrepTemplates.getInstance().getTemplateFor( TemplateType.kBill );
		templ = templ.replace( "%BillNumber%", this.billNumber );
		templ = templ.replace( "!@#$Name$#@!", cust.name );
		templ = templ.replace( "!@#$BillNumber$#@!", billNumber );
		templ = templ.replace( "!@#$Age$#@!", StringUtilities.formatAge(cust.fk_currentAge ) );
		templ = templ.replace( "!@#$Sex$#@!", cust.getGenderAsString( cust.fk_sex ) );
		templ = templ.replace( "!@#$BillDate$#@!", formatter.format(new Date(billDate)) );
		templ = templ.replace( "!@#$ReportDate$#@!", reportDate > 0 ? formatter.format(new Date(reportDate)) : "" );
		templ = templ.replace( "!@#$ReferedBy$#@!", referredBy );
		templ = templ.replace( "!@#$Advance$#@!", String.valueOf(advancePaid) );
		templ = templ.replace( "!@#$CustomerID$#@!", cust.customerId );
		
		
		double totalCost = 0.0;
		StringBuffer buf = new StringBuffer();
		
		for( int i=0; i < this.fk_arrayBillDetails.size(); i++ ) {
			BillDetailsObject bdo = this.fk_arrayBillDetails.get( i );
			bdo.fk_index	= i+1;
			buf.append( bdo.getAsHtml() );
			buf.append("\n");
			
			totalCost += bdo.getCost();
		}
		
		templ = templ.replace( "!@#$BillDetails$#@!", buf.toString() );
		templ = templ.replace( "!@#$Total$#@!", String.valueOf(totalCost) );
		templ = templ.replace( "!@#$Balance$#@!", String.valueOf(totalCost) );
		
		return templ;
	}

	
}
