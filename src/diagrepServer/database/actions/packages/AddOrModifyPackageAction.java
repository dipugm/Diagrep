package diagrepServer.database.actions.packages;

import diagrepServer.Utils.DatabaseUtility;
import diagrepServer.database.actions.BaseAction;
import diagrepServer.database.actions.CommonDefs.EntityType;
import diagrepServer.database.core.DatabaseCallParams;
import diagrepServer.database.core.DatabaseConnection;
import diagrepServer.database.core.DatabaseConnectionPool;
import diagrepServer.database.core.DatabaseCallParams.ConditionEquals;
import diagrepServer.database.model.EntitiesRelationObject;
import diagrepServer.database.model.PackageObject;

public class AddOrModifyPackageAction extends BaseAction {

	private PackageObject packageObject;
	boolean isForModify;
	Integer idOfPackageToModify;
	String[] subEntities;
	
	public AddOrModifyPackageAction( String name, String description, double cost, String subEntities, Integer id ) {
		this.packageObject	= new PackageObject();
		this.packageObject.name	= name;
		this.packageObject.cost = cost;
		this.packageObject.description = description;
		
		this.subEntities = new String[0];
		if( subEntities.isEmpty() == false ) {
			this.subEntities = subEntities.split( "," );
		}
		
		this.idOfPackageToModify = id;
		
		if( id == null ) {
			this.packageObject.id 	= DatabaseUtility.getNextPackageId(); 
		} else {
			this.isForModify	= true;	// valid if this is a modify
		}
	}
	
	public Object doAction() {
		
		DatabaseConnection dc 	= DatabaseConnectionPool.getPool().getMasterDbConnection();
		DatabaseCallParams params = this.packageObject.prepareForSave( this.isForModify );
		if( this.isForModify ) {
			params.addCondition( new ConditionEquals("id", this.idOfPackageToModify ) );
		}
		
		if( dc.execute( params ) ) {
			
			Integer pkgId = isForModify ? idOfPackageToModify : this.packageObject.id;
			
			// clear the existing sub entity relations for this collection.
			super.clearEntitiesRelations( EntityType.Package, pkgId );
			
			for( int iSubEntity=0; iSubEntity < subEntities.length; iSubEntity++ ) {
				String subEntity = subEntities[ iSubEntity ];
				String[] fields = subEntity.split( "-" );
				
				EntitiesRelationObject ero 	= new EntitiesRelationObject( EntityType.Package, pkgId );
				ero.subEntityType	= Integer.parseInt( fields[0] );
				ero.subEntityId		= Integer.parseInt( fields[1] );
				
				DatabaseCallParams params1 = ero.prepareForSave( false );
				if( false == dc.execute( params1 ) ) {
					System.out.println( "Failed to insert into ElementRelations table for Package, Failed value -> " + subEntity );
				}
			}
			
			if( !this.isForModify ) {
				DatabaseUtility.saveNextPackageId( this.packageObject.id );
				return this.packageObject.id;
			} else {
				return true;
			}
		}
		
		return null;
	}

}
