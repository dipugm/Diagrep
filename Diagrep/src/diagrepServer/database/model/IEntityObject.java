package diagrepServer.database.model;

import java.util.ArrayList;

import diagrepServer.database.core.ModelObject;

public interface IEntityObject {

	/* 
	 * Each entity is supposed to have a cost associated with it but some
	 * entities might have to provide a computational value.
	 */
	public double getCost();
	
	/*
	 * Returns the entity or entities as HTML that can be used directly in
	 * a report.
	 */
	public String getAsHtml();
	
	/*
	 * Returns the entity and its sub entities as JSON. 
	 */
	public String getAsJson();
	
	/*
	 * Returns only the tests contained in the model object. Will need to
	 * include the tests in the complete hierarchy.
	 */
	public ArrayList<ModelObject> getContainedTests();
}
