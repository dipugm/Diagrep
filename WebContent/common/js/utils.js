


//function sendAjaxRequestToServer( url, reqType, body ) {
//
//	var httpReq;
//    if( window.XMLHttpRequest ) {
//        httpReq     = new XMLHttpRequest();
//    } else {
//        httpReq     = new ActiveXObject( "Microsoft.XMLHTTP");
//    }
//    
//    var datatosend = body;
//	
//	if( reqType == "GET") {
//		url	+= "?" + datatosend;
//		datatosend = "";
//	} 
//    
//    httpReq.open(reqType, url, false);
//	if( reqType == "POST" ) {
//		httpReq.setRequestHeader("Content-type","application/x-www-form-urlencoded");
//	}
//    httpReq.send( datatosend );
//
//    if( httpReq.readyState == 4 && httpReq.status == 200 ) {
//	//	alert( httpReq.responseText );
//    	return httpReq.responseXML && httpReq.responseXML.documentElement != null ? httpReq.responseXML : httpReq.responseText;
//    }
//    
//    return null;
//}

function sendAsyncAjaxRequestToServer( url, reqType, body, callbackFun ) {
    
	var httpReq = 0;
    if( window.XMLHttpRequest ) {
        httpReq     = new XMLHttpRequest();
    } else {
        httpReq     = new ActiveXObject( "Microsoft.XMLHTTP");
    }

    httpReq["myCallbackFunctionName"] = callbackFun;
    
    var datatosend = body;
	
	if( reqType == "GET") {
		url	+= "?" + datatosend;
		datatosend = "";
	}
    
    httpReq.onreadystatechange=function()
    {
        if( httpReq.readyState == 4 && httpReq.status == 200 ) {
            
            var resp = httpReq.responseXML && httpReq.responseXML.documentElement != null ?
                            httpReq.responseXML : httpReq.responseText;
            
            window[ httpReq.myCallbackFunctionName ]( resp );
        }
    };
    
    httpReq.open(reqType, url, true);
	if( reqType == "POST" ) {
		httpReq.setRequestHeader("Content-type","application/x-www-form-urlencoded");
	}
    httpReq.send( datatosend );
}
