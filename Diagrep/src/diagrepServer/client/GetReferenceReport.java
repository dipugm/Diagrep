package diagrepServer.client;

import diagrepServer.database.actions.analytics.GetReferenceReportAction;
import diagrepServer.servlets.BaseServlet;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GetReferenceReport
 */
@WebServlet("/GetReferenceReport")
public class GetReferenceReport extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see BaseServlet#BaseServlet()
     */
    public GetReferenceReport() {
        super();
        
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doCommonStuff();
		
		// Input : Reference name, start date and end date.
		String name 		= request.getParameter("reference");
		String startDate 	= request.getParameter("startDate");
		String endDate		= request.getParameter("endDate");
		
		response.setStatus( 200 );
		if( name == null || startDate == null || endDate == null ) {
			
			response.setContentType( "text/json" );
			response.getWriter().println(
				"{\"status\":\"failure\", \"info\":\"one or more of mandatory data (reference, startDate and endDate) missing\"}");
		} else {
			String respHtml = (String)
				new GetReferenceReportAction(name, Long.parseLong(startDate), Long.parseLong(endDate)).doAction();
			
			response.setContentType( "text/html" );
			response.getWriter().println( respHtml );
		}
	}


}
