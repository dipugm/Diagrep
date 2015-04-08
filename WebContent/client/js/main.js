var prevSelItem=0;
var prevSelectedTab=0;

var kTestType=0;
var kCategoryType=1;
var kCollectionType=2;
var kPackageType=3;

var dictEntities=0;

var subtotal=0.0;
var advance=0.0;
var grandtotal=0.0;
var itemsInBill = 0;
var itemid = 0;

var textToSearchAndScroll=0;

var gArrayBill = new Array();

function itemClicked( item ) {
    if( prevSelItem == 0 ) {
        prevSelItem = document.getElementById("firstItem");
    }
    
    if( item.id == "createBill" ) {
        item.src = "images/create_new_bill_active.png";
        document.getElementById("editReport").src = "images/edit_report_inactive.png";
        document.getElementById("table_createbill").style.display = "";
        document.getElementById("table_editreport").style.display = "none";
    } else {
        item.src = "images/edit_report_active.png";
        document.getElementById("createBill").src = "images/create_new_bill_inactive.png";
        document.getElementById("table_createbill").style.display = "none";
        document.getElementById("table_editreport").style.display = "";
    }
    
    prevSelItem = item;
}

function initializeDocument() {
	
	makeElementEditable( 'holder_for_test_description', kEditorMiniBar, "" );
	makeElementEditable( 'holder_for_report_recommendations', kEditorFullBar, "" );
	
    document.getElementById("table_createbill").style.display = "";
    document.getElementById("table_editreport").style.display = "none";

	prevSelectedTab = 'tab_choice_tests';
	// table = document.getElementById( 'table_tests' );
	// table.style.display = 'block';

	document.onmousemove = getMousePosition;
	
	dictEntities = new Array();
	
	// get all entities from the server.
	sendAsyncAjaxRequestToServer( "/Diagrep/GetTests", "GET", "", "fillTestsTable" );
	sendAsyncAjaxRequestToServer( "/Diagrep/GetCategories", "GET", "", "fillCategoriesTable" );
	sendAsyncAjaxRequestToServer( "/Diagrep/GetCollections", "GET", "", "fillCollectionsTable" );
	sendAsyncAjaxRequestToServer( "/Diagrep/GetPackages", "GET", "", "fillPackagesTable" );
	
	var billNoForReport = document.getElementById( 'report_bill_number' );
	
	billNoForReport.onkeyup = function(e) {
		if( e.which == 13 ) {
			getBillDetailsForReportEditing();
		}
	};
	
	var editTestValue = document.getElementById( 'text_test_value' );
	
	editTestValue.onkeyup = function(e) {
		if( e.which == 13 ) {
			changeTestValue();
		} else {
			onChangeInTestValue();
		}
	};
	
}

function onTabSelected( id ) {
	
	if( prevSelectedTab == id ) {
		return;
	}
	
	// Clear any text from the entity search bar.
	document.getElementById( 'text_search_and_scroll' ).value = '';
	document.getElementById( 'text_search_and_scroll' ).style.backgroundColor = '#FFFFFF';
	document.getElementById( 'search_box_for_entities' ).style.backgroundColor = '#FFFFFF';
	
	// Setting the focus to the entity search text box.
	document.getElementById( 'text_search_and_scroll' ).focus();
	
	tab = document.getElementById( id );
	tab.className = 'tab_choice_selected';
	
	tab = document.getElementById( prevSelectedTab );
	tab.className = 'tab_choice';
	
	var tableId = id.replace('tab_choice', 'entity_container');
	table = document.getElementById( tableId );
	table.style.display = 'block';
	
	tableId = prevSelectedTab.replace('tab_choice', 'entity_container');
	table = document.getElementById( tableId );
	table.style.display = 'none';
	
	prevSelectedTab = id;
	
}

