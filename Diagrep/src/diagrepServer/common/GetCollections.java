package diagrepServer.common;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import diagrepServer.database.actions.collection.GetCollectionsAction;
import diagrepServer.database.model.CollectionObject;
import diagrepServer.servlets.BaseServlet;

/**
 * Servlet implementation class GetCollections
 */
@WebServlet("/GetCollections")
public class GetCollections extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetCollections() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		super.doCommonStuff();
		
        StringBuilder sb	= new StringBuilder();
		sb.append( "{\"collections\":[");
		
        @SuppressWarnings("unchecked")
		ArrayList<CollectionObject> arrayColls = (ArrayList<CollectionObject>) new GetCollectionsAction().doAction();
        for( int iColl=0; iColl < arrayColls.size(); iColl++ ) {
        	if( iColl > 0 ) {
				sb.append( ",");
			}
        	sb.append( arrayColls.get(iColl).getAsJson() );
        }
        
        sb.append("]}");
        
		response.setContentLength( sb.length() );
		response.setContentType( "text/json" );
		response.getWriter().print( sb.toString() );
		response.setStatus(200 );
	}

	
}
