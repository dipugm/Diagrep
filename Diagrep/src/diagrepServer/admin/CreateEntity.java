package diagrepServer.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import diagrepServer.Utils.StringUtilities;
import diagrepServer.database.actions.category.AddOrModifyCategoryAction;
import diagrepServer.database.actions.collection.AddOrModifyCollectionAction;
import diagrepServer.database.actions.packages.AddOrModifyPackageAction;
import diagrepServer.database.actions.test.AddOrModifyTestAction;
import diagrepServer.servlets.BaseServlet;
import diagrepServer.database.actions.CommonDefs.EntityType;

/**
 * Servlet implementation class AddTest
 */
@WebServlet("/CreateEntity")
public class CreateEntity extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateEntity() {
        super();
        // TODO Auto-generated constructor stub
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
				// Mandatory parameters : Name, Cost. 
				// Optional parameters : Method, Normal Value
				String name = request.getParameter("name");
				String costString = request.getParameter("cost");
				String method = request.getParameter("method");
				String normalValue = request.getParameter("normalvalue");
				
				if( normalValue != null ) {
					normalValue = StringUtilities.decodeURLEncodedString( normalValue );
				}
				
				String unit = request.getParameter("unit");
				
				Double cost	= null;
				if( costString != null ) {
					cost = Double.parseDouble(costString);
				}
				
				response.setContentType("text/json");
				if( name == null || cost == null ) {
					response.getWriter().println("{\"status\":\"failure\", \"info\":\"mandatory data (name and cost) missing\"}");
					return;
				}
				
				Integer testId 	= (Integer)new AddOrModifyTestAction(name, cost, method, normalValue, unit, null ).doAction();
				
				if( testId == null ) {
					response.getWriter().println("{\"status\":\"failure\", \"info\":\"creating test failed\"}");
				} else {
					response.getWriter().println("{\"status\":\"success\", \"testId\":" + testId + "}");
				}
		}	break;
		case Category: {
			// Mandatory parameters : name, cost. 
			// Optional parameters : tests
			String name = request.getParameter("name");
			String costString = request.getParameter("cost");
			String tests = request.getParameter("subEntitiesWithOrder");
			
			if( tests.equalsIgnoreCase( "undefined" ) ) {
				tests = "";
			}
			
			Double cost	= null;
			if( costString != null ) {
				cost = Double.parseDouble(costString);
			}
			
			response.setContentType("text/json");
			if( name == null || cost == null ) {
				response.getWriter().println("{\"status\":\"failure\", \"info\":\"mandatory data (name and cost) missing\"}");
				return;
			}
			
			Object result = new AddOrModifyCategoryAction(name, tests, cost, null ).doAction();
			
			if( result == null ) {
				response.getWriter().println("{\"status\":\"failure\", \"info\":\"Creating category failed\"}");
			} else {
				response.getWriter().println("{\"status\":\"success\", \"categoryId\":" + result + "}");
			}
		}	break;
		
		case Collection: {
			
			// Mandatory parameters : Name. 
			// Optional parameters : Tests, Categories
			String name = request.getParameter("name");
			String subEntities = request.getParameter("subEntitiesWithOrder");
			String costString = request.getParameter("cost");
			
			Double cost	= null;
			if( costString != null ) {
				cost = Double.parseDouble(costString);
			}
			
			if( subEntities.equalsIgnoreCase("undefined") ) {
				subEntities = "";
			}
			
			response.setContentType("text/json");
			if( name == null ) {
				response.getWriter().println("{\"status\":\"failure\", \"info\":\"mandatory data (name) missing\"}");
				return;
			}
			
			Integer collectionId 	= (Integer)new AddOrModifyCollectionAction(name, cost, subEntities, null).doAction();
			
			if( collectionId == null ) {
				response.getWriter().println("{\"status\":\"failure\", \"info\":\"creating collection failed\"}");
			} else {
				response.getWriter().println("{\"status\":\"success\", \"collectionId\":" + collectionId + "}");
			}
		}	break;
		
		case Package: {
			// Mandatory parameters : Name, Cost. 
			// Optional parameters : Description, tests, categories, collections, orderinfo
			String name = request.getParameter("name");
			String costString = request.getParameter("cost");
			String description = request.getParameter( "description" );
			
			if( description != null ) {
				description = StringUtilities.decodeURLEncodedString( description );
			}
			
			String subEntities = request.getParameter("subEntitiesWithOrder");
			
			if( subEntities.equalsIgnoreCase("undefined") ) {
				subEntities = "";
			}
			
			Double cost	= null;
			if( costString != null ) {
				cost = Double.parseDouble(costString);
			}
			
			response.setContentType("text/json");
			if( name == null || cost == 0.0 ) {
				response.getWriter().println("{\"status\":\"failure\", \"info\":\"mandatory data (name and cost) missing\"}");
				return;
			}
			
			Integer testId 	= (Integer)new AddOrModifyPackageAction(name, description, cost, subEntities, null).doAction();
			
			if( testId == null ) {
				response.getWriter().println("{\"status\":\"failure\", \"info\":\"creating package failed\"}");
			} else {
				response.getWriter().println("{\"status\":\"success\", \"packageId\":" + testId + "}");
			}
		} break;
		

		default:
			break;
		}
	}
	
}
