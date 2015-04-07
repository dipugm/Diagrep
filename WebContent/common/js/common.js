
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

function isNumberKey(evt) {
    var charCode = (evt.which) ? evt.which : evt.keyCode;
    if( (charCode >= 48 && charCode <= 57)  
			|| charCode == 32 || charCode == 9 || charCode == 13 || charCode == 127 ) {
        return true;
    }
    return false;
}

function isTextAlphabetsOnly( evt ) {
	var charCode = (evt.which) ? evt.which : evt.keyCode;
	if( (charCode > 64 && charCode < 91) || (charCode > 96 && charCode < 123) 
			|| charCode == 32 || charCode == 8 || charCode == 9 || charCode == 13 || charCode == 127 ) {
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

function decodeURLEncodedString( sz ) {
	
	try {
		sz = decodeURIComponent( sz );
		sz = sz.split("+").join(" ");
	} catch( e ) {
		// Dummy lines to make flag that we have handled the exception
		var i=0;
		i++;
	}
	
	return sz;
}

function makeElementEditable( element ) {
	
	var elemObj = document.getElementById( element );
	
	szBody = "<Table style='margin-left: 10px;'>";
	szBody += "	<tr>";
	szBody += "		<td>";
	szBody += "			<img src='../common/images/text_bold.png' href='javascript:void(0)' onclick=\"fontEdit('bold')\" class='tool_button'>";
	szBody += "			<img src='../common/images/text_italic.png' href='javascript:void(0)' onclick=\"fontEdit('italic')\" class='tool_button'>";
	szBody += "			<img src='../common/images/text_underline.png' href='javascript:void(0)' onclick=\"fontEdit('underline')\" class='tool_button'>";
	szBody += "		</td>";
	szBody += "		<td>";
	szBody += "			<div id='separator'></div>";			
	szBody += "		</td>";
	szBody += "		<td>";
	szBody += "			<img src='../common/images/text_align_left.png' href='javascript:void(0)' onclick=\"fontEdit('justifyleft')\" class='tool_button'>";
	szBody += "			<img src='../common/images/text_align_center.png' href='javascript:void(0)' onclick=\"fontEdit('justifycenter')\" class='tool_button'>";
	szBody += "			<img src='../common/images/text_align_right.png' href='javascript:void(0)' onclick=\"fontEdit('justifyright')\" class='tool_button'>";
	szBody += "		</td>";
	szBody += "		<td>";
	szBody += "			<div id='separator'></div>";			
	szBody += "		</td>";
	szBody += "		<td>";
	szBody += "			<img src='../common/images/text_list_bullets.png' href='javascript:void(0)' onclick=\"fontEdit('insertunorderedlist')\" class='tool_button'>";
	szBody += "			<img src='../common/images/text_list_numbers.png' href='javascript:void(0)' onclick=\"fontEdit('insertorderedlist')\" class='tool_button'>";
	szBody += "		</td>";
	szBody += "		<td>";
	szBody += "			<div id='separator'></div>";			
	szBody += "		</td>";
	szBody += "		<td>";
	szBody += "			<img src='../common/images/text_indent.png' href='javascript:void(0)' onclick=\"fontEdit('indent')\" class='tool_button'>";
	szBody += "			<img src='../common/images/text_indent_remove.png' href='javascript:void(0)' onclick=\"fontEdit('outdent')\" class='tool_button'>";
	szBody += "		</td>";
	szBody += "		<td>";
	szBody += "			<div id='separator'></div>";			
	szBody += "		</td>";
	szBody += "		<td class='styled-select-header'>Text Size : </td>";
	szBody += "		<td> <div class='styled-select'>";
	szBody += "		<select onChange=\"javascript:fontEdit('fontsize',this[this.selectedIndex].value)\">";
	szBody += "			<option value='2'>Normal</option>";
	szBody += "			<option >-----------</option>";
	szBody += "			<option value='5'>Heading 1</option>";
	szBody += "			<option value='4'>Heading 2</option>";
	szBody += "			<option value='3'>Heading 3</option>";
	szBody += "		</select></div>";
	szBody += "		</td>";
	
	
	szBody += "	</tr>";
	szBody += "</table>";
	
	editorHeight = elemObj.style.height * 0.75;
	
	var id = elemObj.id + "_textarea"; 
	szBody += "<iframe class='textEditor' id='" + id + "' style='height:" + editorHeight + "px;'></iframe>";
	
	textEd = document.getElementById( id );
	textEd = (textEd.contentWindow) ? textEd.contentWindow : (textEd.contentDocument.document);
	
	textEd.document.designMode="on";
	textEd.document.open();
	textEd.document.write("<head><style type='text/css'>body{ font-family:arial; font-size:13px; }</style> </head>");
	textEd.document.write("<body>" + elemObj.innerHTML + "</body>");
	textEd.document.close();
	textEd.focus();
}
