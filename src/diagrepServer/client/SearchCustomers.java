package diagrepServer.client;

import diagrepServer.database.actions.customer.LookupCustomers;
import diagrepServer.database.actions.customer.LookupCustomers.SearchType;
import diagrepServer.database.model.CustomerObject;
import diagrepServer.servlets.BaseServlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SearchCustomers
 */
@WebServlet("/SearchCustomers")
public class SearchCustomers extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see BaseServlet#BaseServlet()
     */
    public SearchCustomers() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doCommonStuff();
		
		String custId = request.getParameter("customerId");
		String custName = request.getParameter("customerName");
		String searchOldDbString = request.getParameter("shouldSearchOldDb");
		
		response.setStatus( 200 );
		
		if( ( (custId == null) || custId.equalsIgnoreCase( "undefined" ) ) &&
				( (custName == null) || custName.equalsIgnoreCase( "undefined" ) ) )	{
			response.setContentType("text/json");
			response.getWriter().println("{\"status\":\"failure\", \"info\":\"Please specify either Id or name (full or partial).\"}");
		} else {
		
			String data = null;
			SearchType st = SearchType.kCustomerId;
			if( custId != null) {
				data 	= custId;
			} else {
				data 	= custName;
				st 		= SearchType.kCustomerName;
			}
			
			boolean searchOldDb = true;
			if( searchOldDbString != null ) {
				searchOldDb = Boolean.parseBoolean( searchOldDbString );
			}
			
			HashMap<String, ArrayList<CustomerObject>> contents = 
					(HashMap<String, ArrayList<CustomerObject>>)(new LookupCustomers( data, st, searchOldDb ).doAction());
			
			if( contents != null ) {
				int count 	= 0;
				StringBuilder sbList = new StringBuilder();
				
				sbList.append( "{" );
				
				Iterator<String> iter = contents.keySet().iterator();
				while( iter.hasNext() ) {
					String key = iter.next();
					sbList.append( "\"" );
					sbList.append( key );
					sbList.append( "\":[" );
					
					ArrayList<CustomerObject> custs = contents.get( key );
					count += custs.size();
					for( int i=0; i < custs.size(); i++ ) {
						if( i > 0 ) {
							sbList.append(",");
						}
						CustomerObject co = custs.get(i);
						sbList.append( co );
					}
					
					sbList.append("],");
				}
				sbList.append("\"totalCount\":");
				sbList.append(count);
				sbList.append("}");
				
				response.setContentType("text/html");
				response.setContentLength( sbList.length() );
				response.getWriter().print( sbList.toString() );
			} else {
				response.setContentType("text/json");
				response.getWriter().println("{\"status\":\"failure\", \"info\":\"No customers found for the search string.\"}");
			}
			
		}
	}

}
