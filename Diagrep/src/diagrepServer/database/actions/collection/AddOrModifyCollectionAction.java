package diagrepServer.database.actions.collection;

import diagrepServer.Utils.DatabaseUtility;
import diagrepServer.database.actions.BaseAction;
import diagrepServer.database.actions.CommonDefs.EntityType;
import diagrepServer.database.core.DatabaseCallParams;
import diagrepServer.database.core.DatabaseConnection;
import diagrepServer.database.core.DatabaseConnectionPool;
import diagrepServer.database.core.DatabaseCallParams.ConditionEquals;
import diagrepServer.database.core.DatabaseConnection.EnumDBResult;
import diagrepServer.database.model.CollectionObject;
import diagrepServer.database.model.EntitiesRelationObject;

public class AddOrModifyCollectionAction extends BaseAction {

	private CollectionObject collectionObject;
	boolean isForModify;
	Integer collectionIdForModify;
	String[] subEntities;
	
	public AddOrModifyCollectionAction( String name, double cost, String subEntities, Integer id ) {
		
		this.collectionObject	= new CollectionObject();
		this.collectionObject.name	= name;
		this.collectionObject.cost 	= cost;
		collectionIdForModify = id;
		
		// sub entities will be of the form <type>:<id>
		this.subEntities = new String[0];
		if( subEntities.isEmpty() == false ) {
			this.subEntities 	= subEntities.split( "," );
		}
		
		if( id == null ) {
			this.collectionObject.id 	= DatabaseUtility.getNextCollectionId(); 
		} else {
			this.isForModify	= true;	// valid if this is a modify
		}
	}
	
	public Object doAction() {
		
		DatabaseConnection dc 	= DatabaseConnectionPool.getPool().getMasterDbConnection();
		DatabaseCallParams params = this.collectionObject.prepareForSave( this.isForModify );
		if( this.isForModify ) {
			params.addCondition( new ConditionEquals("id", this.collectionIdForModify ) );
		}
		
		if( EnumDBResult.DB_SUCCESS == dc.execute( params ) ) {
			
			Integer collId = isForModify ? collectionIdForModify : this.collectionObject.id;
			
			// clear the existing sub entity relations for this collection.
			super.clearEntitiesRelations( EntityType.Collection, collId );
			
			for( int iSubEntity=0; iSubEntity < subEntities.length; iSubEntity++ ) {
				String subEntity = subEntities[ iSubEntity ];
				String[] fields = subEntity.split( "-" );
				
				EntitiesRelationObject ero 	= new EntitiesRelationObject( EntityType.Collection, collId );
				ero.subEntityType	= Integer.parseInt( fields[0] );
				ero.subEntityId		= Integer.parseInt( fields[1] );
				
				DatabaseCallParams params1 = ero.prepareForSave( false );
				if( EnumDBResult.DB_SUCCESS != dc.execute( params1 ) ) {
					System.out.println( "Failed to insert into ElementRelations table for Collection, Failed value -> " + subEntity );
				}
			}
			
			if( !this.isForModify ) {
				DatabaseUtility.saveNextCollectionId( this.collectionObject.id );
				return this.collectionObject.id;
			} else {
				return true;
			}
		}
		
		return null;
	}
}
