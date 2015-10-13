package diagrepServer.database.model;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import diagrepServer.Utils.DiagrepConfig;
import diagrepServer.Utils.DiagrepTemplates;
import diagrepServer.Utils.DiagrepTemplates.TemplateType;
import diagrepServer.database.actions.bill.GetBillContentsAction;
import diagrepServer.database.actions.category.GetSingleCategoryAction;
import diagrepServer.database.actions.collection.GetSingleCollectionAction;
import diagrepServer.database.actions.customer.GetCustomerDetailsAction;
import diagrepServer.database.actions.packages.GetSinglePackageAction;
import diagrepServer.database.actions.test.GetSingleTestAction;
import diagrepServer.database.core.ModelObject;

public class ReportObject extends ModelObject implements IEntityObject {

	public String billNumber;
	public String recommendations;
	public String preConditions;
	
	public ArrayList<ReportDetailsObject> fk_reportDetails;
	
	// A convenience to lookup the report details using test id.
	public HashMap<Integer, ReportDetailsObject> fk_reportDetailsAsHash;
	
	@Override
	public double getCost() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public Object doAnyTypeConversions( String columnName, Object value ) {
		if( columnName.equalsIgnoreCase("recommendations") && value != null ) {
			
			String ret = new String(javax.xml.bind.DatatypeConverter.parseBase64Binary( (String)value ));
			return ret;
		}
		return value;
	}
	
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		/*
		 *  We need to send the following.
		 *  1. All report details objects.
		 *  2. report recommendations string.
		 */
		
		sb.append( "{" );
		
