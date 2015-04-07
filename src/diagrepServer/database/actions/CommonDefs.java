package diagrepServer.database.actions;

public class CommonDefs {

	public enum EntityType {
		Test,
		Category,
		Collection,
		Package,
		NoType;
		
		public static EntityType fromValue( String value ) {
			EntityType et = NoType;
			if( value.equalsIgnoreCase("0") ) {
				et	= Test;
			} else if( value.equalsIgnoreCase("1") ) {
				et	= Category;
			} else if( value.equalsIgnoreCase("2") ) {
				et	= Collection;
			} else if( value.equalsIgnoreCase("3") ) {
				et	= Package;
			}
			
			return et;
		}
	}
}
