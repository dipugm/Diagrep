package diagrepServer.database.actions.category;

import java.util.ArrayList;

import diagrepServer.database.actions.BaseAction;
import diagrepServer.database.actions.CommonDefs.EntityType;
import diagrepServer.database.core.DatabaseCallParams;
import diagrepServer.database.core.DatabaseConnection;
import diagrepServer.database.core.DatabaseConnectionPool;
import diagrepServer.database.model.CategoryObject;

public class GetCategoriesAction extends BaseAction {

	public Object doAction() {
		
		DatabaseConnection dc 	= DatabaseConnectionPool.getPool().getMasterDbConnection();
		
		CategoryObject coTemp = new CategoryObject();
		DatabaseCallParams params = coTemp.prepareForFetch();
		params.orderByClause = "name";
		
		ArrayList<?> categories 	= dc.fetch(params);
		if( categories.size() > 0 ) {
			// Go through each of the tests and get a Json equivalent of it.
			for( int iCat=0; iCat < categories.size(); iCat++ ) {
				CategoryObject co	= (CategoryObject)categories.get( iCat );
				
				// Need to get the tests for this category.
				co.fk_tests	= super.getSubEntities( EntityType.Category, co.id ); 
			}
		}

		return categories;
	}
	
	public ArrayList<CategoryObject> getCategoriesFromIds( String categoryIds ) {
		String[] categories = categoryIds.split(",");

		ArrayList<CategoryObject> arrayCats 	= new ArrayList<CategoryObject>();
		for( int iCat=0; iCat < categories.length; iCat++ ) {
			CategoryObject co = (CategoryObject) new GetSingleCategoryAction( Integer.valueOf(categories[iCat]) ).doAction();
			
			if( co != null ) {
				arrayCats.add( co );
			}
		}
		
		return arrayCats;
	}
}
