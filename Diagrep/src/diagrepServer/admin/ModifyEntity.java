package diagrepServer.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import diagrepServer.Utils.StringUtilities;
import diagrepServer.database.actions.CommonDefs.EntityType;
import diagrepServer.database.actions.category.AddOrModifyCategoryAction;
import diagrepServer.database.actions.collection.AddOrModifyCollectionAction;
import diagrepServer.database.actions.packages.AddOrModifyPackageAction;
import diagrepServer.database.actions.test.AddOrModifyTestAction;
import diagrepServer.servlets.BaseServlet;

/**
 * Servlet implementation class ModifyEntity
 */
@WebServlet("/ModifyEntity")
public class ModifyEntity extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ModifyEntity() {
        super();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		super.doCommonStuff();
		
		String typeString = request.getParameter("type");
		EntityType type = EntityType.NoType;
		if( typeString != null ) {
			type = EntityType.fromValue( typeString );
		}
		
		response.setContentType("text/json");
		response.setStatus(200);
		if( type == EntityType.NoType ) {
			response.getWriter().println("{\"status\":\"failure\", \"info\":\"mandatory data (type) missing\"}");
			return;
		}
		
		switch( type ) {
		case Test: {
				// Mandatory parameters : Id 
				// Optional parameters : Method, Normal Value
				String costString = request.getParameter("cost");
				String method = request.getParameter("method");
				String normalValue = request.getParameter("normalvalue");
				
				if( normalValue != null ) {
					normalValue = StringUtilities.decodeURLEncodedString( normalValue );
				}
				
				String idString = request.getParameter("id");
				Integer id = null;
				if( idString != null ) {
					id = Integer.parseInt( idString );
				}
				Double cost = null;
				if( costString != null ) {
					cost 	= Double.parseDouble( costString );
				}
				
				response.setContentType("text/json");
				if( id == null ) {
					response.getWriter().println("{\"status\":\"failure\", \"info\":\"mandatory data (id) missing\"}");
					return;
				}
				
				Boolean bResult 	= (Boolean)new AddOrModifyTestAction(null, cost, method, normalValue, null, id ).doAction();
				
				if( bResult ) {
					response.getWriter().println("{\"status\":\"success\", \"testId\":" + idString + "}");
				} else {
					response.getWriter().println("{\"status\":\"failure\", \"info\":\"creating test failed\"}");
				}
				
		}	break;
		case Category: {
			// Mandatory parameters : Id. 
			// Optional parameters : Cost, Tests
			String costString = request.getParameter("cost");
			String idString = request.getParameter("id");
			String tests = request.getParameter("subEntitiesWithOrder");
			
			if( tests.equalsIgnoreCase( "undefined" ) ) {
				tests = "";
			}
			
			Integer id = null;
			if( idString != null ) {
				id = Integer.parseInt( idString );
			}
			Double cost = null;
			if( costString != null ) {
				cost = Double.parseDouble( costString );
			}
			
			response.setContentType("text/json");
			if( id == null ) {
				response.getWriter().println("{\"status\":\"failure\", \"info\":\"mandatory data (id) missing for modification of category\"}");
				return;
			}
			
			Boolean bResult = (Boolean)new AddOrModifyCategoryAction(null, tests, cost, id ).doAction();
			
			if( bResult ) {
				response.getWriter().println("{\"status\":\"success\", \"categoryId\":" + idString + "}");
			} else {
				response.getWriter().println("{\"status\":\"failure\", \"info\":\"Modifying category failed\"}");
			} 
		}	break;
		
		case Collection: {
			
			// Mandatory parameters : Id. 
			// Optional parameters : Tests, Categories, Cost
			String subEntities = request.getParameter("subEntitiesWithOrder");
			String costString = request.getParameter("cost");
			String idString = request.getParameter("id");
			
			if( subEntities.equalsIgnoreCase( "undefined" ) ) {
				subEntities = "";
			}
			
			Integer id = null;
			if( idString != null ) {
				id = Integer.parseInt( idString );
			}
			
			Double cost = null;
			if( costString != null ) {
				cost = Double.parseDouble( costString );
			}
			
			response.setContentType("text/json");
			if( id == null ) {
				response.getWriter().println("{\"status\":\"failure\", \"info\":\"mandatory data (id) missing\"}");
				return;
			}
			
			Boolean result 	= (Boolean)new AddOrModifyCollectionAction(null, cost, subEntities, id).doAction();
			
			if( result ) {
				response.getWriter().println("{\"status\":\"success\", \"collectionId\":" + idString + "}");				
			} else {
				response.getWriter().println("{\"status\":\"failure\", \"info\":\"creating collection failed\"}");
			}
		}	break;
		
		case Package: {
			// Mandatory parameters : id. 
			// Optional parameters : cost, description, tests, categories, collections
			String costString 	= request.getParameter("cost");
			String description 	= request.getParameter("description" );
			String subEntities 	= request.getParameter("subEntitiesWithOrder");
			
			String idString = request.getParameter("id");
			Integer id = null;
			if( idString != null ) {
				id = Integer.parseInt( idString );
			}
			
			Double cost = null;
			if( costString != null ) {
				cost = Double.parseDouble( costString );
			}
			
			response.setContentType("text/json");
			if( id == null ) {
				response.getWriter().println("{\"status\":\"failure\", \"info\":\"mandatory data (id) missing to modify package.\"}");
				return;
			}
			
			Boolean bResult = (Boolean)new AddOrModifyPackageAction(null, description, cost, subEntities, id).doAction();
			
			if( bResult ) {
				response.getWriter().println("{\"status\":\"success\", \"id\":" + idString + "}");
			} else {
				response.getWriter().println("{\"status\":\"failure\", \"info\":\"creating package failed\"}");
			}
		} break;
		
		default:
			break;
		
		}
	}

}
