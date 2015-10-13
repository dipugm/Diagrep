
var gCategoryEdited = 0;

function show_categories() {
	
	// clear the right panel.
	clearRightPanel();
	
	var central_panel = document.getElementById( 'center_panel_layout' );
	
	var categories = gArrayEntities[ kCategoryType ];

	var body = "";
	body += "<div class='page_header' style='background-color:#FF6600;'>";
	body += "<div class='page_header_title' style='width: 80%; padding-left:10px;'>Available Categories</div>";

	body += "<div class='main_option_button' id='create_new' onclick='javascript:showViewForCategoryEditing(0)' style='width:17%;line-height:40px;' >Create New</div>";
	body += "</div>";

	body += "<table class='entity_table_header' border='0' cellspacing='0'>";
	body += "<tr>";
	body += "<th width='10%'>Sl No.</th>";
	body += "<th width='75%'>Name</th>";
	body += "<th width='15%'>Cost</th>";
	body += "</tr></table>";
	
	body += "<div class='page_contents'>";
	
	body += "<table class='entity_table_contents' border='0' cellspacing='0' id='table_categories'>";

	for( var i=0; i < categories.length; i++ ) {
		var cat = categories[i];
		body += "<tr";
		
		body += " class='odd_row'";
		body += " onclick=\"javascript:rowClickedForCategories(" + i + ")\">";
		body += "	<td width='10%' align='center'>" + (i+1) + "</td>";
		body += "	<td width='75%'>" + cat.name + "</td>";
		body += "	<td width='15%' style='text-align:right;padding-right:30px;'>" + cat.cost + "</td>";
		body += "</tr>";
	}
	
	body += "</table>";
	body += "</div>";
	
	central_panel.innerHTML = body;
}

function showViewForCategoryEditing( category ) {
	
	var name="";
	var cost="";
	var tests = new Array();
	var fModify=0;
	
	if( category != 0 ) {
		fModify = 1;
		
		name = category.name;
		cost = category.cost;
		tests = category.subEntitiesWithOrder;

		gCategoryEdited = getMutableCopy( category );
	} else {
		gCategoryEdited = {name:"", cost:"", id:-1, type: kCategoryType, subEntitiesWithOrder:[]};
	}
	
	gArrayEntityEdited[ kCategoryType ] 	= gCategoryEdited;
	
	body = "";
	body += "<table class='input_form_table'>";
	body += "<tr>";
	body += "	<td width='20%'>Name :</td>";
	
	if( fModify ){
		body += "	<td><b>" + name + "</b></td>";
	} else {
		body += "	<td><input type='text' style='width:300px;' class='entity_text_box' id='text_category_name'></td>";
	}
	
	body += "</tr>";
	body += "<tr>";
	
	body += "	<td width='20%' >Cost (Rs) :</td>";
	body += "	<td><input type='text' style='width:100px;' onkeypress='return isNumberKey(event)' class='entity_text_box' id='text_category_cost' value='" + cost + "'></td>";
	body += "</tr>";
	
	body += "<tr>";
	body += "	<td colspan='2'>Tests in this Category :</td>";
	body += "</tr>";
	
	body += "<tr>";
	body += "	<td colspan='2'>";
	body += "<div class='text_button' style='float:left; width:150px; color: white; background-color:#3399FF' onclick='javascript:showTestsForCategory()'>Add Tests</div>";
	body += "</td>";
	body += "</tr>";
	
	body += "<tr>";
	body += "	<td colspan='2'>";
	body += " <div style='width:400px; height:200px;overflow:auto; border:1px solid lightgray;'>";
	body += "<table class='table_sub_entity' id='table_category_subentities' cellPadding=0 cellSpacing=0>";
	
	// Display the tests in this category
	for( var i=0; i < tests.length; i++ ) {
		var test = tests[i];
		
		var rowId = "row_subentity_" + kTestType + "_" + test.id;
		
		body += "<tr class=\"sub_entity_table_row\" onclick=\"javascript:toggleRowSelection('table_category_subentities','" + rowId + "')\" ";
		body += "id=\"" + rowId + "\">";
		body += "<td width='40px'><img src='images/testsIcon.png'></td>";
		body += "<td style='color: darkblue;'>" + test.name + "</td>";
		body += "<td class=\"td_close_icon\" id=\"imageCell\" onclick=\"javascript:removeEntityFromEntity(gCategoryEdited, " + test.id + ",0, 'table_category_subentities')\">";
		body += "</td>";
		body += "</tr>";
	}
	
	body += "</table>";
	body += "</div>";
	body += "</td></tr>";
	
	if( fModify ) {
		body += "<tr>";
		body += "<td>";
		body += "<div class='text_button' style='width: 150px; text-align: center; color: white; background-color:#3399FF;' ";
		body += "onclick='javascript:modifyCategoryOnServer()'>Modify</div>";
		body += "</td>";
		body += "<td><div class='description_text'>Modify with new values</div></td>";
		body += "</tr>";
		body += "<tr>";
		body += "<td>";
		body += "<div class='text_button' id='delete_entity' style='width:150px;'";
		body += "onclick='javascript:deleteCategoryOnServer()'>Delete</div>";
		body += "</td>";
		body += "<td><div class='description_text'>Delete this category</div></td>";
		body += "</tr>";
	} else {
		body += "<tr>";
		body += "<td>";
		body += "<div class='text_button' style='height: 15px; width:150px; background-color:#3399FF; text-align: center; color: white;' ";
		body += "onclick='javascript:createCategoryOnServer()'>Create</div>";
		body += "</td>";
		body += "<td><div class='description_text'>Create a new category</div></td>";
		body += "</tr>";
	}
	
	body == "</table>";
	
	
	var rightPanel = document.getElementById( 'right_panel_layout' );
	rightPanel.innerHTML = body;
}

