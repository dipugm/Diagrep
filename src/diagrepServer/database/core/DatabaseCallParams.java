package diagrepServer.database.core;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class DatabaseCallParams {
	
	public enum EnumOperationType {
		kSqlFetch,
		kSqlInsert,
		kSqlUpdate,
		kSqlDelete
	}
	
	public static abstract class ConditionBase {
		public String columnName;
		public String value;
		
		public String condition;
		public String toString() {
			return condition;
		}
	}
	
	public static class ConditionLessThan extends ConditionBase {
		public ConditionLessThan( String columnName, Object value ) {
			condition	= columnName + " < ";
			condition 	+= String.valueOf( value );
		}
	}
	
	public static class ConditionLessThanOrEqual extends ConditionBase {
		public ConditionLessThanOrEqual( String columnName, Object value ) {
			condition	= columnName + " <= ";
			condition 	+= String.valueOf( value );
		}
	}
	
	public static class ConditionGreaterThan extends ConditionBase {
		public ConditionGreaterThan( String columnName, Object value ) {
			condition	= columnName + " > ";
			condition 	+= String.valueOf( value );
		}
	}
	
	public static class ConditionGreaterThanOrEqual extends ConditionBase {
		public ConditionGreaterThanOrEqual( String columnName, Object value ) {
			condition	= columnName + " >= ";
			condition 	+= String.valueOf( value );
		}
	}
	
	public static class ConditionEquals extends ConditionBase {
		public ConditionEquals( String columnName, Object value ) {
			condition	= columnName + "=";
			if( value.getClass().equals( String.class ) ) {
				condition += "'" + value + "'";
			} else {
				condition += String.valueOf( value );
			}
		}
	}
	
	public static class ConditionLike extends ConditionBase {
		public ConditionLike( String columnName, Object value ) {
			condition	= columnName + " like ";
			if( value.getClass().equals( String.class ) == false ) {
				value 	= String.valueOf( value );
			}
			condition += "'" + value + "%'";
		}
	}

	private EnumOperationType operationType;
	
	public String tableName;
	public String modelClassName;
	private ArrayList<ConditionBase> conditions;
	public String orderByClause;
	public boolean shouldOrderByDescending;
	
	private Hashtable<String, Object> dbFields;
	private String computedCondition;
	
	@SuppressWarnings("unused")
	private DatabaseCallParams() {}
	
	public DatabaseCallParams( EnumOperationType ot ) {
		operationType	= ot;
		
		conditions 		= new ArrayList<ConditionBase>();
		orderByClause 	= "";
		dbFields 		= new Hashtable<String, Object>();
		tableName 		= "";
		modelClassName	= "";
		
		computedCondition	= "";
	}
	
	public String toString() {
		return getSqlStatement();
	}
	
	public void addParam( String key, Object value ) {
		this.dbFields.put( key, value );
	}
	
	public void addCondition( ConditionBase condition ) {
		
		if( conditions.size() > 0 ) {
			computedCondition += " AND ";
		}
		conditions.add( condition );
		
		computedCondition += condition.toString();
	}
	
	public void deriveConditions( boolean clearFields ) {
		Enumeration<String> enmr = this.dbFields.keys();
		while( enmr.hasMoreElements() ) {
	 		String key	= enmr.nextElement();
			this.addCondition( new ConditionEquals(key, this.dbFields.get(key)) );
		}
		
		if( clearFields ) {
			this.dbFields.clear();
		}
	}
	
	public String getSqlStatement() {
		 String sqlStatement = "";
		 
		 switch( this.operationType ) {
		 case kSqlFetch:
			 sqlStatement	= this.prepareSqlStatementForFetch();
			 break;
			 
		 case kSqlInsert:
			 sqlStatement	= this.prepareSqlStatementForInsert();
			 break;
			 
		 case kSqlUpdate:
			 sqlStatement	= this.prepareSqlStatementForUpdate();
			 break;
			 
		 case kSqlDelete:
			 sqlStatement	= this.prepareSqlStatementForDelete();
			 break;
		 }
		 
		 return sqlStatement;
	}
	
	private String prepareSqlStatementForFetch () {
		StringBuilder sBuilder = new StringBuilder();

		 // Go through the fields to fetch otherwise use *
		 String fields 	= "rowid, *";
		 if( dbFields.size() > 0 ) {
			 fields = "rowid";
			 for( int i=0; i < dbFields.size(); i++ ) {
				 fields += "," + dbFields.get(i);
			 }
		 } 
		 
		 sBuilder.append( "SELECT ");
		 sBuilder.append( fields );
		 sBuilder.append( " FROM " );
		 sBuilder.append( tableName );
		 
		 // If there are any conditions, we need to use it.
		 if( computedCondition != "" ) {
			 sBuilder.append( " WHERE " );
			 sBuilder.append( computedCondition );
		 }
		 
		 if( orderByClause != "" ) {
			 sBuilder.append( " ORDER BY " );
			 sBuilder.append( orderByClause );
			 
			 if( shouldOrderByDescending ) {
				 sBuilder.append( " DESC ");
			 } else {
				 sBuilder.append( " ASC ");
			 }
		 }
		 
		 return sBuilder.toString(); 
	}
	
	private String prepareSqlStatementForInsert () {
		StringBuilder sBuilder = new StringBuilder();

		 // Go through the fields to fetch otherwise use *
		 String fields 	= "";
		 String values 	= "";
		 if( dbFields.size() > 0 ) {
			 Enumeration<String> enmr = dbFields.keys();
			 
			 String key = "";
			 while( enmr.hasMoreElements() ) {
				 key = enmr.nextElement();
				 
				 if( fields != "") {
					 fields += ",";
					 values += ",";
				 }
				 fields += key;
				 
				 if( dbFields.get(key).getClass().equals( String.class ) ) {
					 values += "'" + dbFields.get(key) + "'"; 
				 } else {
					 values += String.valueOf( dbFields.get(key) );
				 }
			 }
			 
		 } 
		 
		 sBuilder.append( "INSERT INTO ");
		 sBuilder.append( this.tableName );
		 sBuilder.append( "(" );
		 sBuilder.append( fields );
		 sBuilder.append( ") VALUES (" );
		 sBuilder.append( values );
		 sBuilder.append( ")" );
		 
		 // If there are any conditions, we need to use it.
		 if( computedCondition != "" ) {
			 sBuilder.append( " WHERE " );
			 sBuilder.append( computedCondition );
		 }
		 
		 return sBuilder.toString(); 
	}
	
	private String prepareSqlStatementForUpdate() {
		StringBuilder sBuilder = new StringBuilder();

		 // Go through the fields to fetch otherwise use *
		 
		 if( dbFields.size() > 0 ) {
			 
			 sBuilder.append( "UPDATE ");
			 sBuilder.append( this.tableName );
			 sBuilder.append( " SET ");
			 
			 Enumeration<String> enmr = dbFields.keys();
			 
			 boolean secondFieldOnwards = false;
			 String field = "";
			 while( enmr.hasMoreElements() ) {
				 field = enmr.nextElement();
				 
				 if( secondFieldOnwards ) {
					 sBuilder.append(", ");
				 }
				 secondFieldOnwards	= true;
				 sBuilder.append(field);
				 sBuilder.append("=");
				 
				 String value 	= "";
				 if( dbFields.get(field).getClass().equals( String.class ) ) {
					 value += "'" + dbFields.get(field) + "'"; 
				 } else {
					 value += String.valueOf( dbFields.get(field) );
				 }
				 sBuilder.append(value);
			 }
			 
			 // If there are any conditions, we need to use it.
			 if( computedCondition != "" ) {
				 sBuilder.append( " WHERE " );
				 sBuilder.append( computedCondition );
			 }
			 
			 return sBuilder.toString();
		 } 
		 
		 return "";
		  
	}
	
	private String prepareSqlStatementForDelete() {
		StringBuilder sBuilder = new StringBuilder();

		 // Go through the fields to fetch otherwise use *
		 
		 sBuilder.append( "DELETE FROM " );
		 sBuilder.append( tableName );
		 
		 // If there are any conditions, we need to use it.
		 if( computedCondition != "" ) {
			 sBuilder.append( " WHERE " );
			 sBuilder.append( computedCondition );
		 }
		 
		 return sBuilder.toString(); 
	}
}
