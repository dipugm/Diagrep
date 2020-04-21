package diagrepServer.database.model;

import java.util.ArrayList;

import diagrepServer.Utils.DiagrepTemplates;
import diagrepServer.Utils.StringUtilities;
import diagrepServer.Utils.DiagrepTemplates.TemplateType;
import diagrepServer.database.actions.CommonDefs.EntityType;
import diagrepServer.database.actions.category.GetSingleCategoryAction;
import diagrepServer.database.actions.collection.GetSingleCollectionAction;
import diagrepServer.database.actions.packages.GetSinglePackageAction;
import diagrepServer.database.actions.test.GetSingleTestAction;
import diagrepServer.database.core.ModelObject;

public class BillDetailsObject extends ModelObject implements IEntityObject  {

	public String billNumber;
	public Integer entityType;
	public Integer entityId;
	public Double cost;
	
	public int fk_index;
	
	public BillDetailsObject() {
		
	}

	@Override
	public double getCost() {
		double c = cost;
		if( c == 0.0 ) {
			switch( EntityType.fromValue( String.valueOf(entityType) ) ) {
				case Test: {
					GetSingleTestAction action	= new GetSingleTestAction( entityId );
					TestObject to = (TestObject)action.doAction();
					if( to != null ) {
						c = to.cost;
					}
					
				} break;
					
				case Category: {
					GetSingleCategoryAction action	= new GetSingleCategoryAction( entityId );
					CategoryObject co = (CategoryObject)action.doAction();
					
					if( co != null ) {
						c = co.cost;
					}
		
				} break;
					
				case Collection: {
					GetSingleCollectionAction action	= new GetSingleCollectionAction( entityId );
					CollectionObject co = (CollectionObject)action.doAction();
					
					if( co != null ) {
						c = co.cost;
					}
		
				} break;
					
				case Package: {
					GetSinglePackageAction action	= new GetSinglePackageAction ( entityId );
					PackageObject po = (PackageObject)action.doAction();
					
					if( po != null ) {
						c = po.cost;
					}
		
				} break;
			}
		}
		return c;
	}

	@Override
	public String getAsHtml() {
		
		String name = getEntityName();
		
		/*
		 *  We use template files to create the final Html.
		 *  Templates used :
		 *  1. BillRowTempl
		 *  
		 *  Strings we look for :
		 *  1. !@#$SlNo$#@!
		 *  2. !@#$Particulars$#@!
		 *  3. !@#$Cost$#@!
		 */
		
		String templ = DiagrepTemplates.getInstance().getTemplateFor( TemplateType.kBillRow );
		
		templ = templ.replace( "%SlNo%", String.valueOf(fk_index) );
		templ = templ.replace( "%TestName%", name );
		templ = templ.replace( "%Cost%", String.valueOf( getCost() ) );
		
		return templ;
	}

	@Override
	public ArrayList<ModelObject> getContainedTests() {
		// TODO Auto-generated method stub
		return null;
	}
	
	// Utility method
	public String getEntityName() {
		String name = "- DELETED ENTITY - ";
		switch( EntityType.fromValue( String.valueOf(entityType) ) ) {
			case Test: {
				GetSingleTestAction action	= new GetSingleTestAction( entityId );
				TestObject to = (TestObject)action.doAction();
				if( to != null ) {
					name = StringUtilities.decodeURLEncodedString( to.name );
				}
				
			} break;
				
			case Category: {
				GetSingleCategoryAction action	= new GetSingleCategoryAction( entityId );
				CategoryObject co = (CategoryObject)action.doAction();
				
				if( co != null ) {
					name = StringUtilities.decodeURLEncodedString( co.name );
				}
	
			} break;
				
			case Collection: {
				GetSingleCollectionAction action	= new GetSingleCollectionAction( entityId );
				CollectionObject co = (CollectionObject)action.doAction();
				
				if( co != null ) {
					name = StringUtilities.decodeURLEncodedString( co.name );
				}
	
			} break;
				
			case Package: {
				GetSinglePackageAction action	= new GetSinglePackageAction ( entityId );
				PackageObject po = (PackageObject)action.doAction();
				
				if( po != null ) {
					name = StringUtilities.decodeURLEncodedString( po.name );
				}
	
			} break;
			
			default:
				break;
		}
		
		return name;
	}
}
