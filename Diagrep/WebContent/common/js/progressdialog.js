

var gMaxRange=100;

function showProgressDialog( titleText, messageText, maxRange ) {
	
    popup_width = 400;
    gMaxRange = maxRange;
    
    var lines = (messageText.length / 60) + 1;
	popup_height = 100 + (20 * lines );
    left = 100;
    top = 100;
	
    // Create a div element that covers the whole screen to
    // make the popup look like a modal dialog.
    var main_container = document.createElement('DIV');
    main_container.className="progressdialog_background";
    main_container.id = "progressdialog_background";
    main_container.style.left = 0 + "px";
    main_container.style.top = 0 + "px";
    main_container.style.height = "100%";
    main_container.style.width = "100%";
    
    document.body.appendChild( main_container );
    
	var left = (window.innerWidth - popup_width) / 2;
	var top = (window.innerHeight - popup_height) / 2;
    
    var popup_container = document.createElement('DIV');
    popup_container.className = "progressdialog_container";
    popup_container.id = "progressdialog_container";
    popup_container.style.width = popup_width + 'px';
    popup_container.style.height = popup_height + 'px';
	popup_container.style.left = left + 'px';
	popup_container.style.top = top + 'px';
    
    document.body.appendChild( popup_container );
    
    /* Title text */
    var titleHolder = document.createElement( 'DIV' );
    titleHolder.style.width = "100%";
    titleHolder.style.height = "30px";
    
    var titleTextDiv = document.createElement('DIV');
    titleTextDiv.className = "progressdialog_titletext";
    titleTextDiv.id = "progressdialog_titletext";
    titleTextDiv.innerHTML = titleText;
    titleHolder.appendChild( titleTextDiv );
    
    var percentText = document.createElement( 'DIV' );
    percentText.className = "progressdialog_percent";
    percentText.id = "progressdialog_percent";
    percentText.innerHTML = "0%";
    titleHolder.appendChild( percentText );
    
    popup_container.appendChild( titleHolder );
    
    var separator = document.createElement( 'HR' );
    separator.className = "progressdialog_seperator";
    separator.id = "progressdialog_seperator";
    popup_container.appendChild( separator );
    
    var messageTextDiv = document.createElement('DIV');
    messageTextDiv.className = "progressdialog_messagetext";
    messageTextDiv.id = "progressdialog_messagetext";
    messageTextDiv.innerHTML = messageText;
    
    popup_container.appendChild( messageTextDiv );

    /* Progress bar container */
    var progressContainer = document.createElement( 'DIV' );
    progressContainer.className = "progressdialog_progress_container";
    progressContainer.id = "progressdialog_progress_container";
    
    /* Actual progress bar */
    var progressBar = document.createElement( 'DIV' );
    progressBar.className = "progressdialog_progress_bar";
    progressBar.id = "progressdialog_progress_bar";
    
    /* Add the progress bar to the progress container */
    progressContainer.appendChild( progressBar );
    popup_container.appendChild( progressContainer );
}

function doProgress( value, messageText ) {
    var progressValue = (value * 100) / gMaxRange;
    
    var progBar = document.getElementById( "progressdialog_progress_bar" );
    progBar.style.width = progressValue + "%";
    
    var percentText = document.getElementById( "progressdialog_percent" );
    percentText.innerHTML = progressValue + "%";
    
    if( messageText != "" ) {
        var messageTextDiv = document.getElementById( "progressdialog_messagetext" );
        messageTextDiv.innerHTML = messageText;
    }
}

function closeProgressDialog() {
    /* We delay destroying the dialog by 1 second so that any progress
     messages that might have been sent in the doProgress function gets 
     time to render. */
    setTimeout(
        function() {
           var bkg = document.getElementById( "progressdialog_background" );
           var cont = document.getElementById( "progressdialog_container" );
           
           document.body.removeChild( cont );
           document.body.removeChild( bkg );
        },
               1000 );
    
}

