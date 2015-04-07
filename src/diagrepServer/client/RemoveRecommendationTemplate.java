package diagrepServer.client;

import diagrepServer.Utils.DiagrepTemplates;
import diagrepServer.servlets.BaseServlet;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class RemoveRecommendationTemplate
 */
@WebServlet("/RemoveRecommendationTemplate")
public class RemoveRecommendationTemplate extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see BaseServlet#BaseServlet()
     */
    public RemoveRecommendationTemplate() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doCommonStuff();
		
		String name = request.getParameter("name");
		
		response.setStatus( 200 );
		response.setContentType( "text/json" );
		if( name == null || name.equalsIgnoreCase("undefined") ) {
			response.getWriter().print( "{\"status\":\"failed\", \"info\":\"name not specified\"}" );
		} else {
			if( DiagrepTemplates.getInstance().deleteRecommendationTemplate( name ) ) {
				response.getWriter().print( "{\"status\":\"success\"}" );
			} else {
				response.getWriter().print( "{\"status\":\"failed\", \"info\":\"Name does not exist.\"}" );
			}
		}
	}

}
