
/*
This file contains the functions for a text editor that can be used
to decorate the text in a given text box.
*/

var gTextElement = "";
var gCallbackFunction = 0;

function showLargeEditor( textElement, title, callbackFun ) {
	showEditor( textElement, title, 700, 550, callbackFun );
}

function showSmallEditor( textElement, title, callbackFun ) {
	showEditor( textElement, title, 700, 300, callbackFun );
}

function showEditor( textElement, title, width, height, callbackFun ) {
	
	var textarea = document.getElementById(textElement);
	
	gTextElement = textElement;
	gCallbackFunction = callbackFun;
	
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
	
	editorHeight = height * 0.75;
	
	szBody += "<iframe class='textEditor' id='textEditor' style='height:" + editorHeight + "px;'></iframe>";
	szBody += "<div onclick=\"javascript:updateStyledText('" + textElement + "')\" class='text_button' style='width: 100px; font-size:12px;margin:auto;margin-top:10px;'>Update</div>";
	
	createPopupContainer(width, height, title, szBody, 'Close' );
	
	textEd = document.getElementById( 'textEditor' );
	textEd = (textEd.contentWindow) ? textEd.contentWindow : (textEd.contentDocument.document);
	
	textEd.document.designMode="on";
	textEd.document.open();
	textEd.document.write("<head><style type='text/css'>body{ font-family:arial; font-size:13px; }</style> </head>");
	textEd.document.write("<body>" + textarea.innerHTML + "</body>");
	textEd.document.close();
	textEd.focus();
	
}

function fontEdit(x,y)
{
	textEd = document.getElementById( 'textEditor' );
	textEd = (textEd.contentWindow) ? textEd.contentWindow : (textEd.contentDocument.document);
	
	textEd.document.execCommand(x,"",y);
	textEd.focus();
}

function updateStyledText(  ) {
	var textarea = document.getElementById( gTextElement );
	
	textEd = document.getElementById( 'textEditor' );
	textEd = (textEd.contentWindow) ? textEd.contentWindow : (textEd.contentDocument.document);
	
	var szText = textEd.document.body.innerHTML.replace( /&nbsp;/g,"");
	
	szText = szText.replace( /\"/g, "-");
	szText = szText.replace( /\'/g, "-");
	szText = szText.trim();

	textarea.innerHTML = szText;
	
	onDone();
	
	// do a call back on the callback function to indicate that the contents of this text
	// element has changed.
	if( gCallbackFunction != "" ) {
		window[gCallbackFunction]();
	}
}


