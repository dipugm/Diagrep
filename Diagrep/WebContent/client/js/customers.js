
function lookupCustomerByName( ) {
	szSearchString = document.getElementById('text_customer_name').value;
	
	if( szSearchString == '') {
		showError("Enter part or the complete name of the customer to search.", "Invalid Input");
	} else {
		var query = 'customerName=' + szSearchString + '&shouldSearchOldDb=true';
		sendAsyncAjaxRequestToServer( '/SearchCustomers', 'GET', query, "onSearchCustomerResponseForAnchoring");
		
	}
}

function onSearchCustomerResponseForAnchoring( resp ) {
	var results = JSON.parse( resp );
	var anchor = document.getElementById('search_button');
	
	anchorCustomerSearchResults( results, anchor );
	
}

function searchCustomers( szSearchString, isId, shouldSearchOldDb ) {

	var query = "";
	if( isId == 1 ) {
		query += "customerId=";
	} else {
		query += "customerName=";
	}
	query += szSearchString + '&shouldSearchOldDb=' + shouldSearchOldDb;
	sendAsyncAjaxRequestToServer( '/SearchCustomers', 'GET', query, "onSearchCustomersResponseReceived" );
}

function onSearchCustomersResponseReceived( resp ) {
	var results = JSON.parse( resp );
	showCustomersSearchResults( results, 1 );
}