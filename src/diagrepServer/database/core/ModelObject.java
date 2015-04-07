package diagrepServer.database.core;

import java.lang.reflect.Field;
import java.util.ArrayList;

import diagrepServer.database.core.DatabaseCallParams.EnumOperationType;
import diagrepServer.database.model.IEntityObject;

public class ModelObject implements IEntityObject {
	
	public Integer id		= null;
	public Integer rowid 	= null;
	
	public ModelObject() {
		rowid	= null;
	}
	
	public String toString() {
		return this.getAsJson();
	}
	
	public void fillFKs() {
	}
	
	public Object doAnyTypeConversions( String columnName, Object value ) {
		return value;
	}
	
	private ArrayList<Field> getNonNullFields() {
		ArrayList<Field> nonNullFields = new ArrayList<Field>();
		
		Field[] fields = getClass().getFields();
		for( int iField=0; iField < fields.length; iField++ ) {
			Field f = fields[iField]; 
			try {
				if( (f.getName().indexOf("fk_")) < 0 && (f.get(this) != null) ) {
					nonNullFields.add( f );
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return nonNullFields;
	}
	
	private void fillParams( DatabaseCallParams params ) {
		ArrayList<Field> arrayFields = this.getNonNullFields();
		for( int iField=0; iField < arrayFields.size(); iField++ ) {
			Field f = arrayFields.get( iField ) ;
			try {
				params.addParam( f.getName() , f.get(this) );
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	 
	public DatabaseCallParams prepareForSave(boolean forceAsUpdate) {
		DatabaseCallParams.EnumOperationType eot = EnumOperationType.kSqlInsert;
		if( this.rowid != null || forceAsUpdate) {
			eot		= EnumOperationType.kSqlUpdate;
		}
		DatabaseCallParams params	= new DatabaseCallParams( eot );
		
		DeriveTableAndModelNames(params);
		
		/*
		 * If this is a update, we use the rowid as the condition and the field values to be modified.
		 */
		if( this.rowid != null ) {
			params.addCondition( new DatabaseCallParams.ConditionEquals( "rowid", this.rowid ) );
			this.rowid = null;	
		}
		
		this.fillParams( params );
		
		return params;
	}
	
	public DatabaseCallParams prepareForDelete() {
		DatabaseCallParams params = new DatabaseCallParams( EnumOperationType.kSqlDelete );
		
		DeriveTableAndModelNames(params);
		
		// prepare the params picking the properties that are non-null.
		this.fillParams(params);
		
		params.deriveConditions( true );
		
		return params;
	}
	
	public DatabaseCallParams prepreForCreate() {
		DatabaseCallParams params = new DatabaseCallParams( EnumOperationType.kSqlInsert );
		
		DeriveTableAndModelNames(params);
		
		// prepare the params picking the properties that are non-null.
		this.fillParams(params);
		
		return params;
	}
	
	public DatabaseCallParams prepareForFetch() {
		DatabaseCallParams params = new DatabaseCallParams( EnumOperationType.kSqlFetch );
		
		DeriveTableAndModelNames(params);
		
		// prepare the params picking the properties that are non-null.
		this.fillParams(params);
		
		params.deriveConditions( true );
		
		return params;
	}
	
	public String getAsJson() {
		StringBuilder sb 	= new StringBuilder();
				
		return sb.toString();
	}
	
	private void DeriveTableAndModelNames( DatabaseCallParams params ) {
		params.modelClassName = this.getClass().getName();
		params.tableName = params.modelClassName.substring( params.modelClassName.lastIndexOf('.') + 1 ).replace("Object", "");
	}

	@Override
	public double getCost() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getAsHtml() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<ModelObject> getContainedTests() {
		// TODO Auto-generated method stub
		return null;
	}
}
