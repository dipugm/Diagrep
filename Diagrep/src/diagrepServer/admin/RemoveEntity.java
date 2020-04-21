package diagrepServer.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import diagrepServer.database.actions.BaseAction;
import diagrepServer.database.actions.CommonDefs.EntityType;
import diagrepServer.database.actions.category.DeleteCategoryAction;
import diagrepServer.database.actions.collection.DeleteCollectionAction;
import diagrepServer.database.actions.packages.DeletePackageObject;
import diagrepServer.database.actions.test.DeleteTestAction;
import diagrepServer.servlets.BaseServlet;

/**
 * Servlet implementation class RemoveTest
 */
@WebServlet("/RemoveEntity")
public class RemoveEntity extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RemoveEntity() {
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
		
		Integer id = null;
		String idString = request.getParameter("id") ;
		if( idString != null ) {
			id = Integer.parseInt( idString );
		}
		
		response.setContentType("text/json");
		response.setStatus(200);
		if( id == 0 || type == EntityType.NoType ) {
			response.getWriter().println("{\"status\":\"failure\", \"info\":\"mandatory data (id and type) missing\"}");
			return;
		}
		
		BaseAction action = null;
		switch( type ) {
		case Test: 
			action = new DeleteTestAction( id );
			break;
			
		case Category:
			action = new DeleteCategoryAction( id );
			break;
			
		case Collection:
			action = new DeleteCollectionAction( id );
			break;
			
		case Package:
			action = new DeletePackageObject( id );
			break;
		}
		
		boolean result = (Boolean)action.doAction();
		
		if( result ) {
			response.getWriter().println("{\"status\":\"success\"}");
		} else {
			response.getWriter().println("{\"status\":\"failure\"}");
		}
	}

}
