
var gCurrentState = 0;

function startDataFetchStateMachine() {
    gCurrentState = 0;
    
    setTimeout(
            function() {
            	changeState();
            },
            500 );
    
    displayStatus( "Initialization", "Loading Tests, Please wait...", "Busy.gif" );
    
}

function changeState() {
    switch( gCurrentState ) {
        case 0:
            sendAsyncAjaxRequestToServer( '/GetTests',
                                    'GET',
                                    '',
                                    "handleFetchedData" );
            break;
            
        case 1:
            sendAsyncAjaxRequestToServer( '/GetCategories',
                                         'GET',
                                         '',
                                         "handleFetchedData" );
            
            break;
            
        case 2:
            sendAsyncAjaxRequestToServer( '/GetCollections',
                                         'GET',
                                         '',
                                         "handleFetchedData" );
            break;
            
        case 3:
            sendAsyncAjaxRequestToServer(
                                         '/GetPackages',
                                         'GET',
                                         '',
                                         "handleFetchedData" );
            break;
            
        default:
        	displayStatus( "Initialization", "Completed successfully", "Success.png" );
            
            // Show the fetched tests.
            show_tests();
            
            break;
    }
}

function handleFetchedData( resp ) {
    var nextEntityName = "";
    switch( gCurrentState ) {
        case 0:
            gArrayEntities[gCurrentState] = JSON.parse( resp ).tests;
            nextEntityName = "Categories";
            break;
            
        case 1:
        	gArrayEntities[gCurrentState] = JSON.parse( resp ).categories;
            nextEntityName = "Collections";
            break;
            
        case 2:
        	gArrayEntities[gCurrentState] = JSON.parse( resp ).collections;
            nextEntityName = "Packages";
            break;
            
        case 3:
        	gArrayEntities[gCurrentState] = JSON.parse( resp ).packages;
            nextEntityName = "Packages";
            break;
    }
    
    gCurrentState++;
    
    displayStatus( "Initialization", "Loading " + nextEntityName + ", Please wait...", "Busy.gif" );

    setTimeout(
            function() {
            	changeState();
            },
            500 );
    
}
