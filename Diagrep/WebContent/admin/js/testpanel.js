
var gTestEdited = 0;

function show_tests() {
	
	// clear the right panel.
	clearRightPanel();
	
	var central_panel = document.getElementById( 'center_panel_layout' );
	
	var body = "";
	body += "<div class='page_header' style='width: 100%; background-color:#336699;'>";
	body += "<div class='page_header_title' style='width: 80%;'>Available Tests</div>";
	body += "<div id='create_new' onclick='javascript:showViewForTestDetails(0)' class='main_option_button' style='width:17%;line-height:40px;' >Create New</div>";
	body += "</div>";
	
	body += "<table class='entity_table_header' border='0' cellspacing='0'>";
	
	body += "<tr>";
	body += "<th width='10%'>Sl No.</th>";
	body += "<th width='50%'>Name</th>";
	body += "<th width='10%'>Unit</th>";
	body += "<th width='20%'>Range</th>";
	body += "<th width='10%' style='padding-right:30px;'>Cost</th>";
	body += "</tr></table>";
	
	body += "<div class='page_contents'>";
	
	body += "<table class='entity_table_contents' border='0' cellspacing='0' id='table_tests' >";
	
	for( var i=0; i < gArrayEntities[kTestType].length; i++ ) {
		var test = gArrayEntities[kTestType][i];
		
		body += "<tr";
		body += " class='odd_row'";
		body += " onclick=\"javascript:rowClickedForTests(" + i + ")\">";
		body += "	<td width='10%' align='center'>" + (i+1) + "</td>";
		body += "	<td width='50%'>" + decodeURLEncodedString( test.name ) + "</td>";
		body += "	<td width='10%'>" + decodeURLEncodedString( test.unit ) + "</td>";
		body += "	<td width='20%'>" + decodeURLEncodedString( test.normalValue ) + "</td>";
		body += "	<td width='10%' style='text-align:right;padding-right:30px;'>" + test.cost + "</td>";
		body += "</tr>";
	}
	
	body += "</table>";
	body += "</div>";
	
	central_panel.innerHTML = body;
}

function showViewForTestDetails( test ) {
	
	var name="";
	var method="";
	var normalvalue = "";
	var cost="";
	var units="";
	
	var fModify = 0;
	if( test != 0 ) {
		fModify = 1;
		
		name = decodeURLEncodedString( test.name );
		method = decodeURLEncodedString( test.method );
		normalvalue = decodeURLEncodedString( test.normalValue );
		cost = test.cost;
		units = decodeURLEncodedString( test.unit );
		
		gTestEdited = getMutableCopy( test );
		gArrayEntityEdited[ kTestType ] 	= gTestEdited;
	} else {
		gTestEdited = {
			type:0,
			id:-1,
			name:"",
			method:"",
			normalValue:"",
			cost:"",
			unit:"",
		};
	}
	
	var body = "";
	body += "<table class='input_form_table'>";
	body += "<tr>";
	body += "<td width='30%'>Name :</td>";
	
	if( fModify ) {
		body += "	<td><b>" + name + "</b></td>";
	} else {
		body += "	<td><input type='text' class='entity_text_box' id='text_test_name' style='width:300px'></td>";
	}
	body += "</tr>";
	
	body += "<tr>";
	body += "	<td>Method :</td>";
	body += "	<td><input type='text' class='entity_text_box' id='text_test_method' style='width:300px' value='" + method + "'></td>";
	body += "</tr>";
	
	body += "<tr>";
	body += "	<td>Reference Range:</td>";
	body += "	<td><div class='styled_text_area' style='width: 300px; height: 100px;' id='text_test_normalvalue'></div></td>";
	body += "</tr>";
	body += "<tr>";
	body += "<td>Units :</td>";
	
	if( fModify ) {
		body += "<td><b>" + units + "</b></td>";
	} else {
		body += "<td><input type='text' class='entity_text_box' id='text_test_units' value='" + units + "'></td>";
	} 
	body += "</tr>";
	
	body += "<tr>";
	body += "	<td>Cost (Rs):</td>";
	body += "	<td><input type='text' onkeypress='return isNumberKey(event)' class='entity_text_box' id='text_test_cost' value='" + cost + "'></td>";
	body += "</tr>";
	
	body += "</table>";
	
	
	body += "<table class='input_form_table'>";
	if( fModify ) {
		body += "<tr>";
		body += "<td width='40%'>";
		body += "<div class='text_button' style='width: 150px; text-align: center; color: white; background-color:#3399FF;' ";
		body += "onclick='javascript:modifyTestOnServer()'>Modify</div>";
		body += "</td>";
		body += "<td><div class='description_text'>Modify with new values</div></td>";
		body += "</tr>";
		body += "<tr>";
		body += "<td width='40%'>";
		body += "<div class='text_button' style='width: 150px;' id='delete_entity' ";
		body += "onclick='javascript:deleteTestOnServer()'>Delete</div>";
		body += "</td>";
		body += "<td><div class='description_text'>Delete this Test.</div></td>";
		body += "<tr>";
	} else {
		body += "<tr>";
		body += "<td width='40%'>";
		body += "<div class='text_button' style='width: 150px; text-align: center; color: white; background-color:#3399FF;' ";
		body += "onclick='javascript:createTestOnServer()'>Create</div>";
		body += "</td>";
		body += "<td><div class='description_text'>Create a new Test.</div></td>";
		body += "<tr>";
		
	}
	body += "</table>";
	
	var rightPanel = document.getElementById( 'right_panel_layout' );
	rightPanel.innerHTML = body;
	
	makeElementEditable('text_test_normalvalue', kEditorNoBar, normalvalue );
	
}

