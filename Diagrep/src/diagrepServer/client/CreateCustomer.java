package diagrepServer.client;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import diagrepServer.database.actions.customer.CreateOrModifyCustomerAction;
import diagrepServer.servlets.BaseServlet;

/**
 * Servlet implementation class CreateCustomer
 */
@WebServlet("/CreateCustomer")
public class CreateCustomer extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateCustomer() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		super.doCommonStuff();
		
		String name 		= request.getParameter("name");
		String ageString 	= request.getParameter("age");
		String sexString	= request.getParameter("sex");
		String idString		= request.getParameter("customerId");
		
		response.setStatus( 200 );
		response.setContentType("text/json");
		// Mandatory data : Name, age, sex.
		if( name == null || ageString == null || sexString == null ) {
			response.getWriter().println("{\"status\":\"failure\", \"info\":\"Incomplete data.\"}");
		} else {
			CreateOrModifyCustomerAction action = 
					new CreateOrModifyCustomerAction(
							name, 
							Integer.parseInt(ageString), 
							Integer.parseInt(sexString), 
							idString);
			
			String custId = (String)action.doAction();

			response.getWriter().println("{\"status\":\"success\", \"customerId\":\"" + custId + "\"}");
		}
	}

}
