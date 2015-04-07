package diagrepServer.database.model;

import java.util.ArrayList;

import diagrepServer.database.actions.CommonDefs.EntityType;
import diagrepServer.database.core.ModelObject;

public class PackageObject extends ModelObject implements IEntityObject {

	public String name;
	public Integer id;
	public Double cost;
	public String description;
	
	public ArrayList<ModelObject> fk_subEntities;
	
	@Override
	public double getCost() {
		// TODO Auto-generated method stub
		return 0;
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
		 * 	"collections":[{},{},...],
		 * 	"categories":[{},{},...],
		 * 	"tests":[{},{},...]
		 * }	
		 */
		
		StringBuilder sb	= new StringBuilder();
		sb.append("{");
		sb.append("\"id\":");
		sb.append( this.id );
		sb.append(",\"type\":");
		sb.append( EntityType.Package.ordinal() );
		sb.append(",\"name\":\"");
		sb.append( this.name );
		sb.append("\",\"cost\":");
		sb.append( this.cost );
		
		if( this.description != null ) {
			sb.append(",\"description\":\"");
			sb.append( this.description );
		}
		
		sb.append("\",\"subEntitiesWithOrder\":[");
		if( this.fk_subEntities!= null && this.fk_subEntities.size() > 0 ) {
			for( int iSubEntity=0; iSubEntity < this.fk_subEntities.size(); iSubEntity++ ) {
				if( iSubEntity > 0 ) {
					sb.append( "," );
				}
				sb.append( this.fk_subEntities.get( iSubEntity ) );	// toString returns JSON.
			}
		}
		sb.append("]}");
		
		return sb.toString();
	}

	@Override
	public ArrayList<ModelObject> getContainedTests() {
		ArrayList<ModelObject> arr 	= new ArrayList<ModelObject>();
		
		for( int i=0; i < fk_subEntities.size(); i++ ) {
			ModelObject mo = fk_subEntities.get( i );
			if( mo.getClass() == TestObject.class ) {
				arr.add( mo );
			} else if( mo.getClass() == CategoryObject.class ) {
				CategoryObject co = (CategoryObject)mo;
				for( int j=0; j < co.fk_tests.size(); j++ ) {
					arr.add( co.fk_tests.get( j ) );
				}
			} else {
				CollectionObject co = (CollectionObject)mo;
				for( int j=0; j < co.fk_subEntities.size(); j++ ) {
					arr.addAll( co.fk_subEntities.get( j ).getContainedTests() );
				}
			}
		}
		
		return arr;
	}

}
