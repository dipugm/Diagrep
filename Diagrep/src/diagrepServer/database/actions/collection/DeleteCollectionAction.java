package diagrepServer.database.actions.collection;

import diagrepServer.database.actions.BaseAction;
import diagrepServer.database.actions.CommonDefs.EntityType;
import diagrepServer.database.core.DatabaseCallParams;
import diagrepServer.database.core.DatabaseConnection;
import diagrepServer.database.core.DatabaseConnectionPool;
import diagrepServer.database.core.DatabaseConnection.EnumDBResult;
import diagrepServer.database.model.CollectionObject ;

public class DeleteCollectionAction extends BaseAction {
	private Integer collectionId;
	public DeleteCollectionAction( Integer collectionId ) {
		this.collectionId		= collectionId;
	}
	
	public Object doAction() {
		CollectionObject co = new CollectionObject();
		co.id 	= collectionId;
		
		DatabaseConnection dc 	= DatabaseConnectionPool.getPool().getMasterDbConnection();
		DatabaseCallParams params = co.prepareForDelete();
		
		if( EnumDBResult.DB_SUCCESS == dc.execute( params ) ) {
			
			// Clear the entities relation table for this collection.
			super.clearEntitiesRelations( EntityType.Collection, collectionId );
			
			// Clear entities relation table to remove all entries where the sub category is
			// this id.
			super.clearEntitiesRelationsForSubEntity( EntityType.Collection, collectionId );
			
			return true;
		}
		
		return false;
	}
	
}
