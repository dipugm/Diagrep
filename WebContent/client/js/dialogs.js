
var html_container = 0;
var gCustomerSearchResults = 0;
var gArrayRecommendationTemplates=0;
var gSelectedRecommendationTemplate=0;
var gRecommendationToDelete = 0;

function showCreateCustomers( anchor ) {

	
	szBody = "<table class='table-dialog-contents' >";
	szBody += "<tr style='padding-top:10px; padding-bottom: 10px;'>";
	szBody += "<td align='right' class='td-dialog-content-option-name'>Name :</td>";
	szBody += "<td style='padding-left: 10px;'>";
	szBody += "<input type='text' id='textName' class='text_box' style='width:250px;text-align:left;'></td>";
	szBody += "</tr>";

	szBody += "<tr>";
	szBody += "<td align='right' class='td-dialog-content-option-name'>Age :</td>";
	szBody += "<td style='padding-left: 10px;'>";
	szBody += "<input class='text_box' onkeypress='return isNumberKey(event)' style='width:40px;text-align:left;' type='text' id='textAgeYears'> Yrs.  ";
	szBody += "<span style='padding-left: 20px;'></span>";
	szBody += "<input class='text_box' style='width:40px;text-align:left;' onkeypress='return isNumberKey(event)' type='text' id='textAgeMonths'> Mnths.";
	szBody += "</td>";
	szBody += "</tr>";
	
	szBody += "<tr>";
	szBody += "<td align='right' class='td-dialog-content-option-name'>Gender :</td>";
	szBody += "<td style='padding-left: 10px;'>";
	szBody += "<input type='radio' name='radio_sex' value='1' id='radioSexMale'> Male<span style='padding-left: 20px;'></span>";
	szBody += "<input type='radio' name='radio_sex' value='0' id='radioSexFemale'> Female";
	szBody += "</td>";
	szBody += "</tr>";
	
	szBody += "<tr>";
	szBody += "<td colspan='2' align='center'> <div onclick='javascript:onCreateCustomer()' class='text_button' style='width:100px;padding-top:10px;' >Create</div>";
	szBody += "</td>";
	szBody += "</tr>";
	
	createPopupContainer(400,200, 'Create Customers', szBody, 'Cancel', anchor );
	
	var firstChild = document.getElementById( 'textName' );
	firstChild.focus();

}

function showSearchCustomers( anchor ) {
	
	szBody = "<table class='table-dialog-content'>";
	
	szBody += "	<tr>";
	szBody += "		<td colspan='2' class='radioHeader'><input type='radio' name='search_choice' id='radio_choice_by_name' checked='checked' onclick='javascript:searchChoiceChange();' /> By Customer Name</td>";
	szBody += "	</tr>";
	szBody += "	<tr>";
	szBody += "		<td class='labelforoptioninput'>Enter a part or whole of the Customer Name:</td>";
	szBody += "		<td><input type='text' class='text_box' style='width: 180px; margin-right:10px;' id='text_search_name' onkeyup='javascript:onKeyUpInSearchBox(event);'></td>";
	szBody += "	</tr>";
	
	szBody += "	<tr>";
	szBody += "		<td colspan='2' class='radioHeader'><input type='radio' name='search_choice' id='radio_choice_by_id' onclick='javascript:searchChoiceChange();'/> By Customer Id</td>";
	szBody += "	</tr>";
	szBody += "	<tr>";
	szBody += "		<td class='labelforoptioninput'>Enter a part or whole of the Customer ID:</td>";
	szBody += "		<td><input type='text' class='text_box' style='width: 180px; margin-right:10px;' id='text_search_id' disabled='true' onkeyup='javascript:onKeyUpInSearchBox(event);'></td>";
	szBody += "	</tr>";
	
	szBody += "	<tr>";
	szBody += "		<td class='labelforoptioninput' colspan='2' style='font-weight:bold;'><br>";
	szBody += "<input type='checkbox' name='search_old_db' id='checkbox_search_old_db' checked=true> Search old database also.</td>";
	szBody += "		<td></td>";
	szBody += "	</tr>";
	
	szBody += "	<tr>";
	szBody += "		<td colspan='2' align='center' class='tdbutton'><div onclick='javascript:onSearchCustomers()' class='text_button' style='width:100px;'>Search</div></td>";
	szBody += "	</tr>";
	szBody += "</table>";
	
	szBody += "<br><table align='center' id='table_busy_indicator' style='display: none;'>";
	szBody += "	<tr>";
	szBody += "		<td class='td_busyindicator'><img src='images/busy-indicator.gif'></td>";
	szBody += "</tr><tr>";
	szBody += "		<td class='td_busymessage'>Searching the database for the parameters you have provided. Please wait while the results are formatted.<br>Do not press the search button again.";
	szBody += "		</td>";
	szBody += "	</tr>";
	szBody += "<table>";
	
	createPopupContainer(500, 500, 'Search Customers', szBody, 'Cancel', anchor);
}