function rowClickedForCategories( rowNum ) {
	
	var category = gArrayEntities[ kCategoryType ][rowNum];
	showViewForCategoryEditing(category);
}

function showTestsForCategory() {
	
	popupChoices( gArrayEntities[ kTestType ], 'Tests', 'addTestToCategory' );
}

function addTestToCategory( i ) {

	addEntityToEntity( 
			gCategoryEdited, 
			gArrayEntities[kTestType][i],
			'table_category_subentities' );	
}

function createCategoryOnServer() {
	gCategoryEdited.name = document.getElementById('text_category_name').value;
	gCategoryEdited.cost = document.getElementById('text_category_cost').value;
	
	if( dataValidityCheck( gCategoryEdited.name, gCategoryEdited.cost ) == true ) {

		var queryParams = "type=" + kCategoryType;
		queryParams += "&name=" + gCategoryEdited.name;
		queryParams += "&cost=" + gCategoryEdited.cost;
		
		queryParams += "&subEntitiesWithOrder=" + getEntityIdArrayAsCommaSeparatedString( gCategoryEdited.tests );
		
        displayBusy( "Create new Category", "Please wait..." );

		sendAsyncAjaxRequestToServer("/Diagrep/CreateEntity",
                                     "POST",
                                     queryParams,
                                     "onCreateCategoryResponseFromServer");
	}
	
}

function onCreateCategoryResponseFromServer( resp ) {
    resObj = JSON.parse( resp );
    
    if( resObj.status == "success" ) {
        
    	displaySuccess( "Create new Category", "Successfully created the category." );

    	// Save the id
    	gCategoryEdited.id = parseInt( resObj.categoryId );
    	
        // Add the new category in the global array and re-render.
        gArrayEntities[ kCategoryType ].push( gCategoryEdited );
        gCategoryEdited	= 0;
        
        show_categories();
    } else {
        displayError( "Create new Category", "Failed to create the category : " + resObj.info );
    }

}

function modifyCategoryOnServer() {
	gCategoryEdited.cost = document.getElementById('text_category_cost').value;

	if( dataValidityCheck( "name", gCategoryEdited.cost ) == true ) {
        
		var queryParams = "type=" + kCategoryType;
		queryParams += "&id=" + gCategoryEdited.id;
		queryParams += "&cost=" + gCategoryEdited.cost;
		queryParams += "&subEntitiesWithOrder=" + getEntityIdArrayAsCommaSeparatedString( gCategoryEdited.subEntitiesWithOrder );

		displayBusy( "Modify Category", "Please wait..." );
		
		sendAsyncAjaxRequestToServer("/Diagrep/ModifyEntity",
                                     "POST",
                                     queryParams,
                                     "onModifyCategoryResposeFromServer");
		
	}
}

function onModifyCategoryResposeFromServer( resp ) {
    resObj = JSON.parse( resp );
    
    if( resObj.status == "success") {
    	displaySuccess( "Modify Category", "Successfully Modified the category." );
        
        // Replace the category object and re-render.
        replaceEntityInGlobalArray( gCategoryEdited );
        gCategoryEdited = 0;
        
        show_categories();
        
    } else {
    	displayError( "Modify Category", "Modify Failed - " + resObj.info );
    }
    
}

function deleteCategoryOnServer() {
	showQuestion( "Are you sure you want to delete this Category?",
                 "Confirm Delete",
                 onDeleteCategoryConfirmation );
}

function onDeleteCategoryConfirmation( option ) {
    if( option == "Yes" ) {
        displayBusy( "Delete a Category", "Please wait..." );
        
        var queryParams = "type=" + kCategoryType;
        queryParams += "&id=" + gCategoryEdited.id;
		
		sendAsyncAjaxRequestToServer("/Diagrep/RemoveEntity",
                                     "POST",
                                     queryParams,
                                     "onDeleteCategoryResponseFromServer");
		

    }
}

function onDeleteCategoryResponseFromServer( resp ) {
    
    resObj = JSON.parse( resp );
    
    if( resObj.status == "success" ) {
    	displaySuccess( "Delete Category", "Successfully Deleted the category." );
        
        // Removed the category from global array and re-render.
        deleteEntityInGlobalArray( gCategoryEdited );
        gCategoryEdited	= 0;
        
        show_categories();
        
    } else {
    	displayError( "Delete Category", "Delete Failed - " + resObj.info );
    }
    
}

/*
We do not directly use the index for the test here because there are possibilities that
the list of tests is modified due to new additions or deletions. Hence we cannot depend
on index in this case.
*/
function removeTestsFromCategory(testid) {
	
	removeEntityFromEntity( 
			testid, 
			2, // Category type as parent
			gTestsAddedNewlyToCategory, 
			1, // Test type
			'table_subentity_tests'   );
			

}
