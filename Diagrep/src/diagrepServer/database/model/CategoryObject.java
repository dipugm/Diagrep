package diagrepServer.database.model;

import java.util.ArrayList;

import diagrepServer.database.actions.CommonDefs.EntityType;
import diagrepServer.database.core.ModelObject;

public class CategoryObject extends ModelObject implements IEntityObject {
	
	public String name	= null;
	public Double cost	= null;
	
	// Non-Db column properties.
	public ArrayList<ModelObject> fk_tests;

	public CategoryObject() {
		fk_tests	= new ArrayList<ModelObject>();
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
		/*
		 * JSON will be a dictionary as follows:
		 * { "id":"", 
		 * 	"name":"",
		 * 	"cost":,
		 * 	"tests":[{},{},...]
		 * }	
		 */
		
		StringBuilder sb	= new StringBuilder();
		sb.append("{");
		sb.append("\"type\":");
		sb.append( EntityType.Category.ordinal() );
		sb.append(",\"id\":");
		sb.append( this.id );
		sb.append(",\"name\":\"");
		sb.append( this.name );
		sb.append("\",\"cost\":");
		sb.append( this.cost );
		sb.append(",\"subEntitiesWithOrder\":[");
		if( this.fk_tests != null && this.fk_tests.size() > 0 ) {
			for( int iTest=0; iTest < this.fk_tests.size(); iTest++ ) {
				if( iTest > 0 ) {
					sb.append( "," );
				}
				ModelObject to	= this.fk_tests.get( iTest );
				sb.append( to );	// toString returns JSON.
				
			}
		}
		sb.append("]}");
		
		return sb.toString();
	}

	@Override
	public ArrayList<ModelObject> getContainedTests() {
		return this.fk_tests;
	}

}
