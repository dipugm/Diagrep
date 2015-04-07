package diagrepServer.database.actions.customer;

import java.util.Date;

import diagrepServer.Utils.DataDictionaryUtitlities;
import diagrepServer.Utils.DatabaseUtility;
import diagrepServer.Utils.DiagrepConfig;
import diagrepServer.database.actions.BaseAction;
import diagrepServer.database.core.DatabaseCallParams;
import diagrepServer.database.core.DatabaseCallParams.ConditionEquals;
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
			String newCustId = DataDictionaryUtitlities.getNextCustomerNumber();
			this.customerObject.customerId	= 
					DiagrepConfig.getConfig().get( DiagrepConfig.CUSTOMER_ID_PREFIX) + "-" + newCustId;
			this.dbFileName 	= DatabaseUtility.getOrCreateDbFileNameForIdAndType( newCustId, 1 );
		} else {
			this.customerIdForModify	= custId;
			this.dbFileName		= DatabaseUtility.getDbFileNameForIdAndType( custId, 1);
		}
	}
	
	public Object doAction() {
		
		if( this.dbFileName != null && ! this.dbFileName.isEmpty() ) {
		
			DatabaseConnection dc 	= DatabaseConnectionPool.getPool().getConnectionForDbFile( this.dbFileName );
			DatabaseCallParams params = this.customerObject.prepareForSave( this.customerIdForModify != null );
			if( this.customerIdForModify != null ) {
				params.addCondition( new ConditionEquals( "customerId", this.customerIdForModify ));
			}
			
			if( dc.execute( params ) ) {
				
				if( this.customerIdForModify != null ) {
					return true;
				} else {
					DataDictionaryUtitlities.storeNextCustomerId( this.customerObject.customerId );
					return this.customerObject.customerId;
				}
				
			}
		}
		
		return null;
	}
}
