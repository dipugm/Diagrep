
var gTestValueDetails = [];
var gCurrentEditedTest = 0;
var gQueriedBillNum = "";
var gMethodToExecuteAfterUpdate=0;

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
		tab = window.open( null, "report");
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
	if( jresp.status == 'failure' ) {
		showError( "Bill number you specified does not exist. Please specify a valid bill number.", "Invalid Bill Number" );
	} else {
        
		clearReportEditForm();
        
		var tests = jresp.entities;
		for( var i=0; i < tests.length; i++ ) {
			var test = tests[i];
			
			test.name 		= decodeURLEncodedString( test.name );
			test.description = decodeURLEncodedString( test.description );
			test.testedValue = decodeURLEncodedString( test.testedValue );
			test.normalValue = decodeURLEncodedString( test.normalValue );
			test.unit		= decodeURLEncodedString( test.unit );
            
			addToReportTests( test, i );
		}
        
		setTextInEditor('holder_for_report_recommendations', decodeURLEncodedString( jresp.recommendations ) );
	}
    
    closeStatusDialog();

}

function clearReportEditForm() {
	
	gTestValueDetails = [];
	
	var table = document.getElementById('table_report_tests');
	count = table.rows.length;
	for( var i=0; i < count; i++ ) {
		table.deleteRow(0);
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
		cell4.innerHTML = test.normalValue + " " + test.unit;
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
	
	setTextInEditor('holder_for_test_description', oTest.description);
	
	var span = document.getElementById( 'span_normal_value' );
	if( oTest.normal_value != '' ) {
		span.innerHTML = oTest.normalValue + " " + oTest.unit;
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
	
	if( value != 'NaN' && range.indexOf(',') < 0 ) {
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
	
	gCurrentEditedTest.description = getTextFromEditor('holder_for_test_description');
	
	var checkbox = document.getElementById( 'checkbox_is_highlighted' );
	gCurrentEditedTest.isHighlighted = checkbox.checked ? 1 : 0;
	
	var i = 0;
	for( i=0; i < gTestValueDetails.length; i++ ) {
		test_id = gTestValueDetails[ i ];
		test = gTestValueDetails[ test_id ];
		
		if( test.id == gCurrentEditedTest.id ) {
			i++;
			break;
		}
	}
	
	onUpdateReport( true );
	
	if( i < gTestValueDetails.length ) {
		var nextTest = gTestValueDetails[ gTestValueDetails[ i ] ];

		testSelectedForEditing( nextTest.id );
		
		textB.focus();
		textB.select();
	}
	
	
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
	
	if( updateTestsAlso && (gCurrentEditedTest == 0) ) {
		return;
	}
	
	var billNum = document.getElementById( 'report_bill_number' ).value;
    
	var recom = getTextFromEditor('holder_for_report_recommendations');
	
	var jsonBody = "{\"bill_number\":\"" + billNum + "\",";
	jsonBody += "\"fullUpdate\":" + (updateTestsAlso | 0 ) + ",";
	jsonBody += "\"recommendations\":\"" + encodeWithCustomUrlEncoding(recom.trim()) + "\",";
    
    if( updateTestsAlso ) {
        jsonBody += "\"test_results\":{";
        jsonBody += "\"id\":" + gCurrentEditedTest.id + ",";
        jsonBody += "\"is_highlighted\":" + gCurrentEditedTest.isHighlighted + ",";
        jsonBody += "\"value\":\"" + encodeWithCustomUrlEncoding(gCurrentEditedTest.testedValue) + "\",";
        jsonBody += "\"description\":\"" + encodeWithCustomUrlEncoding(gCurrentEditedTest.description) + "\"}";
    }
    jsonBody += "}";

	sendAsyncAjaxRequestToServer( '/Diagrep/UpdateReportDetails', 'POST', jsonBody, "onUpdateReportResponseFromServer" );
    
}

function onUpdateReportResponseFromServer( resp ) {
	var jresp = JSON.parse( resp );
	if( jresp.status == 'failure' ) {
		showError( jresp.info, "Editing value Failed" );
	} else {
		if( gMethodToExecuteAfterUpdate != 0 ) {
			gMethodToExecuteAfterUpdate();
		}
	}
}

function generateReportPressed() {
	
	var billNum = document.getElementById('report_bill_number').value;
	if( billNum == '' ) {
		showWarning( "Please specify a Bill Number to first get the report contents.", "No Bill Specified");
		return;
	}
	
	gMethodToExecuteAfterUpdate = onGenerateReportFromMainView;
	onUpdateReport( false );
}

function onGenerateReportFromMainView() {
	
	gMethodToExecuteAfterUpdate = 0;
	
	var billNum = document.getElementById('report_bill_number').value;	
	getReportDetailsAsHtml( billNum );
}




