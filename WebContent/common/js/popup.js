
var main_container = false;
var popup_container = false;
var content_height=0;
var content_width=0;

var left_button=0;
var right_button=0;
var nav_title=0;
var navBarContent=0;
var view_stack = Array();
var back_button_stack = Array();


function createPopupContainer(width,height, szTitle, szBody, szDoneButtonText, anchor ) {
	
	popup_width = width;
	popup_height = height;
	
    // Create a div element that covers the whole screen to
    // make the popup look like a modal dialog.
    main_container = document.createElement('DIV');
    main_container.className='popup_main_container';
    main_container.style.position = 'absolute';
    main_container.id = 'popup_main_container';
    
    document.body.appendChild( main_container );
    
	var left = (window.innerWidth - width) / 2;
	var top = (window.innerHeight - height) / 2;
	
	if( anchor != undefined ) {
		left = anchor.offsetLeft - ((width - anchor.offsetWidth) / 2);
		top = anchor.offsetTop + anchor.offsetHeight - 10;
	} 

    popup_container = document.createElement('DIV');
    popup_container.className = 'popup_container';
    popup_container.id = 'popup_container';
    popup_container.style.width = width + 'px';
    popup_container.style.height = height + 'px';
	popup_container.style.left = left + 'px';
	popup_container.style.top = top + 'px';
    
    main_container.appendChild( popup_container );

   
    var table = document.createElement( "TABLE" );
    table.width = width;
    table.className = "popup-content-table";
    
    var rowNum = 0;
    if( anchor != undefined ) {
        // First row
        var arrowRow = table.insertRow(0);
        var arrowCell = arrowRow.insertCell(0);
        arrowCell.className = "popup_top_arrow";
        arrowCell.colSpan = 3;
        arrowCell.innerHTML = "<div class='popup_top_arrow'></div>";
    	
    	rowNum = rowNum + 1;
    }
    
    // nav bar row.
    var navBarRow = table.insertRow(rowNum);
    navBarRow.className = "nav-bar";
    
    var backBut = navBarRow.insertCell(0);
    backBut.className = "nav-bar-left-button-hidden";
    backBut.id 			= "popup_nav_bar_left_button";
    backBut.style.width = "25%";
    backBut.innerHTML = "";
    
    var title = navBarRow.insertCell(1);
    title.style.width = "50%";
    title.className = "nav-bar-title";
    title.innerHTML = szTitle;
    
    var rightBut = navBarRow.insertCell(2);
    rightBut.className = "nav-bar-right-button";
    rightBut.style.width = "25%";
    rightBut.innerHTML = "Cancel";
    rightBut.onclick = function( event ) {
		onDone();
	};
    
    rowNum = rowNum + 1;
    // Content row
    var contentRow = table.insertRow( rowNum );
    contentRow.id	= "id-popup-container-row";
    
    var contentCell = contentRow.insertCell(0);
    contentCell.colSpan = "3";
    contentCell.className = "popup-container-cell";
    contentCell.innerHTML = szBody;
    
    popup_container.appendChild( table );
    
}

function pushToPopupContainer( szBody, szBackButtonText ) {
	
	var backbtn = document.getElementById( 'popup_nav_bar_left_button' );
	
	var prevBackButtonText = backbtn.innerHTML;
		
	backbtn.className = "nav-bar-left-button";
	backbtn.innerHTML = szBackButtonText;
	backbtn.onclick = function( event ) {
		onBack();
	};

	var contentRow = document.getElementById("id-popup-container-row");
	var prevCellContent = contentRow.cells[0].innerHTML;
	
	view_stack.push( prevCellContent );
	back_button_stack.push( prevBackButtonText );
	
	var contentCell = contentRow.cells[0];    
    contentCell.innerHTML = szBody;

}

function onDone() {
	document.body.removeChild( main_container );
	
	while( back_button_stack.length > 0 ) {
		back_button_stack.pop();
	}
	while( view_stack.length > 0 ) {
		view_stack.pop();
	}
	
}

function onBack() {
	prevCellContents 	= view_stack.pop();
	prevBackButtonText 	= back_button_stack.pop();
	
	var contentRow = document.getElementById("id-popup-container-row");
	contentRow.cells[0].innerHTML = prevCellContents;
	
	// Modify the back button text.
	var backbtn = document.getElementById( 'popup_nav_bar_left_button' );
	
	if( prevBackButtonText == '' ) {
		backbtn.className = "nav-bar-left-button-hidden";	// shown by default.
		backbtn.onclick = 0;
	}
	backbtn.innerHTML = prevBackButtonText;
	
	
}
