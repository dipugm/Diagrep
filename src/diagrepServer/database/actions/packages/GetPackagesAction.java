package diagrepServer.database.actions.packages;

import java.util.ArrayList;

import diagrepServer.database.actions.BaseAction;
import diagrepServer.database.actions.CommonDefs.EntityType;
import diagrepServer.database.core.DatabaseCallParams;
import diagrepServer.database.core.DatabaseConnection;
import diagrepServer.database.core.DatabaseConnectionPool;
import diagrepServer.database.model.PackageObject;

public class GetPackagesAction extends BaseAction {

	public Object doAction() {
		DatabaseConnection dc 	= DatabaseConnectionPool.getPool().getMasterDbConnection();
		
		PackageObject poTemp = new PackageObject();
		DatabaseCallParams params = poTemp.prepareForFetch();
		params.orderByClause = "name";
		
		ArrayList<?> packages 	= dc.fetch(params);
		if( packages.size() > 0 ) {
			// Go through each of the tests and get a Json equivalent of it.
			for( int iColl=0; iColl < packages.size(); iColl++ ) {
				PackageObject po	= (PackageObject)packages.get( iColl );
				
				// Need to get the categories for this collection along with order.
				po.fk_subEntities	= super.getSubEntities( EntityType.Package, po.id );	
			}
		}
	
		return packages;
	}
}
