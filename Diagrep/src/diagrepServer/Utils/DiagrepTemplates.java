package diagrepServer.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import diagrepServer.database.core.DatabaseCallParams;
import diagrepServer.database.core.DatabaseConnection;
import diagrepServer.database.core.DatabaseConnectionPool;
import diagrepServer.database.core.ModelObject;
import diagrepServer.database.core.DatabaseConnection.EnumDBResult;
import diagrepServer.database.model.Recommendation;


public class DiagrepTemplates {

	static DiagrepTemplates instance;
	
	public static void main( String[] args ) {
		
		DiagrepTemplates.getInstance().readAllTemplates(
				"/Users/tavant/springsource/vfabric-tc-server-developer-2.6.0.RELEASE/spring-insight-instance/wtpwebapps/Diagrep/WEB-INF/templates");
	}
	
	public enum TemplateType {
		kBill,
		kBillRow,
		kReport,
		kTestValueRow,
		kTestDescriptionRow,
		kCategoryHeaderRow,
		kCollectionHeaderRow,
		kPackageHeaderRow,
		kReportRecommendationRow,
		kReferenceReport,
		kReferenceReportRow,
		kMonthlyReport,
		kMonthlyReportRow,
		kDaywiseMonthlyReport,
		kDaywiseMonthlyReportRow,
		kDailyReport,
		kDailyReportRow,
		kTemplateTypeInvalid;
		
		public TemplateType fromValue( int value ) {
			TemplateType tt = kBill;
			switch( value ) {
			case 0:
				tt = kBill;
				break;
				
			case 1:
				tt = kBillRow;
				break;
				
			case 2:
				tt = kReport;
				break;
								
			case 3:
				tt = kTestValueRow;
				break;
				
			case 4:
				tt = kTestDescriptionRow;
				break;
				
			case 5:
				tt = kCategoryHeaderRow;
				break;
				
			case 6:
				tt = kCollectionHeaderRow;
				break;
				
			case 7:
				tt = kPackageHeaderRow;
				break;
				
			case 8:
				tt = kReportRecommendationRow;
				break;
				
			case 9:
				tt = kReferenceReport;
				break;
				
			case 10:
				tt = kReferenceReportRow;
				break;
				
			case 11:
				tt = kMonthlyReport;
				break;
				
			case 12:
				tt = kMonthlyReportRow;
				break;
				
			case 13:
				tt = kDaywiseMonthlyReport;
				break;
				
			case 14:
				tt = kDaywiseMonthlyReportRow;
				break;
				
			case 15:
				tt = kDailyReport;
				break;
				
			case 16:
				tt = kDailyReportRow;
				
			default:
				throw new IllegalArgumentException();
			}
			
			return tt;
		}
	};
		
	ArrayList<String> arrayTemplates;
	
	private DiagrepTemplates() {
		arrayTemplates	= new ArrayList<String>( );
		for( int i=0; i < TemplateType.kTemplateTypeInvalid.ordinal(); i++ ) {
			arrayTemplates.add( "" );
		}
	}
	
	public static synchronized DiagrepTemplates getInstance() {
		
		if( instance == null ) {
			instance = new DiagrepTemplates();
		}
		return instance;
	}
	
	public void readAllTemplates( String rootFolder ) {
		
		String templatesFolder = rootFolder + File.separator + "WEB-INF" + File.separator + "templates";
		
		String[] fileNames = {"BillTempl", "BillRowTempl", "ReportTempl", "ReportTestRowTempl", "ReportTestDescriptionRowTempl", 
				"ReportCategoryHeaderRowTempl", "ReportCollectionHeaderRowTempl", "ReportPackageHeaderRowTempl", "ReportRecommendationRowTempl",
						"ReferenceReportTempl", "ReferenceReportRowTempl", "MonthlyReportTempl", "MonthlyReportRowTempl",
						"DaywiseMonthlyReportTempl","DaywiseMonthlyReportRowTempl","DailyReportTempl", "DailyReportRowTempl"};
		
		for( int i=0; i < fileNames.length; i++ ) {
			try{
				FileReader fr = new FileReader( templatesFolder + File.separator + fileNames[i] );
				BufferedReader reader = new BufferedReader( fr );
				
				StringBuffer buffer = new StringBuffer();
				String line = null;
				while( (line = reader.readLine()) != null ) {
					buffer.append( line );
				}
				
				this.arrayTemplates.set( i, buffer.toString() );
				
			} catch( FileNotFoundException e) {
				e.printStackTrace();
			} catch( IOException e ) {
				e.printStackTrace();
			}
		}
	}
	
	public String getTemplateFor( TemplateType tt ) {
		return this.arrayTemplates.get( tt.ordinal() );
	}
	
	public void saveRecommendationTemplate( String name, String content ) {
		DatabaseConnection dc = DatabaseConnectionPool.getPool().getSavedDataDbConnection();
		Recommendation r = new Recommendation();
		r.name = name;
		r.data = content;
		DatabaseCallParams params = r.prepareForSave( false );
		
		dc.execute(params);
	}
	
	public EnumDBResult deleteRecommendationTemplate( String name ) {
		DatabaseConnection dc = DatabaseConnectionPool.getPool().getSavedDataDbConnection();
		Recommendation r = new Recommendation();
		r.name = name;
		DatabaseCallParams params = r.prepareForDelete();
		
		return dc.execute(params);
	}
	
	// Returns a JSON string containing all the recommendation templates from
	// the Db.
	public String getRecommendationTemplates() {
		DatabaseConnection dc = DatabaseConnectionPool.getPool().getSavedDataDbConnection();
		DatabaseCallParams params = new Recommendation().prepareForFetch();
		
		ArrayList<ModelObject> arr = dc.fetch(params);
		
		StringBuilder sb = new StringBuilder();
		sb.append("{\"recommendations\":[");
		if( arr.size() > 0 ) {
			for( int i=0; i < arr.size(); i++ ) {
				Recommendation r = (Recommendation)arr.get(i);
				if( i > 0 ) {
					sb.append( "," );
				}
				sb.append( r );
			}
		}
		sb.append("]}");
		
		return sb.toString();
	}
}
