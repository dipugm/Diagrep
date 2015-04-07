
var gCallbackFunction=0;

function popupChoices( choices, title, callback ) {
	gCallbackFunction = callback;
	
	if( choices.length == 0 ) {
		return;
	}
	
	var maxHeight = 300;
	
	popup_height = choices.length * 40 + 25;
	if( popup_height > maxHeight ) {
		popup_height = maxHeight;
	}
	
	popup_width = 200;
	
	var left = xPosOfCursor;
	var top = yPosOfCursor - (popup_height / 2);
	
    // Create a div element that covers the whole screen to
    // make the popup look like a modal dialog.
    var main_container = document.createElement('DIV');
    main_container.className='popup_main_container';
    main_container.id = 'popup_main_container';
	main_container.onclick = function( ) {
		cont = document.getElementById( 'popup_main_container' );
		document.body.removeChild( cont );
	};
    
    document.body.appendChild( main_container );
	
  
	/*
    Arrow above the dialog.
	*/
    var arrowDiv = document.createElement('DIV');
    arrowDiv.className = 'popup_arrow';
    arrowDiv.id = 'popup_arrow';
  	arrowDiv.style.left = xPosOfCursor + 'px';
 	arrowDiv.style.top = yPosOfCursor + 'px';
   
    main_container.appendChild( arrowDiv );

	/*
	popup container
	*/
  	var popup_container = document.createElement('DIV');
    popup_container.className = 'popup_container';
    popup_container.id = 'popup_container';

    popup_container.style.height = popup_height + 'px';
	popup_container.style.left = (left + 32) + 'px';
	popup_container.style.top = top + 'px';

	popup_container.onclick = function( e ) {
		e.bubbles = false;
		e.cancelbubble = true;
		if( e.stopPropagation ) {
			e.stopPropagation();
		}
	};

    main_container.appendChild( popup_container );

    
	/*
		Navigation Bar
	*/
    var navBarDiv = document.createElement('DIV');
    navBarDiv.className = 'popup_nav_bar';
    navBarDiv.id = 'popup_nav_bar';
    // navBarDiv.style.width = width + 'px';
	navBarDiv.innerHTML = title;
    
    popup_container.appendChild( navBarDiv );
    
	var szBody = "<table>";
	for( var i=0; i < choices.length; i++ ) {
		var ch = choices[i];
		szBody += "<tr class='even_row' onclick='javascript:window[gCallbackFunction](" + i +")'>";
		szBody += "<td>" + ch.name + "</td>";
		szBody += "</tr>";
	}
	
	szBody += "</table>";
    
	/*
		Body of the dialog.
	*/
    var popup_content = document.createElement('DIV');
    popup_content.className = 'popup_content';
    popup_content.id = 'popup_content';
    popup_content.style.height = (popup_height - 45) + 'px';
	popup_content.innerHTML = szBody;
    
    popup_container.appendChild( popup_content );

}