function fillTestsTable( data ) {
	var obj = JSON.parse( data );
	gArrayEntities[kTestType] = obj.tests;
	
	var html = "";
	
	for(var i=0; i < gArrayEntities[kTestType].length; i++ ) {
		var test = gArrayEntities[kTestType][i];
		html += "<tr id='tr_tests_" + i + "'><td id='td_tests_" + i + "'";
		if( (i % 2) == 1 ) {
			html += " class='entity_oddrow' onclick='javascript:addToBill(" + kTestType + "," + i + ")'>";
		} else {
			html += " class='entity_evenrow' onclick='javascript:addToBill(" + kTestType + "," + i + ")'>";
		}
		
		html += test.name;
		if( test.method != '' ) {
			html += "<br><font size='2' color='darkblue'>Method : " + test.method + "</font>";
		}
		html += "</td></tr>";
	}
	
	var table = document.getElementById( 'table_tests' );
	table.innerHTML = html; 

	dictEntities["tab_choice_tests"] = gArrayEntities[kTestType];
}

function fillCategoriesTable( data ) {
	var obj = JSON.parse( data );
	gArrayEntities[kCategoryType] = obj.categories;
	
	var html = "";
	
	for(var i=0; i < gArrayEntities[kCategoryType].length; i++ ) {
		var cat = gArrayEntities[kCategoryType][i];
		html += "<tr id='tr_categories_" + i + "'><td id='td_categories_" + i + "'";
		if( (i % 2) == 1 ) {
			html += " class='entity_oddrow' onclick='javascript:addToBill(" + kCategoryType + "," + i + ")'>";
		} else {
			html += " class='entity_evenrow' onclick='javascript:addToBill(" + kCategoryType + "," + i + ")'>";
		}
		
		html += cat.name + "</td></tr>";
	}
	
	var table = document.getElementById( 'table_categories' );
	table.innerHTML = html; 
	
	dictEntities["tab_choice_categories"] = gArrayEntities[kCategoryType];
}

function fillCollectionsTable( data ) {
	var obj = JSON.parse( data );
	gArrayEntities[kCollectionType] = obj.collections;
	
	var html = "";
	
	for(var i=0; i < gArrayEntities[kCollectionType].length; i++ ) {
		var col = gArrayEntities[kCollectionType][i];
		html += "<tr id='tr_collections_" + i + "'><td id='td_collections_" + i + "'";
		if( (i % 2) == 1 ) {
			html += " class='entity_oddrow' onclick='javascript:addToBill(" + kCollectionType + "," + i + ")'>";
		} else {
			html += " class='entity_evenrow' onclick='javascript:addToBill(" + kCollectionType + "," + i + ")'>";
		}
		
		html += col.name + "</td></tr>";
	}
	
	var table = document.getElementById( 'table_collections' );
	table.innerHTML = html; 
	
	dictEntities["tab_choice_collections"] = gArrayEntities[kCollectionType];
}

function fillPackagesTable( data ) {
	var obj = JSON.parse( data );
	gArrayEntities[kPackageType] = obj.packages;
	
	var html = "";
	
	for(var i=0; i < gArrayEntities[kPackageType].length; i++ ) {
		var pkg = gArrayEntities[kPackageType][i];
		html += "<tr id='tr_packages_" + i + "'><td id='td_packages_" + i + "'";
		if( (i % 2) == 1 ) {
			html += " class='entity_oddrow' onclick='javascript:addToBill(" + kPackageType + "," + i + ")'>";
		} else {
			html += " class='entity_evenrow' onclick='javascript:addToBill(" + kPackageType + "," + i + ")'>";
		}
		
		html += pkg.name + "</td></tr>";
	}
	
	var table = document.getElementById( 'table_packages' );
	table.innerHTML = html; 
	
	dictEntities["tab_choice_packages"] = gArrayEntities[kPackageType];
}

function addToBill( type, index ) {
	itemsInBill = itemsInBill + 1;
	itemid = itemid + 1;
	szSlNo = itemsInBill;
	szName = "";
	szCost = "";
	
	var entity	= getMutableCopy( gArrayEntities[type][index] );

	szName = entity.name;
	szCost = entity.cost;
	
	var row_id = 'bill_row_' + itemsInBill;
	
	var table = document.getElementById('bill_contents');
	row = table.insertRow( itemsInBill );
	row.id = row_id;
	
	if( itemsInBill % 2 == 1 ) {
		row.className = 'clickable_row_odd';
	} else {
		row.className = 'clickable_row_even';
	}
	
	cell1 = row.insertCell( 0 );
	cell1.innerHTML = szSlNo;
	cell1.className = 'td_bill_sl_no';	
	cell2 = row.insertCell( 1 );
	cell2.innerHTML = szName;
	cell2.className = 'td_bill_particulars';
	cell3 = row.insertCell( 2 );
	cell3.innerHTML = szCost;
	cell3.className = 'td_bill_cost';
	
	row.onclick = function() {
		deleteFromBill( row_id, szCost );
	};
	
	// Update the total cost
	subtotal = subtotal + parseFloat( szCost );
	updateCosts();
	
//	gArrayBill[row_id] = entity;
//	gArrayBill.push( row_id );
	
	gArrayBill.push( entity );
}

