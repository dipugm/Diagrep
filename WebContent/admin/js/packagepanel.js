
var gPackageEdited = 0;

function show_packages() {
	
	// clear the right panel.
	clearRightPanel();
	
	var central_panel = document.getElementById( 'center_panel_layout' );
	
	var body = "";
	body += "<div class='page_header' style='background-color:#5A0000;'>";
	body += "<div class='page_header_title' style='width: 80%;'>Available Packges</div>";
	body += "<div class='main_option_button' style='width: 17%;' id='create_new' onclick='javascript:showViewForPackageEditing(0)' >Create New</div>";
	body += "</div>";
	
	body += "<table class='entity_table_header' border='0' cellspacing='0'>";
	body += "<tr>";
	body += "<th width='10%'>Sl No.</th>";
	body += "<th width='75%'>Name</th>";
	body += "<th width='15%'>Cost</th>";
	body += "</tr>";
	body += "</table>";
	
	body += "<div class='page_contents'>";

	body += "<table class='entity_table_contents' width='100%' border='0' cellspacing='0' id='table_packages' >";

	for( var i=0; i < gArrayEntities[kPackageType].length; i++ ) {
		var pkg = gArrayEntities[kPackageType][i];
		
		pkg.description = decodeURLEncodedString( pkg.description );
		
		body += "<tr";
		body += " class='odd_row'";
		body += " onclick=\"javascript:rowClickedForPackages(" + i + ")\">";
		body += "	<td width='10%' align='center'>" + (i+1) + "</td>";
		body += "	<td width='75%'>" + pkg.name + "<BR><font size='1'>[" + pkg.description + "]</font></td>";
		body += "	<td width='15%' style='text-align:right;padding-right:30px;'>" + pkg.cost + "</td>";
		body += "</tr>";
	}
	
	body += "</table>";
	body += "</div>";
	
	central_panel.innerHTML = body;
}

