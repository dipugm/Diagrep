
var gTestValueDetails = [];
var gCurrentEditedTest = 0;
var gQueriedBillNum = "";

function checkIfConditionsFineForReportEditing(divElement) {
	if( document.getElementById('table_report_tests').rows.length == 0 ) {
		showError( "Please specify a bill number and get its contents. No Active bill specified.", "No Active Bill");
		return false;
	}
	else if( (divElement == 'holder_for_test_description') && (gCurrentEditedTest == 0)) {
		showError( "Please select a test to add description", "No Test Selected");
		return false;
	}
	
	return true;
}

function onGenerateReportFromDialog() {
	var billNum = document.getElementById('bill_number_for_report_in_dialog').value;
	if( billNum == '' ) {
		showWarning("Please specify a Bill Number to get the Report.", "Invalid Input");
		return;
	}
	
	getReportDetailsAsHtml( billNum );
	
	onDone();
}

function getReportDetailsAsHtml( billNum ) {
	
    gQueriedBillNum = billNum;
	bn = 'billNumber=' + billNum;
	bn += "&asHtml=1";
    
    showStatusDialog( "Please wait..." );
    
	sendAsyncAjaxRequestToServer( '/Diagrep/GetReportDetails',
                                 'GET',
                                 bn,
                                 "onReportGenerationResponseFromServer");
	
}

function onReportGenerationResponseFromServer( resp ) {
	
	closeStatusDialog();
	
	try {
	    var jresp = JSON.parse( resp );
		if( jresp.status == 'failure' ) {
			showError( "Bill number you specified does not exist. Please specify a valid bill number.", "Invalid Bill Number" );
		} 
	} catch( e ) {
		tab = window.open( );
		tab.document.write(resp);
		tab.document.close();
	}
    
}

function getBillDetailsForReportEditing() {
	
	var billNum = document.getElementById('report_bill_number').value;
	if( billNum == '' ) {
		showWarning("Please specify a Bill Number to get the Report details.", "Invalid Input");
		return;
	}
	
    gQueriedBillNum = billNum;
	bn = 'billNumber=' + billNum;
	bn += "&asHtml=0";

	showStatusDialog( "Please wait..." );
	
	sendAsyncAjaxRequestToServer( '/Diagrep/GetReportDetails',
                                 'GET',
                                 bn,
                                 "onGetBillForEditingResponseFromServer");
	
}

function onGetBillForEditingResponseFromServer( resp ) {
    var jresp = JSON.parse( resp );
	if( jresp.result == 'failure' ) {
		showError( "Bill number you specified does not exist. Please specify a valid bill number.", "Invalid Bill Number" );
	} else {
        
		clearReportEditForm();
        
		var tests = jresp.entities;
		for( var i=0; i < tests.length; i++ ) {
			var test = tests[i];
			
			test.description = decodeURLEncodedString( test.description );
			test.testedValue = decodeURLEncodedString( test.testedValue );
            
			addToReportTests( test, i );
		}
        
		document.getElementById('holder_for_report_recommendations').innerHTML = 
			decodeURLEncodedString(jresp.recommendations);
	}
    
    closeStatusDialog();

}

function clearReportEditForm() {
	var table = document.getElementById('table_report_tests');
	count = table.rows.length;
	for( var i=1; i < count; i++ ) {
		table.deleteRow(1);
	}
	
	var textB = document.getElementById('text_test_value');
	textB.value = "";
	
	var span = document.getElementById( 'span_normal_value' );
	span.innerHTML = "";
	
	var spanName = document.getElementById( 'span_test_name' );
	spanName.innerHTML = "";
	
	var checkbox = document.getElementById( 'checkbox_is_highlighted' );
	checkbox.checked = 0;
	
}

function addToReportTests( test, index ) {
	var table = document.getElementById('table_report_tests');
	
	var row = table.insertRow( index );
	if( index % 2 == 1 ) {
		row.className = 'clickable_row_odd';
	} else {
		row.className = 'clickable_row_even';
	}
		
	var cell1 = row.insertCell(0);
	cell1.innerHTML = (index+1).toString();
	cell1.className = "bill_contents_slno";
	var cell2 = row.insertCell(1);
	cell2.className = "bill_contents_name";
	cell2.innerHTML = test.name;
	var cell3 = row.insertCell(2);
	cell3.className = "bill_contents_value";
	cell3.innerHTML = test.testedValue;
	cell3.id = 'td_report_test_value_for_' + test.id;
	
	var cell4 = row.insertCell(3);
	cell4.className = "bill_contents_range";
	if( test.normal_value != "" ) {
		cell4.innerHTML = test.normalValue + " " + test.units;
	}
	
	row.onclick = function() {
		testSelectedForEditing( test.id.toString() );
	};
	
	var test_id = 'test_id_' + test.id;
	gTestValueDetails[ test_id ] = test;
	gTestValueDetails.push( test_id );
	
}

