package diagrepServer.common;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import diagrepServer.database.actions.test.GetTestsAction;
import diagrepServer.database.model.TestObject;
import diagrepServer.servlets.BaseServlet;

/**
 * Servlet implementation class GetTests
 */
@WebServlet("/GetTests")
public class GetTests extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetTests() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		super.doCommonStuff();
		
		StringBuilder sb	= new StringBuilder();
		sb.append( "{\"tests\":[");

		@SuppressWarnings("unchecked")
		ArrayList<TestObject> arrTests = (ArrayList<TestObject>)new GetTestsAction().doAction();
		// Go through each of the tests and get a Json equivalent of it.
		for( int iTest=0; iTest < arrTests.size(); iTest++ ) {
			if( iTest > 0 ) {
				sb.append( ",");
			}

			TestObject to	= (TestObject)arrTests.get( iTest );
			sb.append( to.getAsJson() );
		}

		sb.append("]}");
		
		response.setContentLength( sb.length() );
		response.setContentType( "text/json" );
		response.getWriter().print( sb.toString() );
		response.setStatus(200 );
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
