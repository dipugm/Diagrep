package diagrepServer.database.model;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import diagrepServer.database.core.ModelObject;

public class Recommendation extends ModelObject {
	public String name;
	public String data;
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append( "{" );
		sb.append("\"name\":\"");
		sb.append( name );
		sb.append("\",\"content\":\"");
		
		String encData;
		try {
			encData = URLEncoder.encode( data, "UTF-8");
			sb.append(encData);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sb.append("\"");
		sb.append( "}" );
		
		return sb.toString();
	}
}
