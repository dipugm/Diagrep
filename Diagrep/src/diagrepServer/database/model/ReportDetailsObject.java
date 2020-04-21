package diagrepServer.database.model;

import diagrepServer.database.core.ModelObject;

public class ReportDetailsObject extends ModelObject implements IEntityObject {

	public String billNumber;
	public String description;
	public Integer entityId;
	public String entityValue;
	public Integer isHighlighted;
	
	@Override
	public double getCost() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public Object doAnyTypeConversions( String columnName, Object value ) {
		
		if( value != null && 
			((columnName.equalsIgnoreCase("description") || columnName.equalsIgnoreCase("entityValue"))) ) {
			String ret = new String( javax.xml.bind.DatatypeConverter.parseBase64Binary((String)value) );
			return ret;
		}
		return value;
	}
	
	@Override
	public String getAsHtml() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
