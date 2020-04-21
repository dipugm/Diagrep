
var gEntitiesAddedNewlyToCollection=0;
var gCollectionEdited = 0;

function show_collections() {
	
	// clear the right panel.
	clearRightPanel();
	
	var central_panel = document.getElementById( 'center_panel_layout' );
	
	var body = "";
	body += "<div class='page_header' style='background-color:#214300;'>";
	body += "<div class='page_header_title' style='width: 80%;'>Available Collections</div>";
	body += "<div class='main_option_button' id='create_new' style='width:17%;line-height:40px;' onclick='javascript:showViewForCollectionEditing(0)' >Create New</div>";
	body += "</div>";
	
	body += "<table class='entity_table_header' border='0' cellspacing='0'>";
	body += "<tr>";
	body += "<th width='10%'>Sl No.</th>";
	body += "<th width='70%'>Name</th>";
	body += "<th width='20%'>Cost</td>";
	body += "</tr>";
	body += "</table>";
	
	body += "<div class='page_contents'>";
	
	body += "<table class='entity_table_contents' border='0' cellspacing='0' id='table_collections'>";
	
	for( var i=0; i < gArrayEntities[kCollectionType].length; i++ ) {
		var col = gArrayEntities[kCollectionType][i];
		body += "<tr";
		body += " class='odd_row'";
		body += " onclick=\"javascript:rowClickedForCollections(" + i + ")\">";
		body += "	<td width='10%' align='center'>" + (i+1) + "</td>";
		body += "	<td width='70%'>" + decodeURLEncodedString( col.name ) + "</td>";
		body += "	<td width='20%' style='text-align:right;padding-right:30px;'>" + col.cost + "</td>";	
		body += "</tr>";
	}
	
	body += "</table>";
	body += "</div>";
	
	central_panel.innerHTML = body;
}

function showViewForCollectionEditing( coll ) {
	
	var name="";
    var cost=0.0;
	var fModify=0;
	var subEntitiesWithOrder = [];
	
	if( coll != 0 ) {
		name = coll.name;
        cost = coll.cost;
		subEntitiesWithOrder = coll.subEntitiesWithOrder;
		
		gCollectionEdited = getMutableCopy( coll );
		fModify = 1;
	} else {
		gCollectionEdited = {
				id:-1, name:"", type: kCollectionType, cost:"", subEntitiesWithOrder:[]};
	}
	
	gArrayEntityEdited[ kCollectionType ] 	= gCollectionEdited;

	body = "";
	body += "<table class='input_form_table'>";
	body += "<tr>";
	body += "	<td width='20%'>Name :</td>";
	
	if( fModify ){
		body += "	<td><b>" + name + "</b></td>";
	} else {
		body += "	<td><input type='text' class='text_box entity_text_box' style='width: 300px;' id='text_collection_name'></td>";
	}
	
	body += "</tr>";
	body += "<tr>";
    body += "	<td width='20%'>Cost (Rs) :</td>";
	body += "	<td><input type='text' onkeypress='return isNumberKey(event)' class='text_box entity_text_box' id='text_collection_cost' value='" + cost + "'></td>";
	
	body += "</tr>";
	body += "<tr>";
	body += "	<td colspan='2'>Entities in this Collection :</td>";
	body += "</tr>";
	body += "<tr>";
	body += "	<td colspan='2'>";
	body += "<div class='text_button' style='float:left; width:150px; color: white; background-color:#3399FF; margin-right: 10px;' onclick='javascript:showTestsForCollection()'>Add Tests</div>";
	body += "<div class='text_button' style='float:left; width:150px; color: white; background-color:#3399FF' onclick='javascript:showCategoriesForCollection()'>Add Categories</div>";
	body += "</td>";
	body += "</tr>";
	
	body += "<tr>";
	body += "	<td colspan='2'>";
	body += " <div style='width:400px; height:200px;overflow:auto; border:1px solid lightgray;'>";
	body += "<table class='table_sub_entity' id='table_collection_subentities' cellPadding=0 cellSpacing=0>";
		
	for( var i=0; i < subEntitiesWithOrder.length; i++ ) {

		var entity = subEntitiesWithOrder[i];

		var imageName = getImageForEntityType( entity.type );
		
		var rowId = "row_subentity_" + entity.type + "_" + entity.id;
		
		body += "<tr class=\"sub_entity_table_row\" onclick=\"javascript:toggleRowSelection('table_collection_subentities','" + rowId + "')\" ";
		body += "id=\"" + rowId + "\">";
 		body += "<td><img src='images/" + imageName + "' height='24' width='24'></td>"; 
		body += "<td>" + entity.name + "</td>";
		body += "<td class=\"td_close_icon\" id=\"imageCell\" onclick=\"javascript:removeEntityFromEntity( gCollectionEdited," + entity.id + "," + entity.type  + ", 'table_collection_subentities' )\">";
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
		body += "onclick='javascript:modifyCollectionOnServer()'>Modify</div>";
		body += "</td>";
		body += "<td><div class='description_text'>Modify this collection with new values</div></td>";
		body += "<tr>";
		body += "<td>";
		body += "<div class='text_button' id='delete_entity' style='width:150px;'";
		body += "onclick='javascript:deleteCollectionOnServer()'>Delete</div>";
		body += "</td>";
		body += "<td><div class='description_text'>Delete this collection</div></td>";
		body += "</tr>";
	} else {
		body += "<tr>";
		body += "<td>";
		body += "<div class='text_button' style='width: 150px; text-align: center; color: white; background-color:#3399FF;' ";
		body += "onclick='javascript:createCollectionOnServer()'>Create</div>";
		body += "</td>";
		body += "<td><div class='description_text'>Create a new collection</div></td>";
		body += "</tr>";
	}
	
	body += "</table>";
	
	var rightPanel = document.getElementById( 'right_panel_layout' );
	rightPanel.innerHTML = body;
}

