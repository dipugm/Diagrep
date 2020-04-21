

function showAnalyticsDialog() {
	
	szBody = "<table class='segment_table' style='margin-top: -10px;'>";
	szBody += "<tr>";
	szBody += "<td id='option_reference_analytic' class='segment_option_selected' align='center' onclick='javascript:selectAnalyticsTable(0)'>Reference</td>";
	szBody += "<td id='option_monthly_analytic' class='segment_option' align='center' onclick='javascript:selectAnalyticsTable(1)'>Monthly</td>";
	szBody += "<td id='option_daily_analytic' class='segment_option' align='center' onclick='javascript:selectAnalyticsTable(2)'>Daily</td>";
	szBody += "</tr>";
	szBody += "</table><br>";
	
	// Table for reference
	szBody += "<table width='100%' class='table-dialog-contents' id='table-reference-filter'>";
	szBody += "<tr><td colspan='2' style='padding-bottom:10px;'>Please select a Reference name from the combo and select the start and end dates.</td></tr>";
	szBody += "<tr>";
	szBody += "<td class='dialog_label' style='width:120px;'>Reference name:</td>";
	szBody += "<td><select style='border:1px solid lightgray; width:180px; height: 25px;' id='reference_filter_name'>";
	szBody += "<option></option>";
	for( var i=0; i < gArrayReferences.length; i++ ) {
		szBody += "<option>" + gArrayReferences[i] + "</option>\n";
	}
	szBody += "</select></td></tr>";
	szBody += "<tr>";
	szBody += "<td class='dialog_label'>Start Date:</td>";
	szBody += "<td><input type='text' class='text_box' style='width: 150px' id='reference_filter_start_date'>";
	szBody += "<span class='image_button' style='background-image: url(images/calendar.png)' "; 
	szBody += " onmouseover=\"fnInitCalendar(this, 'reference_filter_start_date', 'drag=false,close=true,title=Start Date')\"></span></td></tr>";
	szBody += "<tr>";
	szBody += "<td class='dialog_label'>End Date:</td>";
	szBody += "<td><input type='text' class='text_box' style='width: 150px' id='reference_filter_end_date'>";
	szBody += "<span class='image_button' style='background-image: url(images/calendar.png)' "; 
	szBody += " onmouseover=\"fnInitCalendar(this, 'reference_filter_end_date', 'drag=false,close=true,title=End Date')\"></span></td></tr>";
	szBody += "<tr><td colspan='2' align='center'><br><div onclick='javascript:onGenerateReferenceReport()' class='text_button' style='width:100px;'>Get Report</div></td>";
	szBody += "</tr></table>";
	
	// Table for Monthly
	szBody += "<table class='table-dialog-contents' id='table-monthly-analytics' style='display:none;'>";
	szBody += "<tr><td style='padding-bottom:10px;' colspan='2'>Please select the month and year to generate a day-wise report for that month.</td></tr>";
	szBody += "<tr><td colspan='2'><input type='checkbox' id='monthly-filter-include-bills'>&nbsp;&nbsp;Include Bill details</td></tr>";
	szBody += "<tr'>";
	szBody += "<td class='dialog_label'>Month: <select style='border:1px solid lightgray; width:50px; height: 24px;' id='monthly-filter-month-option'>";
	var months = ['Jan', 'Feb','Mar','Apr','May','Jun','Aug', 'Sep','Oct', 'Nov', 'Dec' ];
	for( var m=0; m < months.length; m++ ) {
		szBody += "<option>" + months[m] + "</option>";
	}
	szBody += "</select> </td>";
	szBody += "<td class='dialog_label'>Year : ";
	szBody += "<select style='border:1px solid lightgray; width:60px; height: 24px;'  id='monthly-filter-year-option'>";

	var year = new Date().getFullYear();
	for( var m=0; m < 10; m++ ) {
		szBody += "<option>" + (year - m).toString() + "</option>";
	}
	
	szBody += "</select>";
	szBody += "</td></tr>";
	szBody += "<tr><td colspan='2' align='center'><br><div onclick='javascript:onGenerateMonthlyReport()' class='text_button' style='width:100px;'>Get Report</div></td>";
	szBody += "</tr></table>";
	
	// table for daily report
	szBody += "<table width='100%' class='table-dialog-contents' id='table-daily-analytics' style='display:none;'>";
	szBody += "<tr><td colspan='2' style='padding-bottom:10px;'>Please select a day to generate a bill-wise report for that day. " +
			"Specify the date in YYYY-MM-DD format or select by clicking the calendar button</td></tr>";
	szBody += "<tr>";
	szBody += "<td class='dialog_label'>Selected Date:</td>";
	szBody += "<td><input type='text' class='text_box' style='width: 150px' id='daily_report_filter_start_date'>";
	szBody += "<span class='image_button' style='background-image: url(images/calendar.png)' "; 
	szBody += " onmouseover=\"fnInitCalendar(this, 'daily_report_filter_start_date', 'drag=false,close=true,title=Start Date')\"></span></td></tr>";
	szBody += "<tr><td colspan='2' align='center'><br><div onclick='javascript:onGenerateDailyReport()' class='text_button' style='width:100px;'>Get Report</div></td>";
	szBody += "</tr></table>";
	
    createPopupContainer(350, 220, 'Analytics', szBody, 'Cancel' );

}

