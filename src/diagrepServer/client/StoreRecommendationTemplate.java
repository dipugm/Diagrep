package diagrepServer.client;

import diagrepServer.Utils.DiagrepTemplates;
import diagrepServer.servlets.BaseServlet;
import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class StoreRecommendationTemplate
 */
@WebServlet("/StoreRecommendationTemplate")
public class StoreRecommendationTemplate extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see BaseServlet#BaseServlet()
     */
    public StoreRecommendationTemplate() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doCommonStuff();
		
		// Mandatory data : name, content
		String name = request.getParameter("name");
		String content = request.getParameter("content");
		
		System.out.println( content );
		String convData = URLDecoder.decode( content, "UTF-8");
		
		DiagrepTemplates.getInstance().saveRecommendationTemplate( name, convData );
		
		response.setStatus( 200 );
		response.setContentType( "text/json" );
		response.getWriter().print( "{\"status\":\"success\"}" );
		
		
	}

}
