package diagrepServer.database.actions.packages;

import java.util.ArrayList;

import diagrepServer.database.actions.BaseAction;
import diagrepServer.database.actions.CommonDefs.EntityType;
import diagrepServer.database.core.DatabaseCallParams;
import diagrepServer.database.core.DatabaseConnectionPool;
import diagrepServer.database.core.ModelObject;
import diagrepServer.database.model.PackageObject;

public class GetSinglePackageAction extends BaseAction {

	private int packageId;
	
	public GetSinglePackageAction( int packageId ) {
		this.packageId	= packageId;
	}
	
	public Object doAction() {
		PackageObject coTemp	= new PackageObject();
		coTemp.id	= Integer.valueOf( this.packageId );
		
		DatabaseCallParams params 	= coTemp.prepareForFetch();
		params.deriveConditions( true );
		
		ArrayList<ModelObject> arr = DatabaseConnectionPool.getPool().getMasterDbConnection().fetch(params);
		if( arr.size() > 0 ) {
			PackageObject po	= (PackageObject)arr.get(0);
			
			// Need to get the sub entities for this category.
			po.fk_subEntities	= super.getSubEntities( EntityType.Package, po.id ); 
			
			return po;
		}
		
		return null;
	}
}
