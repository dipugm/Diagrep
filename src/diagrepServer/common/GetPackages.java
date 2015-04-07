package diagrepServer.common;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import diagrepServer.database.actions.packages.GetPackagesAction;
import diagrepServer.database.model.PackageObject;
import diagrepServer.servlets.BaseServlet;

/**
 * Servlet implementation class GetPackages
 */
@WebServlet("/GetPackages")
public class GetPackages extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetPackages() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	super.doCommonStuff();
		
        StringBuilder sb	= new StringBuilder();
		sb.append( "{\"packages\":[");
		
        @SuppressWarnings("unchecked")
		ArrayList<PackageObject> arrayPkgs = (ArrayList<PackageObject>) new GetPackagesAction().doAction();
        for( int iPkg=0; iPkg < arrayPkgs.size(); iPkg++ ) {
        	if( iPkg > 0 ) {
				sb.append( ",");
			}
        	sb.append( arrayPkgs.get(iPkg).getAsJson() );
        }
        
        sb.append("]}");
        
		response.setContentLength( sb.length() );
		response.setContentType( "text/json" );
		response.getWriter().print( sb.toString() );
		response.setStatus(200 );
	}

}
