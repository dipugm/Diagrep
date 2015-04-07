package diagrepServer.client;

import diagrepServer.database.actions.bill.CreateNewBillAction;
import diagrepServer.servlets.BaseServlet;
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Servlet implementation class GenerateBill
 */
@WebServlet("/GenerateBill")
public class GenerateBill extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see BaseServlet#BaseServlet()
     */
    public GenerateBill() {
        super();
        // TODO Auto-generated constructor stub
    }

	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doCommonStuff();
		
		/* The request will come in as JSON data with the following format
		 * 
		 * {"entities" :
		 * 	[{type:<number>, id:<id>}],
		 *  "advance_paid" : amount in number,
		 *  "report_date" : data as number,
		 *  "customer_id" : "<Id of the customer",
		 *  "customer_name" : "";
		 *  "reference" : "<reference",
		 *  }
		 *  
		 *  response will be HTML containing the complete bill data.
		 */
		
		String billData = new String();
		String line = null;
		
		while( (line = request.getReader().readLine()) != null ) {
			billData += line;
		}
		
		System.out.println( billData );
		
		JSONParser parser = new JSONParser();
		try {
			Object retObj = parser.parse( billData );
			HashMap<String, Object> rootMap = (HashMap<String, Object>)retObj;
			
			JSONArray arrayEntities = (JSONArray)rootMap.get( "entities" );
			String customerId		= (String)rootMap.get("customer_id");
			float advance			= ((Double)rootMap.get("advance_paid")).floatValue();
			long reportDate			= (Long)rootMap.get("report_date");
			String reference		= (String)rootMap.get("referred_by");

			String respString = new String();
			String contentType = "text/json";

			// TODO Check if the customer id is a valid one.
			
			CreateNewBillAction action	= 
					new CreateNewBillAction(customerId, advance, reportDate, reference, arrayEntities);
			String billNumber = (String)action.doAction();

			if( billNumber != null ) {
				respString 	= "{\"status\":\"success\", \"billNumber\":\"" + billNumber + "\"}";
			} else {
				respString 	= "{\"status\":\"failed\", \"info\":\"Check customer Id.\"}";
			}
			
			response.setStatus(200);
			response.setContentType( contentType );
			response.setContentLength( respString.length() );
			response.getWriter().print( respString );
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