function testSelectedForEditing( test_id ) {
	var textB = document.getElementById('text_test_value');
	
	var oTest = gTestValueDetails['test_id_' + test_id];
	gCurrentEditedTest = oTest;
	
	textB.value = oTest.testedValue;
	
	var inpDesc = document.getElementById('holder_for_test_description');
	inpDesc.innerHTML = oTest.description;
	
	var span = document.getElementById( 'span_normal_value' );
	if( oTest.normal_value != '' ) {
		span.innerHTML = oTest.normalValue + " " + oTest.units;
	} else {
		span.innerHTML = "";
	}
	
	var spanName = document.getElementById( 'span_test_name' );
	spanName.innerHTML = oTest.name;
	
	var checkbox = document.getElementById( 'checkbox_is_highlighted' );
	checkbox.checked = oTest.isHighlighted;
	
	textB.focus();
	textB.select();
}

function onChangeInTestValue( obj ) {
	
	var textB = document.getElementById('text_test_value');
	
	var range = gCurrentEditedTest.normalValue;
	var value = parseFloat( textB.value );
	
	if( range.indexOf(',') < 0 ) {
		var values = range.split( "-" );
		var lowerlimit = parseFloat(values[0]);
		var upperlimit = parseFloat(values[1]);
		
		var checkbox = document.getElementById( 'checkbox_is_highlighted' );
		if( value < lowerlimit || value > upperlimit ) {
			checkbox.checked = true;
			gCurrentEditedTest.isHighlighted = 1;
		} else {
			checkbox.checked = false;
			gCurrentEditedTest.isHighlighted = 0;
		}
	}
	
}

/*
This function is called when the user presses the Change button or hits a
enter in the test value edit box (configured in initializeDocument function).
*/
function changeTestValue() {
	if( gCurrentEditedTest == 0 ) {
		showError( "Please select a test to edit its value.", "No Test Selected");
		return;
	}
	
	var textB = document.getElementById('text_test_value');
	
	gCurrentEditedTest.testedValue = textB.value;
	
	var td = document.getElementById( 'td_report_test_value_for_' + gCurrentEditedTest.id );
	td.innerHTML = gCurrentEditedTest.testedValue;
	
	var inpDesc = document.getElementById('holder_for_test_description');
	gCurrentEditedTest.description = inpDesc.innerHTML;
	
	var checkbox = document.getElementById( 'checkbox_is_highlighted' );
	gCurrentEditedTest.is_highlighted = checkbox.checked;
	
	var i = 0;
	for( i=0; i < gTestValueDetails.length; i++ ) {
		test_id = gTestValueDetails[ i ];
		test = gTestValueDetails[ test_id ];
		
		if( test.id == gCurrentEditedTest.id ) {
			i++;
			break;
		}
	}
	
	if( i == gTestValueDetails.length ) {
		i = 0;
	}
	var nextTest = gTestValueDetails[ gTestValueDetails[ i ] ];

	onUpdateReport( true );
	
	testSelectedForEditing( nextTest.id );
	
	textB.focus();
	textB.select();
	
}

function onDescriptionChanged() {

	if( gCurrentEditedTest != 0 ) {
		var textarea = document.getElementById( 'holder_for_test_description' );
		gCurrentEditedTest.description = textarea.innerHTML;
		
		onUpdateReport( true );
	}
}

function onRecommendationsChanged () {
    onUpdateReport( false );
}

function onUpdateReport( updateTestsAlso ) {
	
	if( gCurrentEditedTest == 0 ) {
		return;
	}
	
	var billNum = document.getElementById( 'report_bill_number' ).value;
    
	var recom = document.getElementById('holder_for_report_recommendations').innerHTML;
	
	var jsonBody = "{\"bill_number\":\"" + billNum + "\",";
	jsonBody += "\"fullUpdate\":" + (updateTestsAlso | 0 ) + ",";
	jsonBody += "\"recommendations\":\"" + encodeURIComponent(recom.trim()) + "\",";
    
    if( updateTestsAlso ) {
        jsonBody += "\"test_results\":{";
        jsonBody += "\"id\":" + gCurrentEditedTest.id + ",";
        jsonBody += "\"is_highlighted\":" + gCurrentEditedTest.isHighlighted + ",";
        jsonBody += "\"value\":\"" + encodeURIComponent(gCurrentEditedTest.testedValue) + "\",";
        jsonBody += "\"description\":\"" + encodeURIComponent(gCurrentEditedTest.description) + "\"}";
    }
    jsonBody += "}";

	sendAsyncAjaxRequestToServer( '/Diagrep/UpdateReportDetails', 'POST', jsonBody, "onUpdateReportResponseFromServer" );
    
}

function onUpdateReportResponseFromServer( resp ) {
	var jresp = JSON.parse( resp );
	if( jresp.status == 'failure' ) {
		showError( jresp.info, "Editing value Failed" );
	}
}

function onGenerateReportFromMainView() {
	var billNum = document.getElementById('report_bill_number').value;
	if( billNum == '' ) {
		showWarning( "Please specify a Bill Number to first get the report contents.", "No Bill Specified");
		return;
	}
	
	getReportDetailsAsHtml( billNum );
}



