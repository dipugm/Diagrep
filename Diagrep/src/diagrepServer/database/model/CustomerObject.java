package diagrepServer.database.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import diagrepServer.Utils.DiagrepConfig;
import diagrepServer.database.core.ModelObject;

public class CustomerObject extends ModelObject {
	
	public enum EnumSex {
		Female,
		Male,
		Others;
		
		public static EnumSex fromValue( int val ) {
			if( val == 0 ) {
				return Female;
			} else if( val == 1) {
				return Male;
			}
			return Others;
		}
	};

	public String customerId;
	public String name;
	public String emailId;
	public String address;
	public Integer age;
	public Integer sex;
	public String mobileNumber;
	public String landlineNumber;
	public Long dateOfCreation;
	
	/* Non Db variables. */
	public Date fk_dateOfCreation;
	public EnumSex fk_sex;
	public Integer fk_currentAge;
	
	public String getGenderAsString( EnumSex s ) {
		String str = "NA";
		switch( s ) {
		case Female:
			str = "F";
			break;
			
		case Male:
			str = "M";
			break;
			
		case Others:
			str = "NA";
			break;
			
		default:
			break;
		}
		
		return str;
	}
	
	public Object doAnyTypeConversions( String columnName, Object value ) {
		if( columnName.equalsIgnoreCase( "dateofCreation") ) {
			
			String sz = String.valueOf(value);
			Long retValue = Long.parseLong( sz );
			if( sz.length() <= 10 ) {
				retValue *= 1000;
			}
			return retValue; 
		}
		return value;
	}
	
	public void fillFKs() {
		if( sex != null ) {
			fk_sex 	= EnumSex.fromValue(sex);
		}
		if( dateOfCreation != null ) {
			if( String.valueOf(dateOfCreation).length() <= 10 ) {
				dateOfCreation *= 1000;
			}
			fk_dateOfCreation = new Date(dateOfCreation);
		}
		if( age != null ) {
			if( fk_dateOfCreation != null ) {
				Date today = new Date();
				
				Calendar.getInstance().setTime( today );
				int m1 = Calendar.getInstance().get( Calendar.YEAR) * 12 + Calendar.getInstance().get( Calendar.MONTH);
				Calendar.getInstance().setTime( fk_dateOfCreation );
				int m2 = Calendar.getInstance().get( Calendar.YEAR) * 12 + Calendar.getInstance().get( Calendar.MONTH);
			    
				fk_currentAge = age + (m2 - m1) + 1;
			} else {
				fk_currentAge = age;
			}
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append( "{");
		sb.append("\"name\":\"");
		sb.append( name );
		sb.append("\",\"id\":\"");
		sb.append(customerId);
		sb.append("\",\"dateOfCreation\":\"");
		
		String dateFormat = DiagrepConfig.getConfig().get( DiagrepConfig.REPORT_DATE_FORMAT ) ;
		SimpleDateFormat formatter = new SimpleDateFormat( dateFormat );
		sb.append( formatter.format(fk_dateOfCreation) );
		
		sb.append("\",\"age\":");
		sb.append( String.valueOf( fk_currentAge ) );
		sb.append(",\"sex\":\"");
		sb.append( getGenderAsString(fk_sex));
		sb.append("\"");
		sb.append( "}");
		
		return sb.toString();
	}
	

}
