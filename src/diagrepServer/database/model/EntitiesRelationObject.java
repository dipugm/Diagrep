package diagrepServer.database.model;

import java.util.ArrayList;

import diagrepServer.database.actions.CommonDefs.EntityType;
import diagrepServer.database.core.ModelObject;

public class EntitiesRelationObject extends ModelObject implements IEntityObject {

	public Integer parentEntityType;
	public Integer parentEntityId;
	public Integer subEntityType;
	public Integer subEntityId;
	
	public EntitiesRelationObject() {}
	
	public EntitiesRelationObject( EntityType parentEntityType, Integer parentEntityId ) {
		this.parentEntityType = parentEntityType.ordinal();
		this.parentEntityId = parentEntityId;
	}
	
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<ModelObject> getContainedTests() {
		// TODO Auto-generated method stub
		return null;
	}

}
