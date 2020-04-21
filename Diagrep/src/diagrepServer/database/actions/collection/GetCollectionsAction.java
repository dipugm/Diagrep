package diagrepServer.database.actions.collection;

import java.util.ArrayList;

import diagrepServer.database.actions.BaseAction;
import diagrepServer.database.actions.CommonDefs.EntityType;
import diagrepServer.database.core.DatabaseCallParams;
import diagrepServer.database.core.DatabaseConnection;
import diagrepServer.database.core.DatabaseConnectionPool;
import diagrepServer.database.model.CollectionObject;

public class GetCollectionsAction extends BaseAction {

	public Object doAction() {
			
		DatabaseConnection dc 	= DatabaseConnectionPool.getPool().getMasterDbConnection();
		
		CollectionObject coTemp = new CollectionObject();
		DatabaseCallParams params = coTemp.prepareForFetch();
		params.orderByClause = "name";
		
		ArrayList<?> collections 	= dc.fetch(params);
		if( collections.size() > 0 ) {
			// Go through each of the tests and get a Json equivalent of it.
			for( int iColl=0; iColl < collections.size(); iColl++ ) {
				CollectionObject co	= (CollectionObject)collections.get( iColl );

				// Need to get the categories for this collection along with order.
				co.fk_subEntities	= super.getSubEntities( EntityType.Collection, co.id );
			}
		}
	
		return collections;
	}
	
	public ArrayList<CollectionObject> getCollectionsFromIds( String collectionIds ) {
		String[] collections = collectionIds.split(",");

		ArrayList<CollectionObject> arrayColls 	= new ArrayList<CollectionObject>();
		for( int iCat=0; iCat < collections.length; iCat++ ) {
			CollectionObject co = (CollectionObject) new GetSingleCollectionAction( Integer.valueOf(collections[iCat]) ).doAction();
			if( co != null ) {
				arrayColls.add( co );
			}
		}
		
		return arrayColls;
	}
	
}
