package diagrepServer.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import diagrepServer.Utils.DiagrepConfig;
import diagrepServer.Utils.DiagrepTemplates;
import diagrepServer.database.core.DatabaseConnectionPool;

/**
 * Servlet implementation class BaseServlet
 */
@WebServlet("/BaseServlet")
public class BaseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BaseServlet() {
        super();
        
        // TODO Auto-generated constructor stub
    }
    
    // Should be called by subclasses in doGet and doPost methods.
    protected synchronized void doCommonStuff() {
    	if( DatabaseConnectionPool.getPool().getMasterDbConnection() == null ) {
	    	String webInfoPath = getServletContext().getRealPath(getServletInfo()) + "/WEB-INF/";
	    	
	    	DiagrepConfig.getConfig().loadConfig( webInfoPath );
	    	
	        DatabaseConnectionPool.getPool().initializeDbConnections( 
	        	DiagrepConfig.getConfig().get( DiagrepConfig.DB_FOLDER_PATH ) );
	        
	        // Read all templates files to memory.
	        String templatePath = webInfoPath + "/templates";
	        DiagrepTemplates.getInstance().readAllTemplates( templatePath );
    	}
    }

}
