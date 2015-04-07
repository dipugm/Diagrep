package diagrepServer.client;

import diagrepServer.Utils.StringUtilities;
import diagrepServer.database.actions.report.GetReportsForCustomerAction;
import diagrepServer.database.model.BillObject;
import diagrepServer.servlets.BaseServlet;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GetReportsForCustomer
 */
@WebServlet("/GetReportsForCustomer")
public class GetReportsForCustomer extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see BaseServlet#BaseServlet()
     */
    public GetReportsForCustomer() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doCommonStuff();
		
		String custId = request.getParameter("customerId");
		
		response.setContentType("application/json");
		response.setStatus( 200 );
		if( custId == null || custId.isEmpty() || custId.equalsIgnoreCase("undefined") ) {
			response.getWriter().print( "{\"status\":\"failure\",\"info\":\"No customer id specified.\"}");
		} else if( false == StringUtilities.isCustomerId( custId ) ) {
			response.getWriter().print( "{\"status\":\"failure\",\"info\":\"Customer id does not exist.\"}");
		} else {
			ArrayList<BillObject> arr = (ArrayList<BillObject>)new GetReportsForCustomerAction(custId).doAction();
			
			StringBuilder sb = new StringBuilder();
			sb.append("{\"status\":\"success\",");
			sb.append("\"bills\":[");
			
			for( int i=0; i < arr.size(); i++ ) {
				if( i > 0 ) {
					sb.append( "," );
				}
				sb.append( arr.get(i) );
			}
			
			sb.append("]}");
			
			response.getWriter().print( sb.toString() );
		}
	}

}