function deleteFromBill( row_id, szCost ) {
	
	// var row_id_str = "bill_row_" + row_id;
	// var row = document.getElementById( row_id );
	var table = document.getElementById( 'bill_contents' );
	var itemIndex=0;
	for( ; itemIndex < table.rows.length; itemIndex++ ) {
		row = table.rows[itemIndex];
		if( row.id == row_id ) {
			table.deleteRow( itemIndex );
			break;
		}
	}
	itemIndex 	= itemIndex - 1;
	itemsInBill = itemsInBill - 1;

	var entity = gArrayBill[ itemIndex ];

	subtotal = subtotal - entity.cost;
	updateCosts();

	gArrayBill.splice( itemIndex, 1 );
	
	// Updating the Sl no since we have deleted one row.
	for( var i=itemIndex; i < table.rows.length; i++ ) {
		row = table.rows[i];
		cells = row.cells[0];
		cells.innerHTML = i;
		
		if( i % 2 == 1 ) {
			row.className = 'clickable_row_odd';
		} else {
			row.className = 'clickable_row_even';
		}
	}
	
	
}

function updateCosts() {
	// tax = (subtotal * 12)/100;
	balance = subtotal - advance;
	
	// var tx = document.getElementById('bill_tax');
	// tx.innerHTML = tax;
	
	var tc = document.getElementById('bill_sub_total');
	tc.innerHTML = subtotal;
	
	var bl = document.getElementById('bill_balance');
	bl.innerHTML = balance;
	
}

function onAdvance() {
	var tb = document.getElementById('text_box_advance');
	
	advance = parseFloat(tb.value);
	
	if( false == isNaN(advance) ) {
		updateCosts();
	} else {
		showWarning( "Not a number. Only Numbers and decimal point allowed.", "Invalid Input");
		tb.value = '';
	}
}

function clearBill() {
	var table = document.getElementById( 'bill_contents' );
	var count = table.rows.length;
	for( var i=1; i < count; i++ ) {
		table.deleteRow(1);
	}
	
	balance 	= 0.00;
	grandtotal 	= 0.00;
	subtotal 	= 0.00;
    advance 	= 0.00; 
	
	updateCosts();
	
	itemsInBill = 0;
	itemid 		= 0;
	
	gArrayBill = [];
	
	document.getElementById('text_customer_id').value = '';
	document.getElementById('text_customer_name').value = '';
	document.getElementById('text_report_date').value = '';
	document.getElementById('text_referred_by').value = '';
    document.getElementById('text_box_advance').value = '';
	
}

function generateBill() {
	
	var szCustomerId = document.getElementById('text_customer_id').value.toUpperCase();
	var szCustomerName = document.getElementById('text_customer_name').value;
	var szReferredBy = document.getElementById('text_referred_by').value;
	
	if( szCustomerId == '' ) {
		showWarning( "Please provide Customer Id. If you do not know the Customer Id, please use the  Customer Name text box to search.", "Invalid Input");
		return;
	}
	
	if( gArrayBill.length == 0 ) {
		showError( "Please select atleast one or Test, Category, Collection or Package to create the bill.", "Invalid Selection");
		return;
	}
	
    if( szReferredBy == '' ) {
        szReferredBy = "Self";
    }
	
	var advancePaid = document.getElementById('text_box_advance').value;
	if( document.getElementById('text_box_advance').value != '' && parseFloat(advancePaid) != NaN ) {
		if( advancePaid.indexOf(".") < 0 ) {
			advancePaid += ".00";
		}
	} else {
		advancePaid = "0.0";
	}
	
	/*
	Creating the bill as a JSON string.
	*/
	szBill = "{\"customer_id\":\"" + szCustomerId + "\",";
	szBill += "\"referred_by\":\"" + szReferredBy + "\",";
	
	var dat = document.getElementById('text_report_date').value;
	if( dat == '' ) {
		dat = new Date();
	} else {
		dat = new Date( dat );
	}
	
	szBill += "\"report_date\":" + dat.getTime() + ",";
	
	szBill += "\"advance_paid\":" + advancePaid + ",";
	szBill += "\"entities\":[";
	
	for( var i=0; i < gArrayBill.length; i++ ) {
		
		if( i > 0 ) {
			szBill += ",";
		}
		
		var entity = gArrayBill[ i ];
		
		szBill += "{\"id\":" + entity.id + ",";
		szBill += "\"type\":" + entity.type + ",";
		szBill += "\"cost\":" + entity.cost + "}";
	}
	szBill += "]}";
	
    showStatusDialog( "Please wait..." );
    
	sendAsyncAjaxRequestToServer('/Diagrep/GenerateBill',
                                 'POST',
                                 szBill,
                                 "onBillCreateResponseFromServer");
	
	
}

