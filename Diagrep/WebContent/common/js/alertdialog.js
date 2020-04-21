
var gCallbackFun=0;

function showError( message, title) {
    showAlert( message, title, "error", ["OK"], "darkred" );
}

function showInfo( message, title ) {
    showAlert( message, title, "information", ["OK"], "darkblue");
}

function showQuestion( message, title, callback ) {
    gCallbackFun = callback;
    showAlert( message, title, "question", ["Yes", "No", "Cancel"], "darkorange" );
}

function showWarning( message, title) {
    showAlert( message, title, "warning", ["OK"], "darkorange"  );
}

function showAlert( messageText, title, iconType, buttons, textColor ) {
	
    popup_width = 400;
    
    var lines = (messageText.length / 60) + 1;
	popup_height = 100 + (20 * lines );
    var leftEdge = 100;
    var topEdge = 100;
	
    // Create a div element that covers the whole screen to
    // make the popup look like a modal dialog.
    var main_container = document.createElement('DIV');
    main_container.className="alertdialog_background";
    main_container.id = "alertdialog_background";
    main_container.style.left = 0 + "px";
    main_container.style.top = 0 + "px";
    main_container.style.height = "100%";
    main_container.style.width = "100%";
    
    document.body.appendChild( main_container );

	leftEdge = (window.innerWidth - popup_width) / 2;
	topEdge = (window.innerHeight - popup_height) / 2;

    var popup_container = document.createElement('DIV');
    popup_container.className = "alertdialog_container";
    popup_container.id = "alertdialog_container";
    popup_container.style.width = popup_width + 'px';
    popup_container.style.height = popup_height + 'px';
	popup_container.style.left = leftEdge + 'px';
	popup_container.style.top = topEdge + 'px';
    
    document.body.appendChild( popup_container );
    
    var body = "<table align='center' class='alertdialog_table'>";
    body += "<tr><td class='alertdialog_title_bar' align='center' ";
    body += "style='color:" + textColor + ";' ";
    body += "colspan=" + buttons.length + ">";
    body += title + "</td></tr>";
    
    body += "<tr><td class='alertdialog_message' align='center' colspan=" + buttons.length + ">";
    body += messageText;
    body += "</td></tr>";
    body += "<tr class='alertdialog_separator'><td colspan=" + buttons.length + "></td></tr>";
    body += "<tr>";
    for( var i=0; i < buttons.length; i++ ) {
            
        var btnName = buttons[i];
        var functionName = "on" + btnName;
        body += "<td class='alertdialog_text_button' align='center' ";
        body += "onClick='javascript:" + functionName + "()' ";
        body += "style='color:" + textColor + ";'";
        body += ">" + btnName + "</td>";
    }
    body += "</tr>";
    body += "</table>";
 
    popup_container.innerHTML = body;

}

function destroyAlert() {
    var win = document.getElementById("alertdialog_container");
    document.body.removeChild( win );
    
    win = document.getElementById("alertdialog_background");
    document.body.removeChild( win );
}

function onOK() {
    destroyAlert();
}

function onCancel() {
    destroyAlert();
}

function onYes() {
    if( gCallbackFun != 0 ) {
        gCallbackFun('Yes');
    }
    destroyAlert();
}
function onNo() {
    if( gCallbackFun != 0 ) {
        gCallbackFun('No');
    }
    destroyAlert();
}
