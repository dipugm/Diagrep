package diagrepServer.database.model;

import diagrepServer.database.actions.CommonDefs.EntityType;
import diagrepServer.database.core.ModelObject;

public class TestObject extends ModelObject implements IEntityObject {

	public String name;
	public String method;
	public String unit;
	public Double cost;
	public String normalValue;
		
	public String toString() {
		return getAsJson();
	}
	
	@Override
	public double getCost() {
		// TODO Auto-generated method stub
		return cost;
	}
	
	@Override
	public String getAsHtml() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getAsJson() {
		// TODO Auto-generated method stub
		StringBuilder sb	= new StringBuilder();
		sb.append("{");
		sb.append("\"id\":");
		sb.append( String.valueOf(this.id) );
		sb.append(",\"type\":");
		sb.append( EntityType.Test.ordinal() );
		sb.append(",\"name\":\"");
		sb.append( this.name );
		
		sb.append("\",\"method\":\"");
		if( this.method != null && !this.method.equalsIgnoreCase("null") ) {
			sb.append( this.method );
		}
		
		sb.append("\",\"unit\":\"");
		if( this.unit != null ) {
			sb.append( this.unit);
		}
		
		sb.append("\",\"cost\":");
		sb.append( String.valueOf( this.cost ) );
		if( this.normalValue != null ) {
			sb.append(",\"normalValue\":\"");
			sb.append( this.normalValue );
		}
		sb.append("\"");
		sb.append("}");
		
		return sb.toString();
	}
	
}
