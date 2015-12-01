package diagrepServer.database.actions.customer;

import java.util.Date;

import diagrepServer.Utils.DataDictionaryUtitlities;
import diagrepServer.Utils.DatabaseUtility;
import diagrepServer.Utils.DiagrepConfig;
import diagrepServer.database.actions.BaseAction;
import diagrepServer.database.core.DatabaseCallParams;
import diagrepServer.database.core.DatabaseCallParams.ConditionEquals;
import diagrepServer.database.core.DatabaseConnection.EnumDBResult;
import diagrepServer.database.core.DatabaseConnection;
import diagrepServer.database.core.DatabaseConnectionPool;
import diagrepServer.database.model.CustomerObject;

public class CreateOrModifyCustomerAction extends BaseAction {

	private CustomerObject customerObject;
	private String customerIdForModify;
	private String dbFileName;	
	
	public CreateOrModifyCustomerAction(String name, int age, int sex, String custId ) {
		
		this.customerObject = new CustomerObject();
		this.customerObject.name 	= name;
		this.customerObject.age		= age;
		this.customerObject.sex		= sex;
		this.customerObject.dateOfCreation	= (long)new Date().getTime();
		
		if( custId == null ) {
			this.PrepareNewCustomerId( null );
		} else {
			this.customerIdForModify	= custId;
			this.dbFileName		= DatabaseUtility.getDbFileNameForIdAndType( custId, 1);
		}
	}
	
	public Object doAction() {
		int tryCount = 0;
		
		EnumDBResult res = EnumDBResult.DB_SUCCESS;
		do {
			if( this.dbFileName != null && ! this.dbFileName.isEmpty() ) {
				
				DatabaseConnection dc 	= DatabaseConnectionPool.getPool().getConnectionForDbFile( this.dbFileName );
				DatabaseCallParams params = this.customerObject.prepareForSave( this.customerIdForModify != null );
				if( this.customerIdForModify != null ) {
					params.addCondition( new ConditionEquals( "customerId", this.customerIdForModify ));
				}
				res = dc.execute( params );
			
				if( EnumDBResult.DB_SUCCESS == res ) {
				
					if( this.customerIdForModify != null ) {
						return true;
					} else {
						DataDictionaryUtitlities.storeNextCustomerId( 
								this.customerObject.customerId.replace( 
										DiagrepConfig.getConfig().get( DiagrepConfig.CUSTOMER_ID_PREFIX) + "-", "") );
						return this.customerObject.customerId;
					}
				} else {
					this.PrepareNewCustomerId( this.customerObject.customerId );
				}
			}
			
		} while( (res != EnumDBResult.DB_SUCCESS) && (++tryCount < 5) );
		
		return null;
	}
	
	
	private void PrepareNewCustomerId( String reference ) {
		String newCustId = DataDictionaryUtitlities.getNextCustomerNumber( reference ) ;
		this.customerObject.customerId	= 
				DiagrepConfig.getConfig().get( DiagrepConfig.CUSTOMER_ID_PREFIX) + "-" + newCustId;
		this.dbFileName 	= DatabaseUtility.getOrCreateDbFileNameForIdAndType( newCustId, 1 );
	}
}
