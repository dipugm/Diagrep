
/* Functions to display a busy indicator with some message. */
function showStatusDialog( messageText ) {
	
    // Create a div element that covers the whole screen to
    // make the popup look like a modal dialog.
    var main_container = document.createElement('DIV');
    main_container.className="statusdialog_background";
    main_container.id = "statusdialog_background";
    
    document.body.appendChild( main_container );
    
	var left = (window.innerWidth - 100) / 2;
	var top = (window.innerHeight - 100) / 2;
    
    var popup_container = document.createElement('DIV');
    popup_container.className = "statusdialog_container";
    popup_container.id = "statusdialog_container";
	popup_container.style.left = left + 'px';
	popup_container.style.top = top + 'px';
    
    document.body.appendChild( popup_container );
    
	/*
     Title Bar
     */
    var busyInd = document.createElement('DIV');
    busyInd.className = "statusdialog_busy_indicator";
    busyInd.id = "statusdialog_busy_indicator";
    busyInd.innerHTML = "<p align='center'><img src='../common/images/busy_indicator_small.gif' width='32px' height='32px'></p>";
    popup_container.appendChild( busyInd );
    
    var msgDiv = document.createElement('DIV');
    msgDiv.className = "statusdialog_message";
    msgDiv.innerHTML = messageText;
    popup_container.appendChild( msgDiv );
    
}

function closeStatusDialog() {
    var bkg = document.getElementById( "statusdialog_background" );
    var cont = document.getElementById( "statusdialog_container" );
    
    document.body.removeChild( cont );
    document.body.removeChild( bkg );
}