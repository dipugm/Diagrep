package diagrepServer.database.actions.category;

import diagrepServer.Utils.DatabaseUtility;
import diagrepServer.database.actions.BaseAction;
import diagrepServer.database.actions.CommonDefs.EntityType;
import diagrepServer.database.core.DatabaseCallParams;
import diagrepServer.database.core.DatabaseCallParams.ConditionEquals;
import diagrepServer.database.core.DatabaseConnection;
import diagrepServer.database.core.DatabaseConnectionPool;
import diagrepServer.database.model.CategoryObject;
import diagrepServer.database.model.EntitiesRelationObject;

public class AddOrModifyCategoryAction extends BaseAction {

	CategoryObject category;
	boolean isForModify;
	Integer categoryIdForModify;
	String[] tests;
	
	public AddOrModifyCategoryAction( String name, String tests, double cost, Integer id ) {
		this.category	= new CategoryObject();
		category.name	= name;
		category.cost	= cost;
		
		this.tests		= new String[0];
		
		if( tests.isEmpty() == false ) {
			this.tests		= tests.split(",");
		}
		
		this.categoryIdForModify 	= id;

		if( id == null ) {
			category.id = DatabaseUtility.getNextCategoryId();
		} else {
			this.isForModify	= true;
		}
	}
	
	public Object doAction() {
		
		DatabaseConnection dc 	= DatabaseConnectionPool.getPool().getMasterDbConnection();
		DatabaseCallParams params = this.category.prepareForSave( this.isForModify );
		if( this.isForModify ) {
			params.addCondition(new ConditionEquals("id", this.categoryIdForModify) );
		} 
				
		if( dc.execute( params ) ) {
			
			Integer entityId = (category.id != null)? category.id : categoryIdForModify;
			
			// First clear the existing relations for this entity type and id.
			super.clearEntitiesRelations( EntityType.Category, entityId);
			
			// We need to make entries into EntitiesRelation table.
			for( int iTest=0; iTest < this.tests.length; iTest++ ) {
				EntitiesRelationObject ero = 
						new EntitiesRelationObject(EntityType.Category, entityId);
				ero.subEntityId	= Integer.parseInt( this.tests[ iTest ] );
				ero.subEntityType = EntityType.Test.ordinal();
				
				DatabaseCallParams params1 = ero.prepareForSave( false );
				
				dc.execute( params1 );
			}
			
			if( !this.isForModify ) {
				DatabaseUtility.saveNextCategoryId( this.category.id );
				return this.category.id;
			} else {
				return true;
			}
		}
		
		return null;
	}
}
