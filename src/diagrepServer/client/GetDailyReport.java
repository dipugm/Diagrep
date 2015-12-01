package diagrepServer.client;

import diagrepServer.database.actions.analytics.GetDailyReportAction;
import diagrepServer.servlets.BaseServlet;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GetDialyReport
 */
@WebServlet("/GetDailyReport")
public class GetDailyReport extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see BaseServlet#BaseServlet()
     */
    public GetDailyReport() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doCommonStuff();
		
		// Input : reference date
		String referenceDate 	= request.getParameter("referenceDate");
		
		response.setStatus( 200 );
		if( referenceDate == null ) {
			
			response.setContentType( "text/json" );
			response.getWriter().println(
				"{\"status\":\"failure\", \"info\":\"reference date not specified\"}");
		} else {
			String respHtml = (String)
				new GetDailyReportAction( Long.parseLong(referenceDate)).doAction();
			
			response.setContentType( "text/html" );
			response.getWriter().println( respHtml );
		}
	}


}
