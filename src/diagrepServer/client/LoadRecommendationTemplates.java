package diagrepServer.client;

import diagrepServer.Utils.DiagrepTemplates;
import diagrepServer.servlets.BaseServlet;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class LoadRecommendationTemplates
 */
@WebServlet("/LoadRecommendationTemplates")
public class LoadRecommendationTemplates extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see BaseServlet#BaseServlet()
     */
    public LoadRecommendationTemplates() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doCommonStuff();
		
		String recos = DiagrepTemplates.getInstance().getRecommendationTemplates();
		
		response.setStatus( 200 );
		response.setContentType( "text/json" );
		response.setContentLength( recos.length() );
		response.getWriter().print( recos );
	}


}