function rowClickedForTests( rowNum ) {
	
	var test = gArrayEntities[kTestType][ rowNum];
	showViewForTestDetails(test);

}

function createTestOnServer() {
	
	gTestEdited.name = encodeWithCustomUrlEncoding( document.getElementById('text_test_name').value );
	gTestEdited.method = encodeWithCustomUrlEncoding( document.getElementById('text_test_method').value );
	gTestEdited.normalValue = encodeWithCustomUrlEncoding( getTextFromEditor( 'text_test_normalvalue' ) );
	gTestEdited.cost = document.getElementById('text_test_cost').value;
	gTestEdited.unit = encodeWithCustomUrlEncoding( document.getElementById('text_test_units').value );
	
	if( dataValidityCheck( gTestEdited.name, gTestEdited.cost ) == false) {
		return;
	}
    
	var queryparams = "type=" + kTestType;
	queryparams += "&name=" + gTestEdited.name;
	queryparams += "&cost=" + gTestEdited.cost;
	if( gTestEdited.method != "" ) {
		queryparams += "&method=" + gTestEdited.method;
	}
	if( gTestEdited.normalValue != "" ){
		queryparams += "&normalvalue=" + gTestEdited.normalValue;
	}
	if( gTestEdited.unit != "" ){
		queryparams += "&unit=" + gTestEdited.unit;
	}
	
	displayBusy( "Create a new test", "Please wait..." );
	
    sendAsyncAjaxRequestToServer('/CreateEntity',
                                 'POST',
                                 queryparams,
                                 "onCreateTestResponseFromServer" );
	
}

function onCreateTestResponseFromServer( resp ) {
    obj = JSON.parse( resp );
	
	if( obj.status == "success" ) {
		
		displaySuccess( "Create a new test", "Successfully created the test." );
		
		gTestEdited.id = parseInt( obj.testId );
		
		// Add the new test to global array and re-render.
		gArrayEntities[ kTestType ].push( gTestEdited );
		gTestEdited = 0;
		
		show_tests();
	} else {
		displayError( "Create a new test", "Failed to create the test. - " + obj.info );
	}
}

function modifyTestOnServer() {
	gTestEdited.method = encodeWithCustomUrlEncoding( document.getElementById('text_test_method').value );
	gTestEdited.normalValue = encodeWithCustomUrlEncoding( getTextFromEditor( 'text_test_normalvalue' ) );
	gTestEdited.cost = document.getElementById('text_test_cost').value;
	
	if( dataValidityCheck("dummy", gTestEdited.cost) == false) {
		return;
	}

	var queryparams = "id=" + gTestEdited.id;
	queryparams += "&type=" + kTestType;
	queryparams += "&method=" + gTestEdited.method;
	queryparams += "&normalvalue=" + gTestEdited.normalValue;
	queryparams += "&cost=" + gTestEdited.cost;
	
	displayBusy( "Modify an existing test", "Please wait..." );
	sendAsyncAjaxRequestToServer( '/ModifyEntity',
                                 'POST',
                                 queryparams,
                                 "onModifyTestResponseFromServer" );
                                 
		
}

function onModifyTestResponseFromServer( resp ) {
    obj = JSON.parse( resp );
	
	if( obj.status == "success" ) {
		displaySuccess( "Modify an existing test", "Successfully modified the test values!" );
        
		// Replace the test in the global array and re-render.
		replaceEntityInGlobalArray( gTestEdited );
		gTestEdited = 0;
		
		show_tests();
	} else {
		displayError( "Modify an existing test", "Failed to modify the test! - " + obj.info );
	}

}

function deleteTestOnServer() {
	showQuestion( "Are you sure you want to delete this test?",
                 "Confirm Delete",
                 onDeleteTestConfirmation);
}

function onDeleteTestConfirmation( option ) {
    if( option == "Yes" ) {
        displayBusy( "Deleting Test", "Please wait..." );

        sendAsyncAjaxRequestToServer('/RemoveEntity',
                                     'POST',
                                     'type=0&id=' + gTestEdited.id,
                                     "onDeleteTestResponseFromServer" );
    }
}

function onDeleteTestResponseFromServer( resp ) {
    obj = JSON.parse( resp );
    
    if( obj.status == "success" ) {
    	displaySuccess( "Deleting Test", "Successfully deleted the test permanently" );
        
    	// remove the test from the global array and re-render.
    	deleteEntityInGlobalArray( gTestEdited );
    	gTestEdited = 0;
        show_tests();
    } else {
        displayError( "Deleting Test", "Failed to delete the test - " + obj.info );

    }
    
}