		sb.append( "\"recommendations\":\"");
		if( recommendations != null ) {
			try {
				recommendations = URLEncoder.encode( recommendations, "UTF-8" );
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sb.append( recommendations );
		}
		sb.append( "\",\"entities\":[" );
		
		boolean appendComma = false;
		for( int i=0; i < fk_reportDetails.size(); i++ ) {
			ReportDetailsObject rdo = fk_reportDetails.get( i );
			TestObject to = (TestObject)new GetSingleTestAction( rdo.entityId ).doAction();
			
			if( to == null ) {
				System.out.println( "Test does not exist for id -> " + rdo.entityId );
				continue;
			}
			
			if( appendComma ) {
				sb.append( "," );
			}
			appendComma = true;
			
			sb.append( "{" );
			
			sb.append( "\"id\":" );
			sb.append( to.id );
			sb.append( ",\"name\":\"" );
			sb.append( to.name );
			sb.append( "\",\"normalValue\":\"" );
			sb.append( to.normalValue );
			sb.append( "\",\"units\":\"" );
			sb.append( to.unit );
			sb.append( "\",\"testedValue\":\"" );
			
			if( rdo.entityValue != null && !rdo.entityValue.equalsIgnoreCase("null")) {
				try {
					rdo.entityValue = URLEncoder.encode( rdo.entityValue, "UTF-8" );
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				sb.append( rdo.entityValue );
			}
			
			sb.append( "\",\"description\":\"" );
			if( rdo.description != null ) {
				try {
					rdo.description = URLEncoder.encode( rdo.description, "UTF-8" );
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				sb.append( rdo.description );
			}
			
			sb.append( "\",\"isHighlighted\":" );
			if( rdo.isHighlighted != null ) {
				sb.append( String.valueOf( rdo.isHighlighted ) );
			} else {
				sb.append( "0" );
			}
			sb.append( "}" );
		}

		sb.append( "]");
		sb.append( "}" );
		
		return sb.toString();
	}
	
	@Override
	public String getAsHtml() {
		
		// Creating a hash map of the test id to the details object.
		this.fk_reportDetailsAsHash = new HashMap<Integer, ReportDetailsObject>();
		for( int iTest=0; iTest < fk_reportDetails.size(); iTest++ ) {
			ReportDetailsObject rdo = fk_reportDetails.get( iTest );
			this.fk_reportDetailsAsHash.put( rdo.entityId, rdo );
		}
		
		BillObject bo = (BillObject)new GetBillContentsAction( billNumber ).doAction();
		CustomerObject co = (CustomerObject)new GetCustomerDetailsAction( bo.customerId ).doAction();
		
		String reportString = DiagrepTemplates.getInstance().getTemplateFor( TemplateType.kReport );
		
		reportString	= reportString.replace("%BillNumber%", billNumber );
		reportString	= reportString.replace("!@#$Name$#@!", co.name );
		reportString	= reportString.replace("!@#$CustomerID$#@!", co.customerId );
		reportString	= reportString.replace("!@#$Age$#@!", String.valueOf( co.fk_currentAge / 12 ) + "Y" );
		reportString	= reportString.replace("!@#$Sex$#@!", co.getGenderAsString( co.fk_sex ) );
		reportString	= reportString.replace("!@#$ReferedBy$#@!", bo.referredBy );
		reportString	= reportString.replace("!@#$BillNumber$#@!", billNumber );
		
		String dateFormat = DiagrepConfig.getConfig().get( DiagrepConfig.DATE_FORMAT ) ;
		SimpleDateFormat formatter = new SimpleDateFormat( dateFormat );
		reportString	= reportString.replace("!@#$ReportDate$#@!", formatter.format( new Date(bo.reportDate) ) );
		
		if( recommendations != null && ! recommendations.isEmpty() ) {
			// Some reports might just have a line break in them. So eliminating that case also.
			if( recommendations.trim().equalsIgnoreCase("<br>") ) {
				recommendations = "";
			}

			String recoTempl = DiagrepTemplates.getInstance().getTemplateFor( TemplateType.kReportRecommendationRow );
			recoTempl	= recoTempl.replace( "%Recommendation%", recommendations );
			reportString	= reportString.replace("!@#$Recommendation$#@!", recoTempl );
			
		} else {
			reportString	= reportString.replace("!@#$Recommendation$#@!", "" );
		}
			
		StringBuilder sb = new StringBuilder();
		for( int iEnt=0; iEnt < bo.fk_arrayBillDetails.size(); iEnt++ ) {
			BillDetailsObject bdo = bo.fk_arrayBillDetails.get( iEnt );
			
			switch( bdo.entityType ) {
				case 0: // Test
					TestObject to = (TestObject)new GetSingleTestAction( bdo.entityId ).doAction();
					if( to != null ) {
						sb.append( getTestForReportAsHtml(0, to) );
					}
					break;
					
				case 1:  // Category
					CategoryObject catO = (CategoryObject)new GetSingleCategoryAction( bdo.entityId ).doAction();
					if( catO != null ) {
						sb.append( getCategoryForReportAsHtml(0, catO) );
					}
					break;
					
				case 2: { // Collection
					CollectionObject collO = (CollectionObject)new GetSingleCollectionAction( bdo.entityId ).doAction();
					if( collO != null ) {
						sb.append( getCollectionForReportAsHtml(0, collO) );
					}
				} break;
					
				case 3:	{ // Package
					PackageObject po = (PackageObject)new GetSinglePackageAction( bdo.entityId ).doAction();
					if( po != null ) {
						sb.append( getPackageForReportAsHtml(0, po) );
					}
				} break;
			}
		}
		
		reportString = reportString.replace( "!@#$Report$#@!", sb.toString() );
		return reportString;
	}
	
	private String getTestForReportAsHtml(int level, TestObject to ) {
		String rowTemplate = DiagrepTemplates.getInstance().getTemplateFor( TemplateType.kTestValueRow);
		
		if( to == null ) {
			
			rowTemplate = rowTemplate.replace( "%TestName%", " -- Test does not exist -- " );
			rowTemplate = rowTemplate.replace( "%NormalValue%", "");
			rowTemplate = rowTemplate.replace( "%Units%", "" );
			rowTemplate = rowTemplate.replace( "%TestedValue%", "");

			return rowTemplate;
		}
		
		String rowDescTemplate = DiagrepTemplates.getInstance().getTemplateFor( TemplateType.kTestDescriptionRow);
		
		StringBuilder sb = new StringBuilder();
		
		ReportDetailsObject rdo = fk_reportDetailsAsHash.get( to.id );
		
		String testName = to.name;
		if( to.method != null && ! to.method.isEmpty() && ! to.method.equalsIgnoreCase("null") ) {
			testName += " [" + to.method + "]";
		}
				
		rowTemplate = rowTemplate.replace( "%Level%", String.valueOf( level ) );
		
		rowTemplate = rowTemplate.replace( "%TestName%", testName );
		rowTemplate = rowTemplate.replace( "%NormalValue%", to.normalValue);
		rowTemplate = rowTemplate.replace( "%Units%", to.unit );
		
		String testValue = ((rdo != null) && (rdo.entityValue != null)) ? rdo.entityValue : "";
		try {
			testValue = URLDecoder.decode( testValue, "UTF-8" );
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if( rdo.isHighlighted != null && rdo.isHighlighted == 1 ) {
			testValue = "<b><u>" + testValue + "</u></b>";
		}
		
		rowTemplate = rowTemplate.replace( "%TestedValue%", testValue);
		
		sb.append( rowTemplate );
		
		// If the test contained some description, we need to show it in a subsequent
		// row.
		if( rdo.description != null ) {
			rowDescTemplate 	= rowDescTemplate.replace( "%Level%", String.valueOf( level ) );
			String rdesctempl 	= rowDescTemplate.replace( "%Description%", rdo.description );
			
			sb.append( rdesctempl );
		}
		
		return sb.toString();
	}
	
	private String getCategoryForReportAsHtml( int level, CategoryObject co ) {
		
		String headingTemplate 	= DiagrepTemplates.getInstance().getTemplateFor( TemplateType.kCategoryHeaderRow);
		
		headingTemplate = headingTemplate.replace( "%level%", String.valueOf( level ) );
		if( co == null ) {
			headingTemplate = headingTemplate.replace( "%CategoryName%", "-- Category does not exist --" );
			return headingTemplate;
		}
		
		StringBuilder sb = new StringBuilder();
		
		headingTemplate = headingTemplate.replace( "%CategoryName%", co.name );
		
		sb.append( headingTemplate );
		
		for( int iTest=0; iTest < co.fk_tests.size(); iTest++ ) {
			TestObject to = (TestObject)co.fk_tests.get( iTest );
			sb.append( getTestForReportAsHtml(level + 1, to) );
		}
		
		return sb.toString();
	}
	
	private String getCollectionForReportAsHtml( int level, CollectionObject co ) {
		
		String headingTemplate 	= DiagrepTemplates.getInstance().getTemplateFor( TemplateType.kCollectionHeaderRow);
		
		headingTemplate = headingTemplate.replace( "%level%", String.valueOf( level ) );
		
		if( co == null ) {
			headingTemplate = headingTemplate.replace( "%CollectionName%", "-- Colelction does not exist --" );
			return headingTemplate;
		}
		
		StringBuilder sb = new StringBuilder();
		
		headingTemplate = headingTemplate.replace( "%CollectionName%", co.name );
		
		// append the collection name row.
		sb.append( headingTemplate );
		
		for( int iEnt=0; iEnt < co.fk_subEntities.size(); iEnt++ ) {
			ModelObject mo = (ModelObject)co.fk_subEntities.get( iEnt );
			if( mo.getClass() == TestObject.class ) {
				sb.append( getTestForReportAsHtml(level + 1, (TestObject)mo ) );
			} else {	// Category
				sb.append( getCategoryForReportAsHtml(level + 1, (CategoryObject)mo ) );
			}
		}
		
		return sb.toString();
	}
	
	private String getPackageForReportAsHtml( int level, PackageObject po ) {
		
		String headingTemplate 	= DiagrepTemplates.getInstance().getTemplateFor( TemplateType.kPackageHeaderRow);
		
		if( po == null ) {
			headingTemplate = headingTemplate.replace( "%PackageName%", "-- Package does not exist --" );
			return headingTemplate;
		}
		
		StringBuilder sb = new StringBuilder();
		
		headingTemplate = headingTemplate.replace( "%PackageName%", po.name );
		
		// append the collection name row.
		sb.append( headingTemplate );
		
		for( int iEnt=0; iEnt < po.fk_subEntities.size(); iEnt++ ) {
			ModelObject mo = (ModelObject)po.fk_subEntities.get( iEnt );
			if( mo.getClass() == TestObject.class ) {
				sb.append( getTestForReportAsHtml( level + 1, (TestObject)mo ) );
			} else if( mo.getClass() == CategoryObject.class ) {	
				sb.append( getCategoryForReportAsHtml( level + 1, (CategoryObject)mo ) );
			} else { // Collection
				sb.append( getCollectionForReportAsHtml( level + 1, (CollectionObject)mo ) );
			}
		}
		
		return sb.toString();
	}

	
}
