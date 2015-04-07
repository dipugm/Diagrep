

var prevSelectedMenuButton = "panel_button_tests";

var gArrayEntityEdited=[0,0,0,0];

var gParamsForFunction=0;

var gCurrentSelectedSubEntity;

function getMousePosition(e) 
{ 
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

function initializeDocument() {
	document.onmousemove = getMousePosition;
	
    startDataFetchStateMachine();
	
}

function toolbar_button_clicked( type ) {
	var but = 'panel_button_' + type;
	
	if( prevSelectedMenuButton != 0 ) {
		document.getElementById( prevSelectedMenuButton ).className = 'panelbar_button';
	}
	
	document.getElementById( but ).className = 'panelbar_button_selected';
	
	/* Execute the handler function for this toolbar button */
	window['show_' + type]();
	
	prevSelectedMenuButton = but;
	
	// Clear the right panel.
	clearRightPanel();
	
}

function clearRightPanel() {
	// Clear the right panel if it is showing anything.
	var rightPanel = document.getElementById( 'right_panel_layout' );
	rightPanel.innerHTML = "<br>";
	
	gCurrentSelectedSubEntity	= 0;
}

function getEntityIdArrayAsCommaSeparatedString( arr ) {
	if( arr == null ) {
		return "";
	}
	
	var idArray = [];
	for( var i=0; i < arr.length; i++ ) {
		idArray.push( arr[i].id );
	}
	var resultString = idArray.join();
	
	return resultString;
}

function dataValidityCheck(name,cost) {
	
	msg = "";
	if( name == "" ) {
		msg += "* Name cannot be blank\n";
	}
	if( cost == "" ) {
		msg += "* Cost cannot be blank\n";
	}
	
	if( msg != "" ) {
		msg = "Please see below for more information.\n\n" + msg;
		showError( msg, "Invalid input"  );
		return false;
	}
	
	return true;
	
}


/*
 * Status display methods
 */
function displayStatus( task, result, icon ) {
	status_task = document.getElementById('status_main_task');
	status_task.innerHTML = task;
	
	status_result = document.getElementById('status_result');
	status_result.innerHTML = result;
	
	status_image = document.getElementById('status_image');
	status_image.setAttribute('src', "images/" + icon);
}

function displayBusy( task, result ) {
	displayStatus( task, result, "Busy.gif" );
}

function displaySuccess( task, result ) {
	displayStatus( task, result, "Success.png" );
}

function displayError( task, result ) {
	displayStatus( task, result, "Failed.png");
}

/*
 * Entity manipulation methods
 */
function addEntityToEntity( parentEntity, subEntity, subEntityTableName ) {
	
	// If we are in modify mode, we should send out a request immediately to 
	// add the test to this category.
	var subEntityTable = document.getElementById( subEntityTableName );
	
	var subEntityTableInsertIndex = -1;
	if( gCurrentSelectedSubEntity != 0 ) {
		var rowSelected = document.getElementById( gCurrentSelectedSubEntity );
		
		if( rowSelected != null ) {
			subEntityTableInsertIndex = rowSelected.rowIndex;
		}
		
	}
	
	if( subEntityTableInsertIndex == -1 ) {
		// We insert the new entity at the end.
		parentEntity.subEntitiesWithOrder.push( subEntity ); 
	} else {
		// We insert the new entity at the specified index.
		parentEntity.subEntitiesWithOrder.splice( subEntityTableInsertIndex, 0, subEntity );
	}
	
	// Add the test into the tests table.
	var row = subEntityTable.insertRow( subEntityTableInsertIndex );
	var newRowId = "row_subentity_" + subEntity.type + "_" + subEntity.id;
	
	row.className = 'sub_entity_table_row';
	row.id = newRowId;
	row.onclick = function( event ) {
		toggleRowSelection( subEntityTableName, newRowId );
	};
	
	var body = "";
	
	var imageName = getImageForEntityType( subEntity.type );
	if( imageName != "" ) {
		body += "<td><img src='images/" + imageName + "' height='24' width='24'></td>"; 
	}
	body += "<td style=\"color: darkblue;\">" + subEntity.name + "</td>";
	body += "<td class=\"td_close_icon\" id=\"imageCell\" onclick=\"javascript:removeEntityFromEntityWithId(" + parentEntity.id;
	body += "," + parentEntity.type + "," + subEntity.id + "," + subEntity.type + ", '" + subEntityTableName + "')\">";
	body += "</td>"; 
	row.innerHTML = body;
	
}

function removeEntityFromEntityWithId( parentEntityId, parentEntityType, subEntityId, subEntityType, subEntityTableName ) { 

	var parentEntity = gArrayEntityEdited[ parentEntityType ];
	
	removeEntityFromEntity( parentEntity, subEntityId, subEntityType, subEntityTableName );
}

function removeEntityFromEntity( parentEntity, subEntityId, subEntityType, subEntityTableName ) {
	
    gParamsForFunction = 0;
	showQuestion("Are you sure you want to remove the selected entity?",
                 "Confirm Remove",
                 onRemoveOption );
	
    gParamsForFunction = {
            "parentEntity":parentEntity,
            "subEntityTableName":subEntityTableName,
            "subEntityId" : subEntityId,
            "subEntityType" : subEntityType};
    
		
}

function onRemoveOption( option ) {
    if( option == "Yes" ) {
        
        parentEntity = gParamsForFunction["parentEntity"];
        subEntityTableName = gParamsForFunction["subEntityTableName"];
        subEntityId = gParamsForFunction["subEntityId"];
        subEntityType = gParamsForFunction["subEntityType"];
        
        var subEntityTable = document.getElementById( subEntityTableName );
                
        var subEntityIndex = -1;
        for( var i=0; i < parentEntity.subEntitiesWithOrder.length; i++ ) {
        	var se = parentEntity.subEntitiesWithOrder[i];
        	
        	if( (se.type == subEntityType) && (subEntityId == se.id) ) {
        		subEntityIndex = i;
        		break;
        	}
        	
        }
        
        // remove the entity from the sub entities array.
        parentEntity.subEntitiesWithOrder.splice( subEntityIndex, 1 );
        
        // remove the row for this test from the table.
        subEntityTable.deleteRow( subEntityIndex );
    }
}

function getSubEntitiesOrderAsCommanSeparatedString( entity ) {
	var orderString = "";
	// Is it a category, collection or a package?
	if( entity.propertyIsEnumerable( "subEntitiesWithOrder" ) ) {
		
		for( var i=0; i < entity.subEntitiesWithOrder.length; i++ ) {
			var subEnt = entity.subEntitiesWithOrder[i];

			if( i > 0 ) {
				orderString += ",";
			}
			
			orderString += subEnt.type + "-" + subEnt.id;
		}
	}
	
	return orderString;
}

function replaceEntityInGlobalArray( entity ) {
	
	var entities = gArrayEntities[ entity.type ];
	for( var i=0; i < entities.length; i++ ) {
		var ent = entities[i];
		
		if( ent.id == entity.id ) {
			entities[i] = entity;
			break;
		}
	}
}

function deleteEntityInGlobalArray( entity ) {
	
	var entities = gArrayEntities[ entity.type ];
	for( var i=0; i < entities.length; i++ ) {
		var ent = entities[i];
		
		if( ent.id == entity.id ) {
			entities.splice(i,1);
			break;
		}
	}
	
	// We also need to delete this from any higher types that might
	// contain this as sub entities.
	for( var iType=entity.type+1; iType < gArrayEntities.length; iType++ ) {
		var ents = gArrayEntities[ iType ];
		
		for( var iEnt=0; iEnt < ents.length; iEnt++ ) {
			var higherEntity = ents[ iEnt ];
			
			for( var iSubEnt=0; iSubEnt < higherEntity.subEntitiesWithOrder.length; iSubEnt++ ) {
				var subEntity 	= higherEntity.subEntitiesWithOrder[ iSubEnt ];
				
				if( (subEntity.type == entity.type) && (subEntity.id == entity.id) ) {
					higherEntity.subEntitiesWithOrder.splice( iSubEnt, 1);
					break;
				}
			}
		}
	}
}

function toggleRowSelection( tableName, rowId ) {
	
	var tab = document.getElementById( tableName );
	var rowSelected = document.getElementById( rowId );
	
	for( var i=0; i < tab.rows.length; i++ ) {
		var row = tab.rows[ i ];
		row.className = "sub_entity_table_row";
	}
	
	// If the user clicked the same row, we reset it otherwise we
	// select it.
	if( gCurrentSelectedSubEntity != rowId ) {
		gCurrentSelectedSubEntity = rowId;
		rowSelected.className = "sub_entity_table_row_selected";
	} else {
		gCurrentSelectedSubEntity = 0;
	}
	
}

