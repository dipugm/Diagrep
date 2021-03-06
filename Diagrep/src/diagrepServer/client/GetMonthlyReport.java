package diagrepServer.client;

import diagrepServer.database.actions.BaseAction;
import diagrepServer.database.actions.analytics.GetDaywiseMonthlyReportAction;
import diagrepServer.database.actions.analytics.GetMonthlyReportAction;
import diagrepServer.servlets.BaseServlet;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GetMonthlyReport
 */
@WebServlet("/GetMonthlyReport")
public class GetMonthlyReport extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see BaseServlet#BaseServlet()
     */
    public GetMonthlyReport() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doCommonStuff();
		
		// Input : reference date
		String referenceMonth 	= request.getParameter("month");
		String referenceYear 	= request.getParameter("year");
		String shouldGetBills	= request.getParameter("shouldIncludeBills");
		
		response.setStatus( 200 );
		if( referenceMonth == null && referenceYear == null ) {
			
			response.setContentType( "text/json" );
			response.getWriter().println(
				"{\"status\":\"failure\", \"info\":\"reference month or yearnot specified\"}");
		} else {
			BaseAction action = Boolean.parseBoolean(shouldGetBills) ?
				new GetDaywiseMonthlyReportAction(
						Long.parseLong(referenceMonth), 
						Long.parseLong(referenceYear)) :
				new GetMonthlyReportAction( 
						Long.parseLong(referenceMonth), 
						Long.parseLong(referenceYear));
				
							
			String respHtml = (String)action.doAction();
			
			response.setContentType( "text/html" );
			response.getWriter().println( respHtml );
		}
	}

	
}
