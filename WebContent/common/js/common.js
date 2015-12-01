
/*
 * 
 * This script is common to client and admin java scripts. 
 * This file must be included in all files that include the 
 * admin or client java scripts. 
 */

var kTestType 		= 0;
var kCategoryType	= 1;
var kCollectionType	= 2;
var kPackageType	= 3;

// Array storing all the loaded entities. Maintains an array of 
// entity arrays.
var gArrayEntities = [0,0,0,0];

var xPosOfCursor=0, yPosOfCursor=0;

function getMousePosition(e) { 
	var pos = e.pageX ? 
		{'x':e.pageX, 'y':e.pageY} : 
		{'x':e.clientX + document.documentElement.scrollLeft + 
			document.body.scrollLeft, 
		 'y':e.clientY + document.documentElement.scrollTop + 
			document.body.scrollTop}; 
	
	xPosOfCursor = pos['x'];
	yPosOfCursor = pos['y'];
	
	return pos;
}

function isStandardKey( evt ) {
	
	var charCode = (evt.which) ? evt.which : evt.keyCode;
	return charCode == 32 || charCode == 8 || 
			charCode == 9 || charCode == 13 || 
			charCode == 127 || (charCode >= 37 && charCode <= 40);  
}

function isNumberKey(evt) {
    var charCode = (evt.which) ? evt.which : evt.keyCode;
    if( (charCode >= 48 && charCode <= 57) || isStandardKey(evt) ) {
        return true;
    }
    return false;
}

function isTextAlphabetsOnly( evt ) {
	var charCode = (evt.which) ? evt.which : evt.keyCode;
	if( (charCode > 64 && charCode < 91) || (charCode > 96 && charCode < 123) 
			|| isStandardKey(evt) ) {
		return true;
	}
		
    return false;
}

/*
 * Entity related methods.
 */
function getEntityForId( id, type ) {
	for( var i=0; i < gArrayEntities[type].length; i++ ) {
		var entity = gArrayEntities[type][i];
		if( entity.id == id ) {
			return entity;
		}
	}
	return 0;
}

function typeString( entityType ) {
	var type = "";
	switch( entityType ) {
		
		case kTestType:
			type = "Test";
			break;
			
		case kCategoryType:
			type = "Category";
			break;
			
		case kCollectionType:
			type = "Collection";
			break;
			
		case kPackageType:
			type = "Package";
			break;
	} 
	
	return type;
}

function getImageForEntityType( type ) {
	var imageName = "";
	if( type == kTestType ) {
		imageName = "testsIcon.png";
	} else if( type == kCategoryType ) {
		imageName = "groupIcon.png";
	} else if( type == kCollectionType ) {
		imageName = "collectionIcon.png";
	}
	
	return imageName;
}

function getMutableCopy( obj ) {
	// To create a mutable copy we use JSON.
	var mutObj = JSON.parse( JSON.stringify( obj ) );
	return mutObj;
}

function encodeWithCustomUrlEncoding( sz ) {	
	sz = sz.replace(/\+/g, "__PLUS__SIGN__");
	sz = sz.replace(/'/g,"__SINGLE__QUOTES__");
	sz = sz.replace(/%/g,"__PERCENT__");
	
	sz = encodeURIComponent( sz );
	
	return sz;
}

function decodeURLEncodedString( sz ) {
	
	try {
		sz = decodeURIComponent( sz );
		sz = sz.split("+").join(" ");
		
		sz = sz.replace(/__PLUS__SIGN__/g, "+");
		sz = sz.replace(/__SINGLE__QUOTES__/g, "'");
		sz = sz.replace(/__PERCENT__/g, "%");
	} catch( e ) {
		// Dummy lines to make flag that we have handled the exception
		var i=0;
		i++;
	}
	
	return sz;
}
