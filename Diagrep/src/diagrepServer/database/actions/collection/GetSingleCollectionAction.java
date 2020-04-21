package diagrepServer.database.actions.collection;

import java.util.ArrayList;

import diagrepServer.database.actions.BaseAction;
import diagrepServer.database.actions.CommonDefs.EntityType;
import diagrepServer.database.core.DatabaseCallParams;
import diagrepServer.database.core.DatabaseConnectionPool;
import diagrepServer.database.core.ModelObject;
import diagrepServer.database.model.CollectionObject;

public class GetSingleCollectionAction extends BaseAction {

	private int  			collectionId;
	
	public GetSingleCollectionAction( int collectionId ) {
		this.collectionId	= collectionId;
	}
	
	public Object doAction() {
		CollectionObject coTemp	= new CollectionObject();
		coTemp.id	= Integer.valueOf( this.collectionId );
		
		DatabaseCallParams params 	= coTemp.prepareForFetch();
		params.deriveConditions( true );
		
		ArrayList<ModelObject> arr = DatabaseConnectionPool.getPool().getMasterDbConnection().fetch(params);
		if( arr.size() > 0 ) {
			CollectionObject co	= (CollectionObject)arr.get(0);
			
			// Need to get the categories for this collection along with order.
			co.fk_subEntities	= super.getSubEntities( EntityType.Collection, co.id );
			
			return co;
		}
		
		return null;
	}
}
