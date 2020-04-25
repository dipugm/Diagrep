import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

public class ModifyMaster {

	public static void main(String[] args) {
		
		ModifyMaster mdd = new ModifyMaster();
		if( args.length < 1 ) {
			mdd.printGeneralUsage();
		}
		
		mdd.handleArguments(args);

	}
	
	class Parameters extends LinkedList<String> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public void push(String s) {
			super.addLast(s);
		}
		
	}
	
	class SubParameters extends Hashtable<String, String> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public SubParameters processParameters(Parameters params) {
			Iterator<String> iter = params.iterator();
			while(iter.hasNext()) {
				String param = iter.next();
				int ind = param.indexOf(":");
				
				if( ind > -1 ) {
					this.put( param.substring(0, ind), param.substring(ind+1, param.length()) );
				}
			}
			
			return this;
		}
		
	}
	
	public ModifyMaster() {
		super();
	}
	
	private void handleArguments(String[] args) {
		Parameters arguments = new Parameters();
		for(int i=0; i < args.length; i++) arguments.push(args[i]);
		
		if( arguments.size() > 0 ) {
			switch(arguments.pop()) {
				case "-apdfm":
					handleAddingAppNameToDBFileMappingTable(arguments);
					break;
				
				default:
					System.out.println("Invalid command");
					printGeneralUsage();
					break;
			}
		} else {
			printGeneralUsage();
		}
	}
	
	private void printGeneralUsage() {
		StringBuilder sb = new StringBuilder();
		sb.append("Usage : ModifyMaster command <options>");
		sb.append("\n\nSupported Commands:");
		sb.append("\n-apdfm \t\t Add app name to db file mapping table");
		sb.append("\n\noptions depend on command. \n\nRun the tool for a command without any options ");
		sb.append("to know more about options for that command");
		sb.append("\n\n");
		
		System.out.println(sb.toString());
	}
	
	private void handleAddingAppNameToDBFileMappingTable(Parameters args) {
		SubParameters subparams = new SubParameters().processParameters(args);
		if( subparams.containsKey("a") && subparams.containsKey("dbp") ) {
			System.out.println("Modifying Db file mapping table for : " + subparams.get("a"));
			
			Connection conn = getDbConnectionForFile(subparams.get("dbp") + "/master.db");
			
			try {
				conn.createStatement().execute("ALTER TABLE DbFileMapping ADD COLUMN appName TEXT");
				conn.createStatement().execute(
					"UPDATE DbFileMapping SET appName='" + subparams.get("a") + "' WHERE type=0");
				conn.close();
				
				System.out.println("Successfully modified the db file mapping table");
			} catch( Exception e) {
				System.err.println(e);
			}
			
		} else {
			printAPDFMUsage();
		}
		
	}
	
	private void printAPDFMUsage() {
		StringBuilder sb = new StringBuilder();
		sb.append("Invalid application name");
		sb.append("\nUsage : -apdfm a:<application name> dbp:<db folder path>\n\n");
		
		System.out.println(sb);
	}
	
	private Connection getDbConnectionForFile(String filePath) {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:" + filePath);
		} catch( Exception e) {
			System.out.println(e);
		}
		return conn;
	}
	
}
