package diagrepServer.database.actions;

import java.util.ArrayList;

import diagrepServer.database.actions.CommonDefs.EntityType;
import diagrepServer.database.actions.category.GetSingleCategoryAction;
import diagrepServer.database.actions.collection.GetSingleCollectionAction;
import diagrepServer.database.actions.test.GetSingleTestAction;
import diagrepServer.database.core.DatabaseCallParams;
import diagrepServer.database.core.DatabaseConnection;
import diagrepServer.database.core.DatabaseConnectionPool;
import diagrepServer.database.core.ModelObject;
import diagrepServer.database.core.DatabaseConnection.EnumDBResult;
import diagrepServer.database.model.EntitiesRelationObject;

public abstract class BaseAction {

	public Object doAction() { return ""; }
	
	public ArrayList<ModelObject> getSubEntities( EntityType parentEntityType, Integer parentEntityId ) {
		ArrayList<ModelObject> subEntities = new ArrayList<ModelObject>();
		
		EntitiesRelationObject ero	= new EntitiesRelationObject( parentEntityType, parentEntityId );
		DatabaseCallParams params = ero.prepareForFetch();
		
		DatabaseConnection dc = DatabaseConnectionPool.getPool().getMasterDbConnection();
		
		ArrayList<ModelObject> entities = dc.fetch(params);
		for( int iEntity=0; iEntity < entities.size(); iEntity++ ) {
			EntitiesRelationObject eroFetched = (EntitiesRelationObject)entities.get( iEntity );
			
			EntityType et 	= EntityType.fromValue( String.valueOf( eroFetched.subEntityType ) );
			BaseAction subEntityGetAction = null;
			switch( et ) {
			case Test:
				subEntityGetAction 	= new GetSingleTestAction( eroFetched.subEntityId );
				break;
			case Category:
				subEntityGetAction 	= new GetSingleCategoryAction( eroFetched.subEntityId );
				break;
			case Collection:
				subEntityGetAction 	= new GetSingleCollectionAction( eroFetched.subEntityId );
				break;
			}
			
			ModelObject mo = (ModelObject)subEntityGetAction.doAction();
			
			if( mo != null ) {
				subEntities.add( mo );
			}
			
		}
		
		return subEntities;
	}
	
	public EnumDBResult clearEntitiesRelations( EntityType parentEntityType, Integer parentEntityId ) { 

		EntitiesRelationObject ero	= new EntitiesRelationObject( parentEntityType, parentEntityId );
		DatabaseCallParams params = ero.prepareForDelete();
		
		DatabaseConnection dc = DatabaseConnectionPool.getPool().getMasterDbConnection();
		return dc.execute( params );
	}
	
	public EnumDBResult clearEntitiesRelationsForSubEntity( EntityType subEntityType, Integer subEntityId ) { 

		EntitiesRelationObject ero	= new EntitiesRelationObject();
		ero.subEntityId		= subEntityId;
		ero.subEntityType	= subEntityType.ordinal();
		
		DatabaseCallParams params = ero.prepareForDelete();
		
		DatabaseConnection dc = DatabaseConnectionPool.getPool().getMasterDbConnection();
		return dc.execute( params );
	}
}
