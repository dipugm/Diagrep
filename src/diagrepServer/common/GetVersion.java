package diagrepServer.common;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import diagrepServer.servlets.BaseServlet;

/**
 * Servlet implementation class GetVersion
 */
@WebServlet(description = "Gets the version of the diagrep server", urlPatterns = { "/GetVersion" })
public class GetVersion extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetVersion() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		super.doCommonStuff();
		
		PrintWriter pw = response.getWriter();
		response.setContentType( "text/json" );
		pw.println("{'serverVersion':'1.0', 'dbVersion':'1.0'}");
		response.setStatus( 200 );
		
	}
	

}
