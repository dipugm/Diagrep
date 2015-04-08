
/********************************************************************/
/*
This file contains the functions for a text editor that can be used
to prepare formatted contents. Any div element can be converted to an
editor by calling makeElementEditable.
Editor suppors 3 modes.
1. kEditorNoBar - No stylying buttons bar will be displayed.
2. kEditorMiniBar - A small set of buttons will be displayed in the bar.
					Bold, Italic, Underline, Numbered and Bullets.
3. kEditorFullBar - All the styling buttons will be displayed in the bar.
					Bold, Italic, Underline, Numbered, Bullets, Indentation,
					Text sizing.
					
IMPORTANT - Requires that texteditor.css be imported in the main html
*/					
/********************************************************************/

var kEditorNoBar	= 0;
var kEditorMiniBar	= 1;
var kEditorFullBar	= 2;

function getTextFromEditor( element ) {
	var id = element + "_textarea";
	
	var textEd = document.getElementById( id );
	textEd = (textEd.contentWindow) ? textEd.contentWindow : (textEd.contentDocument.document);
	
	var szText 	= textEd.document.body.innerHTML;
	szText 		= szText.trim();
	
	return szText;
}

function setTextInEditor( element, text ) {
	var id = element + "_textarea";
	
	var textEd = document.getElementById( id );
	textEd = (textEd.contentWindow) ? textEd.contentWindow : (textEd.contentDocument.document);
	
	textEd.document.body.innerHTML = text;
}

function makeElementEditable( element, mode, text ) {
	
	var elemObj = document.getElementById( element );
	var id = element + "_textarea";
	
	szBody = "";
	
	if( mode != kEditorNoBar ) {
		szBody += "<Table>";
		szBody += "	<tr>";
		szBody += "		<td style='padding-left: 5px; padding-top:3px;'>";
		szBody += "			<img src='../common/images/text_bold.png' href='javascript:void(0)' onclick=\"fontEdit('" + id + "','bold')\" class='tool_button'>";
		szBody += "			<img src='../common/images/text_italic.png' href='javascript:void(0)' onclick=\"fontEdit('" + id + "','italic')\" class='tool_button'>";
		szBody += "			<img src='../common/images/text_underline.png' href='javascript:void(0)' onclick=\"fontEdit('" + id + "','underline')\" class='tool_button'>";
		szBody += "		</td>";
		szBody += "		<td>";
		szBody += "			<div id='separator'></div>";			
		szBody += "		</td>";
		szBody += "		<td>";
		szBody += "			<img src='../common/images/text_list_bullets.png' href='javascript:void(0)' onclick=\"fontEdit('" + id + "','insertunorderedlist')\" class='tool_button'>";
		szBody += "			<img src='../common/images/text_list_numbers.png' href='javascript:void(0)' onclick=\"fontEdit('" + id + "','insertorderedlist')\" class='tool_button'>";
		szBody += "		</td>";
	
		
		if( mode == kEditorFullBar ) {
			szBody += "		<td>";
			szBody += "			<div id='separator'></div>";			
			szBody += "		</td>";
			szBody += "		<td>";
			szBody += "			<img src='../common/images/text_align_left.png' href='javascript:void(0)' onclick=\"fontEdit('" + id + "','justifyleft')\" class='tool_button'>";
			szBody += "			<img src='../common/images/text_align_center.png' href='javascript:void(0)' onclick=\"fontEdit('" + id + "','justifycenter')\" class='tool_button'>";
			szBody += "			<img src='../common/images/text_align_right.png' href='javascript:void(0)' onclick=\"fontEdit('" + id + "','justifyright')\" class='tool_button'>";
			szBody += "		</td>";
			szBody += "		<td>";
			szBody += "			<div id='separator'></div>";			
			szBody += "		</td>";
			szBody += "		<td>";
			szBody += "			<img src='../common/images/text_indent.png' href='javascript:void(0)' onclick=\"fontEdit('" + id + "','indent')\" class='tool_button'>";
			szBody += "			<img src='../common/images/text_indent_remove.png' href='javascript:void(0)' onclick=\"fontEdit('" + id + "','outdent')\" class='tool_button'>";
			szBody += "		</td>";
			szBody += "		<td>";
			szBody += "			<div id='separator'></div>";			
			szBody += "		</td>";
			szBody += "		<td class='styled-select-header'>Text Size : </td>";
			szBody += "		<td> <div class='styled-select'>";
			szBody += "		<select onChange=\"javascript:fontEdit('" + id + "','fontsize',this[this.selectedIndex].value)\">";
			szBody += "			<option value='2'>Normal</option>";
			szBody += "			<option >-----------</option>";
			szBody += "			<option value='5'>Heading 1</option>";
			szBody += "			<option value='4'>Heading 2</option>";
			szBody += "			<option value='3'>Heading 3</option>";
			szBody += "		</select></div>";
			szBody += "		</td>";
		}
		
		szBody += "	</tr>";
		szBody += "</table>";
	}
	
	szBody += "<iframe class='textEditor' id='" + id + "' style='height:80%;";
	if( mode == kEditorNoBar ) {
		szBody += "border: 0px;";
	}
	szBody += "'></iframe>";
	
	elemObj.innerHTML = szBody;
	
	textEd = document.getElementById( id );
	textEd = (textEd.contentWindow) ? textEd.contentWindow : (textEd.contentDocument.document);
	
	textEd.document.designMode="on";
	textEd.document.open();
	textEd.document.write("<head><style type='text/css'>body{ font-family:arial; font-size:13px; }</style> </head>");
	textEd.document.write("<body>" + text + "</body>");
	textEd.document.close();
	
	textEd.onclick = function(e) {
		this.focus();
	};
}

function fontEdit( textEdId, x, y )
{
	textEd = document.getElementById( textEdId );
	textEd = (textEd.contentWindow) ? textEd.contentWindow : (textEd.contentDocument.document);
	
	textEd.document.execCommand(x,"",y);
	textEd.focus();
}
