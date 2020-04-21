package diagrepServer.database.model;

import diagrepServer.database.core.ModelObject;

public class DataDictionary extends ModelObject {

	public String paramName;
	public String paramValue;
	
	public String toString() {
		return paramName + " => " + paramValue;
	}
}
