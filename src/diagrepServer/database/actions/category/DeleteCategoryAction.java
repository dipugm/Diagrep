package diagrepServer.database.actions.category;

import diagrepServer.database.actions.BaseAction;
import diagrepServer.database.actions.CommonDefs.EntityType;
import diagrepServer.database.core.DatabaseCallParams;
import diagrepServer.database.core.DatabaseConnection;
import diagrepServer.database.core.DatabaseConnectionPool;
import diagrepServer.database.core.DatabaseConnection.EnumDBResult;
import diagrepServer.database.model.CategoryObject;

public class DeleteCategoryAction extends BaseAction {

	private Integer categoryId;
	public DeleteCategoryAction( Integer categoryId ) {
		this.categoryId		= categoryId;
	}
	
	public Object doAction() {
		CategoryObject co = new CategoryObject();
		co.id 	= categoryId;
		
		DatabaseConnection dc 	= DatabaseConnectionPool.getPool().getMasterDbConnection();
		DatabaseCallParams params = co.prepareForDelete();
		
		if( EnumDBResult.DB_SUCCESS == dc.execute( params ) ) {
			
			// Clear entities relation table to remove all entries where the sub category is
			// this id.
			super.clearEntitiesRelationsForSubEntity( EntityType.Category, categoryId );
			
			return true;
		}
		
		return false;
	}
}