function onKeyUpInSearchBox( evt ) {
	if( evt.which == 13 ) {
		onSearchCustomers();
	}
}

function showCustomersSearchResults( results, addToPopup ) {
	gCustomerSearchResults = new Array();
	gCustomerSearchResults = ["Existing", "Old"];
	gCustomerSearchResults["Existing"] = results.customers;
	gCustomerSearchResults["Old"] = results.customers_legacy;
	
	totalFound = results.totalCount;
	
	var tab = document.getElementById('table_busy_indicator');
	tab.style.display = 'None';
	document.getElementById('text_search_name').value = "";
	document.getElementById('text_search_id').value = "";
	
	szBody = "";
	
	szBody += "<ul class='tabLayout' style='padding-left:20px'>";
	szBody += "<li class='tabItem' style='padding-bottom:5px;'><a href='javascript:void(0)' class='tab_choice_selected' id='tab_choice_Existing' onclick=\"javascript:toggleSearchDisplay('Existing')\">Current ( " + results.customers.length + " )</a></li>";
	szBody += "<li class='tabItem' style='padding-bottom:5px;'><a href='javascript:void(0)' class='tab_choice' id='tab_choice_Old' onclick=\"javascript:toggleSearchDisplay('Old')\">Old Records ( " + results.customers_legacy.length +" )</a></li>";
	szBody += "</ul>";

	szBody += "<div style='width: 500px;height: 400px;'>";
	szBody += "<div class='search_results_container' id='search_results_container' >";

	
	for( var j=0; j < gCustomerSearchResults.length; j++ ) {
		
		var key = gCustomerSearchResults[j];
		
		szBody += "<div class='search_results' id='search_results_"+ key + "'";
		if( j > 0 ) {
			szBody += " style='display:none'";
		}
		szBody += ">";
		szBody +="<table width='100%'; height:400px; border:0px; border-spacing:0px; border-collapse:collapse;' cellspacing=0>";

		var results = gCustomerSearchResults[key];
		
		if( results.length == 0 ) {
			szBody += "<tr><td class='no-data-message-cell'>No results for this group.</td></tr>";
		} else {
			for( var i=0; i < results.length; i++ ) {
				var cus = results[i];
				
				var age = cus.age + " M";
				if( cus.age > 12 ) {
		            yrs = Math.floor( cus.age / 12 );
					age = yrs + " Y";
				}
				szBody += "	<tr class='search_result_row' onclick='javascript:getCustomerReports(\"" + cus.id + "\")' style='padding:3px;'>";
				szBody += "		<td width='70%' class='search_name'>" + cus.name;
				szBody += "		<a class='search_id'> [" + cus.id + "]</td>";
				szBody += "		<td class='search_contact'>Reg. on : " + cus.dateOfCreation + "<br>";
				szBody += "			Age : " + age;
				szBody += "		</td>";
				szBody += "	</tr>";
			}
		}
		szBody += "</table></div>";
	}
	
	szBody += "</div></div>";
	
	if( addToPopup ) {
		
		// Since the radio button state is not saved, we are having to reset
		// the selection to the default state.
		var choiceName = document.getElementById('radio_choice_by_name');
		choiceName.checked = true;
		searchChoiceChange();
		
		pushToPopupContainer( szBody, 'Search' );
	}
}

function toggleSearchDisplay( tabToShow ) {
	var tab = document.getElementById( "tab_choice_Existing" );
	tab.className = "tab_choice";
	var tab = document.getElementById( "tab_choice_Old" );
	tab.className = "tab_choice";

	var tab = document.getElementById( "tab_choice_" + tabToShow );
	tab.className = "tab_choice_selected";

	var div = document.getElementById( "search_results_Existing" );
	div.style.display = "none";
	var div = document.getElementById( "search_results_Old" );
	div.style.display = "none";
	
	var divToShow = document.getElementById( "search_results_" + tabToShow );
	divToShow.style.display = "block";
}

