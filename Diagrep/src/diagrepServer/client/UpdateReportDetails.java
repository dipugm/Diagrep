package diagrepServer.client;

import diagrepServer.database.actions.report.ModifyReportAction;
import diagrepServer.servlets.BaseServlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Servlet implementation class UpdateReportDetails
 */
@WebServlet("/UpdateReportDetails")
public class UpdateReportDetails extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see BaseServlet#BaseServlet()
     */
    public UpdateReportDetails() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doCommonStuff();
		
		BufferedReader reader = request.getReader();
		String line = null;
		StringBuilder sb = new StringBuilder();
		while( (line = reader.readLine()) != null ) {
			sb.append( line );
		}
		
		/* Format will be as
		
		{
		"bill_number":"A00038",
		"fullUpdate":1,
		"recommendations":"",
		"test_results":
			{
				"id":14,
				"is_highlighted":0,
				"value":"",
				"description":""
			}
		}
		*/
		
		JSONParser parser = new JSONParser();
		try {
			Object retObj = parser.parse( sb.toString() );
			HashMap<String, Object> rootMap = (HashMap<String, Object>)retObj;
			
			String billNumber 	= (String)rootMap.get( "bill_number");
			billNumber = billNumber.toUpperCase();
			
			Long fullUpdate		= (Long)rootMap.get( "fullUpdate");
			String recos		= (String)rootMap.get( "recommendations");
			recos = URLDecoder.decode( recos, "UTF-8");
			
			ModifyReportAction action = null;
			if( fullUpdate == 1 ) {
				HashMap<String, Object> tdMap = (HashMap<String, Object>)rootMap.get("test_results");
				Long id 		= (Long)tdMap.get("id");
				Long isHigh 	= (Long)tdMap.get( "is_highlighted");
				String value 	= (String)tdMap.get("value");
				value = URLDecoder.decode( value, "UTF-8");
				
				String desc		= (String)tdMap.get("description");
				desc = URLDecoder.decode( desc, "UTF-8");
				
				action = new ModifyReportAction(billNumber, recos, id.intValue(), value, desc, isHigh.intValue());
			} else {
				action 	= new ModifyReportAction(billNumber, recos);
			}
			String anyError = (String)action.doAction();
			
			response.setStatus( 200 );
			response.setContentType( "text/json" );
			if( anyError == null ) {
				response.getWriter().print( "{\"status\":\"success\"}");
			} else {
				response.getWriter().print( "{\"status\":\"failure\", \"info\":\"" +  anyError + "\"}");
			}

		} catch( ParseException e) {
			e.printStackTrace();
		}
		
	}

}
