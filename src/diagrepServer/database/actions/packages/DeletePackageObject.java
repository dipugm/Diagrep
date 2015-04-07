package diagrepServer.database.actions.packages;

import diagrepServer.database.actions.BaseAction;
import diagrepServer.database.actions.CommonDefs.EntityType;
import diagrepServer.database.core.DatabaseCallParams;
import diagrepServer.database.core.DatabaseConnection;
import diagrepServer.database.core.DatabaseConnectionPool;
import diagrepServer.database.model.PackageObject;

public class DeletePackageObject extends BaseAction {
	private Integer packageId;
	public DeletePackageObject( Integer packageId ) {
		this.packageId		= packageId;
	}
	
	public Object doAction() {
		PackageObject po = new PackageObject();
		po.id 	= this.packageId;
		
		DatabaseConnection dc 	= DatabaseConnectionPool.getPool().getMasterDbConnection();
		DatabaseCallParams params = po.prepareForDelete();
		
		if( dc.execute( params ) ) {
			
			// Clear the entities relation table for this collection.
			super.clearEntitiesRelations( EntityType.Package, packageId );
			
			return true;
		}
		
		return false;
	}
}