function getCustomerReports( customerId ) {
	var query = "customerId=" + customerId;
	
	sendAsyncAjaxRequestToServer( "/Diagrep/GetReportsForCustomer", "GET", query, "onGetReportsResponseFromServer" );
}

function onGetReportsResponseFromServer( resp ) {

	var jResp = JSON.parse( resp );
	
	if( jResp.status == "success" ) {
		var bills = jResp.bills;
		
		szBody = "<p align='center'>";
		szBody += "<div class='search_results_header'> Displaying " + bills.length + " reports for this customer</div>";
		szBody += "<div class='customer_report_list_table_container'>";
		szBody += "<table class='customer_report_list_table'>";
		for( var i=0; i < bills.length; i++ ) {
			var bill = bills[i];
			szBody += "<tr onclick=\"javascript:getReportDetailsAsHtml('" +  bill.billNumber + "')\">";
			szBody += "<td width='50%' class='customer_report_list_billnumber'>" + bill.billNumber + "</td>";
			cd = new Date( bill.billDate );
			szDate = cd.getDate() + "-" + (cd.getMonth() + 1) + "-" + cd.getFullYear();
			szBody += "<td class='customer_report_list_dates'><span><b>Bill Date :</b> " + szDate + "</span><br>";
			
			cd = new Date(bill.reportDate);
			szDate = cd.getDate() + "-" + (cd.getMonth() + 1) + "-" + cd.getFullYear();
			szBody += "<span><b>Report Date : </b>" + szDate + "</span></td>";
			szBody += "</tr>";
		}
		szBody += "</table></div></p>";
		pushToPopupContainer( szBody, 'Search Results' );
	} else {
		showError( jResp.info, "Error");
	}
}

function showSearchBill( anchor ) {
	szBody = "<table class='table-dialog-contents' width='100%'>";
	szBody += "<tr><td colspan='2' style='padding-bottom:10px;'>Please enter the bill number and click Search to get bill contents. If found, the bill details will show up in a separate tab.</td></tr>";
	szBody += "<tr>";
	szBody += "<td class='dialog_label'>Bill Number :</td>";
	szBody += "<td><input type='text' class='text_box' style='width: 150px' id='bill_number_for_bill_in_dialog'></td></tr>";
	szBody += "<tr><td colspan='2' align='center'><br><div onclick='javascript:getBillDetailsAsHtml()' class='text_button' style='width:100px;' >Get Bill</div></td>";
	szBody += "</tr></table>";
	
    createPopupContainer(300, 200, 'Search Bill', szBody, 'Cancel', anchor );

	var firstChild = document.getElementById( 'bill_number_for_bill_in_dialog' );
	firstChild.focus();
	
	firstChild.onkeyup = function(e) {
		if( e.which == 13 ) {
			getBillDetailsAsHtml();
		}
	};

}

function showSearchReport( anchor ) {
	szBody = "<table width='100%' class='table-dialog-contents'>";
	szBody += "<tr><td colspan='2' style='padding-bottom:10px;'>Please enter the bill number and click Search to get report. If found, the report will show up in a separate tab.</td></tr>";
	szBody += "<tr>";
	szBody += "<td class='dialog_label'>Bill Number :</td>";
	szBody += "<td><input type='text' class='text_box' style='width: 150px' id='bill_number_for_report_in_dialog'></td></tr>";
	szBody += "<tr><td colspan='2' align='center'><br><div onclick='javascript:onGenerateReportFromDialog()' class='text_button' style='width:100px;'>Get Report</div></td>";
	szBody += "</tr></table>";
	
    createPopupContainer(300, 200, 'Search Report', szBody, 'Cancel', anchor );

	var firstChild = document.getElementById( 'bill_number_for_report_in_dialog' );
	firstChild.focus();
	
	firstChild.onkeyup = function(e) {
		if( e.which == 13 ) {
			onGenerateReportFromDialog();
		}
	};
	
}