function showViewForPackageEditing( pack ) {
	
	var name="";
	var cost="";
	
	var entitiesWithOrder = [];
	
	var fModify=0;
	
	if( pack != 0 ) {
		
		name = pack.name;
		desc = pack.description;
		cost = pack.cost;
		entitiesWithOrder = pack.subEntitiesWithOrder;
		
		fModify = 1;
		
		gPackageEdited = getMutableCopy( pack );
	} else {
		gPackageEdited = {
				name:"", desc:"", type: kPackageType, id:-1, cost:"", subEntitiesWithOrder:[]};
	}
	
	gArrayEntityEdited[ kPackageType ] 	= gPackageEdited;
	
	gEntitiesAddedNewlyToPackage = new Array();
	
	body = "";
	body += "<table class='input_form_table'>";
	body += "<tr>";
	body += "	<td width='20%'>Name :</td>";
	
	if( fModify ){
		body += "	<td><b>" + name + "</b></td>";
	} else {
		body += "	<td><input type='text' class='text_box entity_text_box' style='width: 300px;' id='text_package_name'></td>";
	}
	
	body += "</tr>";
	body += "<tr>";
	body += "	<td width='20%'>Cost (Rs) :</td>";
	body += "	<td><input type='text' onkeypress='return isNumberKey(event)' class='text_box entity_text_box' style='width: 100px;' id='text_package_cost' value='" + cost + 
		"'></td>";
	body += "</tr>";
	
	body += "<tr>";
	body += "	<td width='20%'>Description :</td>";
	
	if( fModify ){
		body += "	<td><b>" + desc + "</b></td>";
	} else {
		body += "	<td colspan='4'><div class='styled_text_area' style='width: 300px; height: 70px;' id='text_package_description'></div></td>";
	}
	
	body += "</tr>";
	
	body += "<tr>";
	body += "	<td colspan='2'>Entities in this Package :</td>";
	body += "</tr>";
	
	body += "<tr>";
	body += "<td colspan='2'>";
	body += "<div class='text_button' style='float:left; width:100px; color: white; background-color:#3399FF; margin-right:5px;' onclick='javascript:showTestsForPackage()'>Add Tests</div>";
	body += "<div class='text_button' style='float:left;width:100px; color: white; background-color:#3399FF; margin-right:5px;' onclick='javascript:showCategoriesForPackage()'>Add Categories</div>";
	body += "<div class='text_button' style='float:left;width:100px; color: white; background-color:#3399FF; margin-right:5px;' onclick='javascript:showCollectionsForPackage()'>Add Collections</div>";
	
	body += "</td>";
	body += "</tr>";
	
	body += "<tr>";
	body += "	<td colspan='2'>";
	body += " <div style='width:400px; height:300px;overflow:auto; border:1px solid lightgray;'>";
	body += "<table class='table_sub_entity' id='table_package_subentities' cellPadding=0 cellSpacing=0>";

	// Display the entities in this category
	for( var i=0; i < entitiesWithOrder.length; i++ ) {
		
		var entity = entitiesWithOrder[i] ;
		var imageName = getImageForEntityType( entity.type );
		
		var rowId = "row_subentity_" + entity.type + "_" + entity.id;
		
		body += "<tr class=\"sub_entity_table_row\" onclick=\"javascript:toggleRowSelection('table_package_subentities','" + rowId + "')\" ";
		body += "id=\"" + rowId + "\">";
		body += "<td><img src='images/" + imageName + "' height='24' width='24'></td>"; 
		body += "<td>" + entity.name + "</td>";
		body += "<td class=\"td_close_icon\" id=\"imageCell\" onclick=\"javascript:removeEntityFromEntity(gPackageEdited," + entity.id + "," + entity.type + ", 'table_package_subentities')\">";
		body += "</td>"; 
		body += "</tr>";
	}
	
	body += "</table>";
	body += "</div>";
	body += "</td>";
	body += "</tr>";
	
	if( fModify ) {

		body += "<tr>";
		body += "<td>";
		body += "<div class='text_button' style='width: 150px; text-align: center; color: white; background-color:#3399FF;' ";
		body += "onclick='javascript:modifyPackageOnServer()'>Modify</div>";
		body += "</td>";
		body += "<td><div class='description_text'>Modify this package with new values</div></td>";
		body += "</tr>";
		body += "<tr>";
		body += "<td>";
		body += "<div class='text_button' id='delete_entity' style='width:150px;'";
		body += "onclick='javascript:deletePackageOnServer()'>Delete</div>";
		body += "</td>";
		body += "<td><div class='description_text'>Delete this package</div></td>";
		body += "</tr>";
	} else {
		body += "<tr>";
		body += "<td>";
		body += "<div class='text_button' style='width: 150px; text-align: center; color: white; background-color:#3399FF;' ";
		body += "onclick='javascript:createPackageOnServer()'>Create</div>";
		body += "</td>";
		body += "<td><div class='description_text'>Create a new package</div></td>";
		body += "</tr>";
	}
	
	body += "</table>";
	
	var rightPanel = document.getElementById( 'right_panel_layout' );
	rightPanel.innerHTML = body;
	
	makeElementEditable( 'text_package_description', kEditorNoBar );

}

function rowClickedForPackages( rowNum ) {
	
	var pkg = gArrayEntities[kPackageType][ rowNum ];
	showViewForPackageEditing(pkg);

}

function showTestsForPackage() {
	popupChoices( gArrayEntities[kTestType], 'Tests', 'addTestToPackage' );
}

function showCategoriesForPackage() {
	popupChoices( gArrayEntities[kCategoryType], 'Categories', 'addCategoryToPackage' );
}

function showCollectionsForPackage() {
	popupChoices( gArrayEntities[kCollectionType], 'Collections', 'addCollectionToPackage' );
}

function addTestToPackage( i ) {
	
	addEntityToEntity( 
			gPackageEdited, 
			gArrayEntities[kTestType][i],
			'table_package_subentities' );

}

function addCategoryToPackage( i ) {
	
	addEntityToEntity( 
			gPackageEdited, 
			gArrayEntities[kCategoryType][i],
			'table_package_subentities' );

}

function addCollectionToPackage( i ) {
	
	addEntityToEntity( 
			gPackageEdited, 
			gArrayEntities[kCollectionType][i],
			'table_package_subentities' );

}