function selectAnalyticsTable( tabIndex ) {
	var tableArray = ['table-reference-filter','table-monthly-analytics', 'table-daily-analytics'];
	var optionArray = [ 'option_reference_analytic', 'option_monthly_analytic', 'option_daily_analytic'];
	
	for( var i=0; i < tableArray.length; i++ ) {
		var tab = document.getElementById( tableArray[i] );
		tab.style.display = 'none';
		var opt = document.getElementById( optionArray[i] );
		opt.className = "segment_option";
		if( i == tabIndex ) {
			tab.style.display = 'table';
			opt.className = "segment_option_selected";
		}
	}
}

function onGenerateReferenceReport() {
	var combo = document.getElementById("reference_filter_name");
	var reference = combo.options[combo.selectedIndex].text;
	var startDate = document.getElementById('reference_filter_start_date').value;
	var endDate = document.getElementById('reference_filter_end_date').value;
	
	if( (reference == undefined || reference == "") || 
			(startDate == undefined || startDate == "") || 
			(endDate == undefined || endDate == "" ) ) {
		showWarning( "Please specify all the data to generate report", "Data missing");
		return;
	}
	
	var sd = new Date(startDate).getTime();
	var ed = new Date(endDate).getTime();
	
	var query = "reference=" + reference;
	query += "&startDate=" + sd.toString();
	query += "&endDate=" + ed.toString();
	
	showStatusDialog( "Please wait, this can take some time....");
	sendAsyncAjaxRequestToServer( '/GetReferenceReport', 'GET', query, "onReferenceReportReceivedFromServer" );
}

function onReferenceReportReceivedFromServer( resp ) {
	closeStatusDialog();
	
	try {
	    var jresp = JSON.parse( resp );
		if( jresp.status == 'failure' ) {
			showError( "Could not generate report. Please check your data and try again.", "Failed" );
		} 
	} catch( e ) {
		tab = window.open( null, "reference_report" );
		tab.document.write(resp);
		tab.document.close();
	}
}

function onGenerateDailyReport() {
	var refDate = document.getElementById('daily_report_filter_start_date').value;
	
	if( refDate== undefined || refDate == "") {
		showWarning( "Please select a day to generate the daily report", "Invalid input");
		return;
	}
	
	var rd = new Date(refDate).getTime();
	
	var query = "referenceDate=" + rd.toString();
	
	showStatusDialog( "Please wait, this can take some time....");
	sendAsyncAjaxRequestToServer( '/GetDailyReport', 'GET', query, "onDailyReportReceivedFromServer" );
	
}

function onDailyReportReceivedFromServer(resp) {
	closeStatusDialog();
	
	try {
	    var jresp = JSON.parse( resp );
		if( jresp.status == 'failure' ) {
			showError( "Could not generate report. Please select a different date and try again.", "Failed" );
		} 
	} catch( e ) {
		tab = window.open( null, "daily_report" );
		tab.document.write(resp);
		tab.document.close();
	}
}

function onGenerateMonthlyReport() {
	var refMonth = document.getElementById('monthly-filter-month-option');
	var month = refMonth.selectedIndex;
	var refYear = document.getElementById('monthly-filter-year-option');
	var year = refYear.options[ refYear.selectedIndex].text;
	var shouldIncludeBills = document.getElementById('monthly-filter-include-bills').checked;
	
	var query = "month=" + month.toString();
	query += "&year=" + year;
	query += "&shouldIncludeBills=" + shouldIncludeBills;
	
	showStatusDialog( "Please wait, this can take some time....");
	sendAsyncAjaxRequestToServer( '/GetMonthlyReport', 'GET', query, "onMonthlyReportReceivedFromServer" );
	
}

function onMonthlyReportReceivedFromServer(resp) {
	closeStatusDialog();
	
	try {
	    var jresp = JSON.parse( resp );
		if( jresp.status == 'failure' ) {
			showError( "Could not generate report. Please try again.", "Failed" );
		} 
	} catch( e ) {
		tab = window.open( null, "monthly_report" );
		tab.document.write(resp);
		tab.document.close();
	}
}