function showHtmlData( szData ) {

    // Create a div element that covers the whole screen to
    // make the popup look like a modal dialog.
    html_container = document.createElement('DIV');
    html_container.className='html_container';
    html_container.style.position = 'absolute';
    html_container.id = 'html_container';
    
    document.body.appendChild( html_container );

	var contents = document.createElement( 'DIV' );
	contents.className='html_contents';
	contents.id = 'html_contents';

	html_container.appendChild( contents );
	
	var closeButton = document.createElement( 'span' );
	closeButton.className='image_button';
	closeButton.id='dialog_close_button';
	closeButton.style.width='24px';
	closeButton.style.height='24px';
	closeButton.style.left = '14%';
	closeButton.style.top = '4%';
	closeButton.style.zIndex = 100000005;
	closeButton.style.backgroundImage = "url('images/close_icon.png')";
	
	closeButton.onclick = function() {
		document.body.removeChild( document.getElementById('html_container') );
	};
	
	html_container.appendChild( closeButton );

	contents.innerHTML = "<div class='html_dialog_title_bar'><span onclick='javascript:printHtmlContents()' class='text_button'>Print</span></div>";
	contents.innerHTML += "<div><div id='content_to_print'>" + szData +"</div></div>";
}

function printHtmlContents() {
	$('#content_to_print').print();
}

function anchorCustomerSearchResults( results, anchor ) {
	
	var customers = results.customers.concat( results.customers_legacy );
	
	if( customers.length == 0 ) {
		showWarning("No Customers matching this name", "No Match");
		return;
	}
	
	var search_results_container = document.createElement( 'DIV' );
	search_results_container.className='popup_search_results_container';
	search_results_container.id = 'popup_search_results_container';
	
	document.body.appendChild( search_results_container );
	
	var search_contents = document.createElement('DIV');
	search_contents.className = 'popup_customer_search_contents';
	search_contents.style.left = (anchor.offsetLeft + (anchor.offsetWidth / 2)) + 'px';
	search_contents.style.top = (anchor.offsetTop + (anchor.offsetHeight / 2)) + 'px';
	
	search_results_container.appendChild( search_contents );
	
	szResults = "";
	szResults += "<div class='close_icon_button'";
	szResults += " 	onclick='javascript:closeCustomerListPopup()'></div>";
	szResults += "<div class='popup_customer_lookup_title'>" + customers.length + " Customer(s) Found</div>";
	szResults += "<div class='popup_customer_search_table_holder'>";
	
	szResults += "<table width='100%'; border:0px; border-spacing:0px; border-collapse:collapse;' cellspacing=0>";
	
	for( var i=0; i < customers.length; i++ ) {
		var cus = customers[i];
		
		var age = cus.age + " M";
		if( cus.age > 12 ) {
            yrs = Math.floor( cus.age / 12 );
			age = yrs + " Y";
		}
		szResults += "	<tr class='search_result_row' ";
		szResults += "onclick=" + "'javascript:customerSelectedFromLookup(\"" + cus.name + "\",\"" + cus.id + "\")' ";
		szResults += "style='padding:3px;'>";
		szResults += "		<td width='60%' class='search_name'>" + cus.name;
		szResults += "		<a class='search_id'> [" + cus.id + "]</td>";
		szResults += "		<td class='search_contact'>Reg. on : " + cus.dateOfCreation + "<br>";
		szResults += "			Age : " + age;
		szResults += "		</td>";
		szResults += "	</tr>";
	}
	
	szResults += "</table></div>";
	
	search_contents.innerHTML = szResults;
	
}

function customerSelectedFromLookup(name, id) {
	var textName = document.getElementById( 'text_customer_name');
	textName.value = name;
	
	var textId = document.getElementById( 'text_customer_id' );
	textId.value = id;
	
	closeCustomerListPopup();
}

/* 
 * Recommendation templates related functions
 */

function displaySaveDataDialog( anchor ) {
	
	if( false == checkIfConditionsFineForReportEditing() ) {
		return;
	}
	
	data = document.getElementById( 'holder_for_report_recommendations' ).innerHTML;
	
	if( data == "" ) {
		showWarning( "Please enter something into the Recommendations text area to save it as template.", "Invalid Input");
		return;
	}
	
	szBody = "<br>";
	szBody += "<p width='90%' class='dialog_caption'>Please enter a name to save the recommendation as a template. You can load it at a later point of time.</p>";
	szBody += "<table width='100%'><tr>";
	szBody += "<td class='dialog_label'>Name :</td>";
	szBody += "<td><input type='text' class='text_box' style='width: 150px' id='name_for_template_data'></td></tr>";
	szBody += "<tr><td colspan='2' align='center'><br><div style='width:100px;' onclick='javascript:onSaveAsTemplate()' class='text_button'>Save</div></td>";
	szBody += "</tr></table>";
	
    createPopupContainer(300, 230, 'Save As Template', szBody, 'Cancel', undefined );

	var firstChild = document.getElementById( 'name_for_template_data' );
	firstChild.focus();
	
	firstChild.onkeyup = function(e) {
		if( e.which == 13 ) {
			onSaveAsTemplate();
		}
	};
}

