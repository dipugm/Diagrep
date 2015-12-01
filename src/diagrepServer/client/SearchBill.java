package diagrepServer.client;

import diagrepServer.Utils.StringUtilities;
import diagrepServer.database.actions.bill.GetBillContentsAction;
import diagrepServer.database.model.BillObject;
import diagrepServer.servlets.BaseServlet;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SearchBill
 */
@WebServlet("/SearchBill")
public class SearchBill extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see BaseServlet#BaseServlet()
     */
    public SearchBill() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doCommonStuff();
		
		String billNumber = request.getParameter("billNumber");
		
		response.setStatus( 200 );
		
		if( (billNumber == null) || billNumber.equalsIgnoreCase( "undefined" ) ) {
			response.setContentType("text/json");
			response.getWriter().println("{\"status\":\"failure\", \"info\":\"mandatory data (billNumber) missing\"}");
		} else {
		
			billNumber = billNumber.toUpperCase();
			
			if( false == StringUtilities.isBillNumber( billNumber ) ) {
				response.setContentType("text/json");
				response.getWriter().println("{\"status\":\"failure\", \"info\":\"Invalid bill number specified.\"}");
			} else {
				BillObject bo = (BillObject)(new GetBillContentsAction(billNumber).doAction());
				
				if( bo != null ) {
					String htmlText = bo.getAsHtml();
					response.setContentType("text/html");
					response.setContentLength( htmlText.length() );
					response.getWriter().print( htmlText );
				} else {
					response.setContentType("text/json");
					response.getWriter().println("{\"status\":\"failure\", \"info\":\"Bill does not exist or has been archived.\"}");
				}
			}			
		}
	}


}