function validityCheckForPackage(name,cost) {
	
	msg = "";
	if( name == "" ) {
		msg += "* Name cannot be blank\n";
	}
	if( cost == "" ) {
		msg += "* Cost cannot be blank\n";
	}
	
	if( msg != "" ) {
		msg = "Please see below for more information.\n\n" + msg;
		showError( msg, "Invalid input" );
		return false;
	}
	
	return true;
	
}

function createPackageOnServer() {
	gPackageEdited.name = document.getElementById('text_package_name').value;
	gPackageEdited.cost = document.getElementById('text_package_cost').value;
	gPackageEdited.description = getTextFromEditor('text_package_description');
	
	if( dataValidityCheck( gPackageEdited.name, gPackageEdited.cost ) == true ) {
		
        var queryParams = "type=" + kPackageType;
		queryParams += "&name=" + gPackageEdited.name;
		queryParams += "&cost=" + gPackageEdited.cost;
		queryParams += "&description=" + encodeURIComponent(gPackageEdited.description);
		queryParams += "&subEntitiesWithOrder=" + getSubEntitiesOrderAsCommanSeparatedString( gPackageEdited );
		
		displayBusy( "Create Package", "Please wait..." );
		
		sendAsyncAjaxRequestToServer("/Diagrep/CreateEntity",
                                     "POST",
                                     queryParams,
                                     "onCreatePackageResponseFromServer");
	}
}

function onCreatePackageResponseFromServer( resp ) {
    resObj = JSON.parse( resp );
    
    if( resObj.status == "success" ) {
        displaySuccess( "Create Package", "Successfully Created the Package." );

        gPackageEdited.id = parseInt( resObj.packageId );
        
        // Add the created package to the global array and re-render.
        gArrayEntities[ kPackageType ].push( gPackageEdited );
        gPackageEdited = 0;
        
        show_packages();
    } else {
    	displayError( "Create Package", "Create Failed - " + resObj.info );
    }

}

function modifyPackageOnServer() {
	gPackageEdited.cost = document.getElementById('text_package_cost').value;

	if( dataValidityCheck( "name", gPackageEdited.cost ) == true ) {
		
		var queryParams = "type=" + kPackageType;
		queryParams += "&id=" + gPackageEdited.id;
		queryParams += "&cost=" + gPackageEdited.cost;
		queryParams += "&subEntitiesWithOrder=" + getSubEntitiesOrderAsCommanSeparatedString( gPackageEdited );
		
 		displayBusy( "Modify Package", "Please wait..." );
 				
		sendAsyncAjaxRequestToServer("/Diagrep/ModifyEntity",
                                     "POST",
                                     queryParams,
                                     "onModifyPackageResponseFromServer");
		
	}
}

function onModifyPackageResponseFromServer( resp ) {
    resObj = JSON.parse( resp );
    
    if( resObj.status == "success" ) {
        displaySuccess( "Modify Package", "Successfully Modified the Package.");
        
        // Replace the package in the global array and re-render.
        replaceEntityInGlobalArray( gPackageEdited );
        gPackageEdited = 0;
        
        show_packages();
    } else {
    	displayError( "Modify Package", "Modify Failed - " + resObj.info );
    }
}

function deletePackageOnServer() {
	showQuestion( "Are you sure you want to delete this Package?",
                 "Confirm Delete",
                 onDeletePackageUserConfirmation );
}

function onDeletePackageUserConfirmation( option ) {
    if( option == "Yes" ) {
        
        displayBusy( "Delete Package", "Please wait..." );
        var queryParams = "id=" + gPackageEdited.id;
        queryParams += "&type=" + kPackageType;
		
		sendAsyncAjaxRequestToServer("/Diagrep/RemoveEntity",
                                   "POST",
                                   queryParams,
                                   "onDeletePackageResponseFromServer");

    }
}

function onDeletePackageResponseFromServer( resp ) {
    resObj = JSON.parse( resp );
    
    if( resObj.status == "success" ) {
        displaySuccess( "Delete Package", "Successfully Deleted the Package." );
        
        // Remove this package from the global array and re-render.
        deleteEntityInGlobalArray( gPackageEdited );
        gPackageEdited = 0;
        
        show_packages();
    } else {
    	displayError( "Delete Package", "Delete Failed - " + resObj.info );
    }
}