function displayLoadDataDialog() {
	if( false == checkIfConditionsFineForReportEditing() ) {
		return;
	}
	
	sendAsyncAjaxRequestToServer( '/Diagrep/LoadRecommendationTemplates', 'GET', '', "onRecommendationTemplatesAvailable" );
}

function onRecommendationTemplatesAvailable( resp ) {
	var dict = JSON.parse( resp );
	
	var recoTempls = dict['recommendations'];
	if( recoTempls ) {
		gArrayRecommendationTemplates = recoTempls;
		
		if( gArrayRecommendationTemplates.length > 0 ) {
			
			szBody = "<table style='width: 600px; height: 400px; border-collapse: collapse;'>";
			szBody += "<tr>";
			szBody += "	<td width='40%' class='templates_list_area' valign='top'>";
			szBody += "		<p class='templates_list_preview_label'>Select a saved template from the list to preview</p>";
			szBody += "		<div class='templates_list_table_holder'>";
			szBody += "			<table class='templates_list_table' id='templates_list_table'>";
						
			var flag=0;
			for( var i=0; i < gArrayRecommendationTemplates.length; i++ ) {
				sd = gArrayRecommendationTemplates[i];
				sd.content = decodeURIComponent( sd.content );
				sd.content = sd.content.split("+").join(" ");
				
				gArrayRecommendationTemplates[ sd.name ] = sd;
				
				szBody += "<tr id='reco_row_" + sd['name'] + "' ";
				if( flag == 0 ) {
					szBody += " class='clickable_row_odd'";
				} else {
					szBody += " class='clickable_row_even'";
				}
				flag = ! flag;
				
				szBody += "onclick='javascript:onSavedTemplateClicked(\"" + sd['name']  + "\")' "; 
				szBody += "><td width='90%'style='padding:3px;'>" + sd['name'] + "</td>";
				szBody += "<td class='td_remove' onclick='javascript:deleteRecommendation(\"" + sd['name'] + "\")'></td>";
				szBody += "</tr>";
			}
			
			szBody += "			</table>";

			szBody += "		</div> <br>";
			szBody += "		<div class='text_button' style='width: 100px;margin:auto;' onclick='javascript:onSelectSavedData()'>Select</div>";
			szBody += "	</td>";
			szBody += "	<td width='60%' height='100%' class='templates_list_preview_area'>";
			szBody += "		<span class='templates_list_preview_label'>Preview:</span>";
			szBody += "		<span class='templates_list_preview_label' id='template_selected' style='color: darkblue;'>None</span>";
			szBody += "		<div class='templates_list_preview_pane' id='templates_list_preview_pane'></div>";
			szBody += "	</td>";
			szBody += "</tr>";
			szBody += "</table>";

			
			createPopupContainer(600, 400, 'Load a Template', szBody, 'Cancel', undefined );
		}
	}	
}

function onSavedTemplateClicked( name ) {
	var dt = gArrayRecommendationTemplates[ name ];
	var pane = document.getElementById('templates_list_preview_pane');
	pane.innerHTML = dt.content;
	
	document.getElementById('template_selected').innerHTML = dt.name;
	
	gSelectedRecommendationTemplate = dt;
}

function onSelectSavedData( ) {
	
	if( gSelectedRecommendationTemplate == 0 ) {
		showError( "Please select a Template to use its contents.", "Invalid Selection" );
		return;
	}
	
	setTextInEditor( 'holder_for_test_description' , gSelectedRecommendationTemplate.content );
	onDone();
}

function onSaveAsTemplate() {
	
	var name = document.getElementById( 'name_for_template_data' ).value;
	var data = getTextFromEditor( 'holder_for_test_description' );
	data 	 = encodeURIComponent( data );
	
	var query = "name=" + name + "&content=" + data;
	sendAsyncAjaxRequestToServer( '/Diagrep/StoreRecommendationTemplate', 'POST', query, "onSaveTemplateResponse" );
	
}

function onSaveTemplateResponse( resp ) {
	var dictResp = JSON.parse( resp );
	
	if( dictResp.status == 'failure' ) {
		showError( dictResp.info, "Save Failed" );
	} else {
		showInfo( "Recommendation text saved successfully as a template.", "Save As Template" );
		onDone();
	}
}

