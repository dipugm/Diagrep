import java.util.LinkedList;

public class ModifyMaster {

	public static void main(String[] args) {
		
		ModifyMaster mdd = new ModifyMaster();
		if( args.length < 2 ) {
			mdd.printGeneralUsage();
		}
		
		mdd.handleArguments(args);

	}
	
	public ModifyMaster() {
		super();
	}
	
	private void handleArguments(String[] args) {
		LinkedList<String> arguments = new LinkedList<String>();
		for(int i=0; i < args.length; i++) arguments.push(args[i]);
		
		switch(arguments.pop()) {
			case "-apdfm":
				handleAddingAppNameToDBFileMappingTable(arguments);
				break;
			
			default:
				System.out.println("Invalid command");
				printGeneralUsage();
				break;
		}
	}
	
	private void printGeneralUsage() {
		StringBuilder sb = new StringBuilder();
		sb.append("Usage : ModifyDD command <options>");
		sb.append("\nwhere command can be:");
		sb.append("-apdfm - Add app name to db file mapping table");
		sb.append("\n\noptions depend on command. Run the tool for a command without any options");
		sb.append("\n\nto know more about options for that command");
		
		System.out.println(sb.toString());
	}
	
	private void handleAddingAppNameToDBFileMappingTable(LinkedList<String> args) {
		if( args.size() != 1 ) {
			printAPDFMUsage();
			return;
		}
		
		
	}
	
	private void printAPDFMUsage() {
		
	}

}
