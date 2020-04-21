package diagrepServer.common;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import diagrepServer.database.actions.category.GetCategoriesAction;
import diagrepServer.database.model.CategoryObject;
import diagrepServer.servlets.BaseServlet;

/**
 * Servlet implementation class GetCategories
 */
@WebServlet("/GetCategories")
public class GetCategories extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetCategories() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				
		super.doCommonStuff();
				
        StringBuilder sb	= new StringBuilder();
		sb.append( "{\"categories\":[");
		
        @SuppressWarnings("unchecked")
		ArrayList<CategoryObject> arrayCats = (ArrayList<CategoryObject>) new GetCategoriesAction().doAction();
        for( int iCat=0; iCat < arrayCats.size(); iCat++ ) {
        	if( iCat > 0 ) {
				sb.append( ",");
			}
        	sb.append( arrayCats.get(iCat).getAsJson() );
        }
        
        sb.append("]}");
        
		response.setContentLength( sb.length() );
		response.setContentType( "text/json" );
		response.getWriter().print( sb.toString() );
		response.setStatus(200 );
	}
	
}