function onBillCreateResponseFromServer( resp ) {
	var jresp = JSON.parse( resp );
	if( jresp.status == "success" ) {
		var billNum = jresp.billNumber;
		url = '/Diagrep/SearchBill?billNumber=' + billNum;
		window.open( url, '_newtab');
	} else {
		showError( jresp.info, "Create Failed" );
	}
	
	clearBill();
    
    closeStatusDialog();
}

function getBillDetailsAsHtml() {
	
	var billNum = document.getElementById('bill_number_for_bill_in_dialog').value;
	if( billNum == '' ) {
		showError("Please specify a Bill Number to get the Bill Details.", "Invalid Input");
		return;
	}
	
	query = 'billNumber=' + billNum;
    
    showStatusDialog( "Please wait..." );
	
	sendAsyncAjaxRequestToServer( '/Diagrep/SearchBill',
                                 'GET',
                                 query,
                                 "onGetBillResponseFromServer");

}

function onGetBillResponseFromServer( resp ) {
	
	try {
		var jresp = JSON.parse( resp );
		if( jresp.status == "failure" ) {
			showError( jresp.info, "Invalid Bill Number" );
		}
	} catch( e ) {
		tab = window.open( );
		tab.document.write(resp);
		tab.document.close();
	}
	
	onDone();
    
    closeStatusDialog();
}

function displayTextEditor( divElement, title, callbackFn ) {
	if( false == checkIfConditionsFineForReportEditing(divElement) ) {
		return;
	}
	showLargeEditor( divElement, title, callbackFn );
}

function clickInSearchEntityAndScroll() {
	document.getElementById( 'text_search_and_scroll' ).focus();
}

/*
 This is the function called when user types into the edit box in any of 
 entity tabs. Depending on the text typed by the user, the respective
 tab scrolls to the matching row.
*/
function searchEntityAndScroll() {
	var textBox = document.getElementById( 'text_search_and_scroll' );
	if( textToSearchAndScroll == textBox.value ) {
		return;
	}
	
	textToSearchAndScroll = textBox.value.toUpperCase();
	if( textToSearchAndScroll == '' ) {
		textBox.style.backgroundColor = '#FFFFFF';
		document.getElementById( 'search_box_for_entities' ).style.backgroundColor = '#FFFFFF';
		
		return;
	}
	
	// Get a hold of the tab.
	var divTab = prevSelectedTab.replace( 'tab_choice', 'entity_container' );
	var trSel=0;
	
	var arr = dictEntities[prevSelectedTab];
	
	var scrollPos = -1;
	for( var i=0; i < arr.length; i++ ) {
		var entity = arr[i];
		
		if( entity.name.toUpperCase().indexOf( textToSearchAndScroll) == 0 ) {
			trSel = prevSelectedTab.replace( 'tab_choice', 'tr');
			trSel += "_" + i;
			scrollPos = document.getElementById( trSel ).offsetTop;
			break;
		}
	}
	
	var bkColor = "#fbe3da";
	if( scrollPos != -1 ) {
		document.getElementById( divTab ).scrollTop = scrollPos;
		
		bkColor = "#e8fbda";
	} 
	
	textBox.style.backgroundColor = bkColor;
	document.getElementById( 'search_box_for_entities' ).style.backgroundColor = bkColor;
	
	
}
