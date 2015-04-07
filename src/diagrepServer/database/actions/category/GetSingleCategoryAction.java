package diagrepServer.database.actions.category;

import java.util.ArrayList;

import diagrepServer.database.actions.BaseAction;
import diagrepServer.database.actions.CommonDefs.EntityType;
import diagrepServer.database.core.DatabaseCallParams;
import diagrepServer.database.core.DatabaseConnectionPool;
import diagrepServer.database.core.ModelObject;
import diagrepServer.database.model.CategoryObject;

public class GetSingleCategoryAction extends BaseAction {

	private int  			categoryId;
	
	public GetSingleCategoryAction( int categoryId ) {
		this.categoryId	= categoryId;
	}
	
	public Object doAction() {
		CategoryObject coTemp	= new CategoryObject();
		coTemp.id	= Integer.valueOf( this.categoryId );
		
		DatabaseCallParams params 	= coTemp.prepareForFetch();
		params.deriveConditions( true );
		
		ArrayList<ModelObject> arr = DatabaseConnectionPool.getPool().getMasterDbConnection().fetch(params);
		if( arr.size() > 0 ) {
			CategoryObject co	= (CategoryObject)arr.get(0);
			
			// Need to get the tests for this category.
			co.fk_tests	= super.getSubEntities( EntityType.Category, co.id ); 
			
			return co;
		}
		
		return null;
	}
	
	
}