function rowClickedForCollections( rowNum ) {	
	var coll = gArrayEntities[kCollectionType][ rowNum ];
	showViewForCollectionEditing(coll);
}

function showTestsForCollection() {
	
	popupChoices( gArrayEntities[kTestType], 'Tests', 'addTestToCollection' );
}

function showCategoriesForCollection() {
	
	popupChoices( gArrayEntities[kCategoryType], 'Categories', 'addCategoryToCollection' );
}


function validityCheckForCollection(name, cost) {
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

function createCollectionOnServer() {
	gCollectionEdited.name = encodeWithCustomUrlEncoding( document.getElementById('text_collection_name').value );
	gCollectionEdited.cost = document.getElementById('text_collection_cost').value;
	
	if( dataValidityCheck( gCollectionEdited.name, gCollectionEdited.cost ) == true ) {
		
		var queryParams = "type=" + kCollectionType;
		queryParams += "&name=" + gCollectionEdited.name;
	    queryParams += "&cost=" + gCollectionEdited.cost;
	    queryParams += "&subEntitiesWithOrder=" + getSubEntitiesOrderAsCommanSeparatedString( gCollectionEdited );

		displayBusy( "Create Collection", "Please wait..." );
        
		resp = sendAsyncAjaxRequestToServer("/Diagrep/CreateEntity",
                                            "POST",
                                            queryParams,
                                            "onCreateCollectionResponseFromServer");
	}
}

function onCreateCollectionResponseFromServer( resp ) {
    resObj = JSON.parse( resp );
    
    if( resObj.status == "success" ) {
        
        displaySuccess( "Create Collection", "Successfully Created the Collection." );

        gCollectionEdited.id 	= parseInt( resObj.collectionId );
        
        // Add the new collection object to the global array and re-render.
        gArrayEntities[ kCollectionType ].push( gCollectionEdited );
        gCollectionEdited = 0;
        
        show_collections();
	} else {
		displayError( "Create Collection", "Create Failed - " + resObj.info );
	}

}

function modifyCollectionOnServer() {
	gCollectionEdited.cost = document.getElementById('text_collection_cost').value;
    
	if( dataValidityCheck( "name", gCollectionEdited.cost ) == true ) {
        
		var queryParams = "type=" + kCollectionType ;
		queryParams += "&id=" + gCollectionEdited.id;
		queryParams += "&cost=" + gCollectionEdited.cost;
		queryParams += "&subEntitiesWithOrder=" + getSubEntitiesOrderAsCommanSeparatedString( gCollectionEdited );
		
		displayBusy( "Modify Collection", "Please wait..." );
		sendAsyncAjaxRequestToServer("/Diagrep/ModifyEntity",
                                     "POST",
                                     queryParams,
                                     "onModifyCollectionResposeFromServer");
		
	}
}

function onModifyCollectionResposeFromServer( resp ) {
    resObj = JSON.parse( resp );
    
    if( resObj.status == "success" ) {
        displaySuccess( "Modify Collection", "Successfully Modified the Collection." );
        
        // Replace the collection object in the global array and re-render.
        replaceEntityInGlobalArray( gCollectionEdited );
        kCollectionEdited = 0;
        
        show_collections();
        
    } else {    
        displayError( "Modify Collection", "Modify Failed - " + resObj.info);
    }
}

function deleteCollectionOnServer() {
	showQuestion( "Are you sure you want to delete this Collection?",
                 "Confirm Delete",
                 onDeleteCollectionUserConfirmation );
}

function onDeleteCollectionUserConfirmation( option ) {
    if( option == "Yes" ) {
        displayBusy( "Delete Collection", "Please wait..." );
        var queryParams = "type=" + kCollectionType;
        queryParams += "&id=" + gCollectionEdited.id;
		
		sendAsyncAjaxRequestToServer("/Diagrep/RemoveEntity",
                                     "POST",
                                     queryParams,
                                     "onDeleteCollectionResponseFromServer");
    }
}

function onDeleteCollectionResponseFromServer( resp ) {
    resObj = JSON.parse( resp );
    
    if( resObj.status == "success" ) {
        displaySuccess( "Delete Collection", "Successfully Deleted the Collection." );
        
        deleteEntityInGlobalArray( gCollectionEdited );
        kCollectionEdited = 0;
        
        show_collections();
    } else {
    	displayError( "Delete Collection", "Delete Failed - " + resObj.info );
    }
    
    closeStatusDialog();

}

function addTestToCollection( i ) {

	addEntityToEntity( 
			gCollectionEdited, 
			gArrayEntities[kTestType][i],
			'table_collection_subentities' );

}

function addCategoryToCollection( i ) {
	
	addEntityToEntity( 
			gCollectionEdited, 
			gArrayEntities[kCategoryType][i],
			'table_collection_subentities' );

}

/*
We do not directly use the index for the test here because there are possibilities that
the list of tests is modified due to new additions or deletions. Hence we cannot depend
on index in this case.
*/
function removeEntityFromCollection(entityId, entityType) {
	
	removeEntityFromEntity( 
			entityId, 
			3, // Collection type for Parent
			gEntitiesAddedNewlyToCollection, 
			entityType, 
			'table_subentities'   );
			

}
