package diagrepServer.client;

import diagrepServer.database.actions.reference.GetAllReferencesAction;
import diagrepServer.database.model.ReferenceObject;
import diagrepServer.servlets.BaseServlet;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GetReferences
 */
@WebServlet("/GetReferences")
public class GetReferences extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see BaseServlet#BaseServlet()
     */
    public GetReferences() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doCommonStuff();
		
		GetAllReferencesAction action = new GetAllReferencesAction();
		ArrayList<ReferenceObject> references = (ArrayList<ReferenceObject>)action.doAction();
		
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append( "{\"result\":[" );
		for( int i=0; i < references.size(); i++ ) {
			ReferenceObject ro = references.get(i);
			sBuilder.append( "{\"name\":\"" );
			sBuilder.append( ro.name );
			sBuilder.append( "\", \"id\":" );
			sBuilder.append( ro.id.toString() );
			sBuilder.append( "}," );
		}
	
		String data = sBuilder.toString();
		data = data.substring(0, data.length() - 1 ) + "]}";

		response.setStatus( 200 );
		response.setContentType("text/json");
		response.setContentLength( data.length() );
		response.getWriter().print( data );
	}

}