function deleteRecommendation( recoName ) {
	gRecommendationToDelete = recoName;
	showQuestion( "Are sure you want to delete this recommendation template?", "Confirm", onDeleteRecommendationConfirm);
}

function onDeleteRecommendationConfirm( option ) {
	
	if( option == 'Yes' ) {
		var query = "name=" + gRecommendationToDelete;
		sendAsyncAjaxRequestToServer( '/Diagrep/RemoveRecommendationTemplate', 'POST', query, "onDeleteRecommendationResponse" );
	}
	
}

function onDeleteRecommendationResponse( resp ) {
	var result = JSON.parse( resp );
	
	if( result.status == "success" ) {
		var tab = document.getElementById( "templates_list_table" );
		var row = document.getElementById( "reco_row_" + gRecommendationToDelete );
		
		tab.deleteRow( row.rowIndex );
		
		pane = document.getElementById('templates_list_preview_pane');
		pane.innerHTML = "";
		
		document.getElementById('template_selected').innerHTML = "";
		
		gSelectedRecommendationTemplate = 0;

		
	} else {
		showError( "Could not delete the recommendation. " + result.info, "Failed");
	}
	
	gRecommendationToDelete = 0;
}

/*
Button handlers in different popups.
*/

function closeCustomerListPopup() {
	document.body.removeChild( document.getElementById('popup_search_results_container') );
}

function onCreateCustomer() {
	
	szName = document.getElementById('textName').value;
	szAge = (document.getElementById('textAgeYears').value * 12);
    var months = document.getElementById('textAgeMonths').value;
    if( months != "" ) {
        szAge += parseInt(months);
    }
	szSex = -1;
	
	if( document.getElementById('radioSexFemale').checked ) {
		szSex = 0;
	} else if( document.getElementById('radioSexMale').checked ) {
		szSex = 1;
	}
	
	if( szName == '' || szAge == 0 || szSex == -1 ) {
		showError( "Data not complete. Please specify all the fields and then press Create.", "Invalid Input");
		return;
	}
	
	query = "name=" + szName;
	query += "&age=" + szAge;
	query += "&sex=" + szSex;
	
	sendAsyncAjaxRequestToServer( '/Diagrep/CreateCustomer', 'POST', query, "onCreateCustomerResponseFromServer" );
}

function onCreateCustomerResponseFromServer( resp ) {
	respJson = JSON.parse( resp );
	
	if( respJson.status == "success" ) {
		showInfo( "Customer creation successful.\nPlease note down the customer Id for this user.\n\n" + respJson.customerId, "Create Success");
	} else {
		showError( "Customer creation failure.\nExtra info : " + respJson.extrainfo, "Create Failed");
	}
}

function onSearchCustomers() {
	
	var choiceId = document.getElementById('radio_choice_by_id');
	var choiceName = document.getElementById('radio_choice_by_name');
	
	var tb = 0;
	var isId = 1;
	
	if( choiceId.checked == true ) {
		tb = document.getElementById( 'text_search_id' );
	} else if( choiceName.checked == true ) {
		tb = document.getElementById( 'text_search_name' );
		isId = 0;
	} else {
		showWarning( "Please specify the search type by choosing one of the radio buttons.", "Invalid Input");
		return;
	}
	
	if( tb.value == '' ) {
		showWarning("Please specify a search term or Id to search.", "Invalid Input");
		return;
	}
	
	var tab = document.getElementById('table_busy_indicator');
	tab.style.display = 'block';
	
	var searchOldDb = document.getElementById('checkbox_search_old_db').checked;

	window.setTimeout(
			function(){ 
				searchCustomers( tb.value.toUpperCase(), isId, searchOldDb ); 
				}, 
				10);
	
}

function searchChoiceChange() {
	var choiceId = document.getElementById('radio_choice_by_id');
	
	if( choiceId.checked == true ) {
		document.getElementById( 'text_search_id' ).disabled = false;
		document.getElementById( 'text_search_id' ).focus();
		document.getElementById( 'text_search_name' ).disabled = true;
		document.getElementById( 'text_search_name' ).value = "";
	} else {
		document.getElementById( 'text_search_id' ).disabled = true;
		document.getElementById( 'text_search_id' ).value = "";
		document.getElementById( 'text_search_name' ).disabled = false;
		document.getElementById( 'text_search_name' ).focus();
	}
}
