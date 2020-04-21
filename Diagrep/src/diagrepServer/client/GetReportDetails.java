package diagrepServer.client;

import diagrepServer.Utils.StringUtilities;
import diagrepServer.database.actions.report.GetReportAction;
import diagrepServer.database.model.ReportObject;
import diagrepServer.servlets.BaseServlet;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GetReportDetails
 */
@WebServlet("/GetReportDetails")
public class GetReportDetails extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see BaseServlet#BaseServlet()
     */
    public GetReportDetails() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doCommonStuff();
		
		String billNumber = request.getParameter("billNumber");
		String asHtml = request.getParameter("asHtml");
		
		response.setStatus( 200 );
		
		response.setContentType("text/json");
		if( billNumber == null || billNumber.equalsIgnoreCase("undefined") ) {
			response.getWriter().print("{\"status\":\"failure\",\"info\":\"Bill number must be provided.\"}" );
		} else {
			
			billNumber = billNumber.toUpperCase();
			
			if( false == StringUtilities.isBillNumber( billNumber ) ) {
				response.setContentType("text/json");
				response.getWriter().print("{\"status\":\"failure\", \"info\":\"Invalid bill number specified.\"}");
			} else {
				ReportObject ro = (ReportObject)(new GetReportAction(billNumber)).doAction();
				
				if( ro == null ) {
					response.getWriter().print("{\"status\":\"failure\",\"info\":\"Bill number does not exist.\"}" );	
				} else {
					
					if( asHtml == null || (Integer.parseInt(asHtml) == 0)) {
						response.getWriter().print( ro );
					} else {
						response.setContentType("text/html");
						response.getWriter().print( ro.getAsHtml() );
					}
				}
			}
		}
	}

}
