package com.microstrategy.samples.searching;

/*
 * SDK sample
 * Purpose: to find a user's ID using their name
 * 
 * Notes: The method searchObject can be used as a standalone function
 * The searchObject method will only search for one type of object as defined by int OBJECT_TYPE (see notes for values)
 * 
 * Created by: Michael Scarfi
 * Creation date: 5/29/2020
 */

//SDK sample package
import com.microstrategy.samples.sessions.SessionManager;

//Import MicroStrategy Packages
import com.microstrategy.webapi.*;
import com.microstrategy.web.objects.*;

public class ObjectSearchByName {

	public static void main(String[] args) {
	    // Connectivity for the intelligence server
	    final String INTELLIGENCE_SERVER_NAME = "sup-w-001643.labs.microstrategy.com";
	    final String PROJECT_NAME = "MicroStrategy Tutorial";
	    final String MICROSTRATEGY_USERNAME = "Administrator";
	    final String MICROSTRATEGY_PASSWORD = "";
	    
	    /**
	      * This determines the type of object for the search, currently it is set to attribute
	      * Changing DSSXML will allow you to choose what type of object the search will return
	      * Values for this can be found at https://lw.microstrategy.com/msdz/MSDL/GARelease_Current/docs/ReferenceFiles/reference/com/microstrategy/webapi/EnumDSSXMLObjectSubTypes.html
	      */
	    final int OBJECT_TYPE = EnumDSSXMLObjectSubTypes.DssXmlSubTypeAttribute;
	    
	    // Object Name for search
	    String searchName = "test";
	    
	    // Create our I-Server Session
	    WebIServerSession session = SessionManager.getSessionWithDetails(INTELLIGENCE_SERVER_NAME, PROJECT_NAME, MICROSTRATEGY_USERNAME, MICROSTRATEGY_PASSWORD);
	    
	    // Find User and output search results
	    WebObjectInfo[] objects = searchObjectsOfTypeByName(session, searchName, OBJECT_TYPE);
	    
	    if(objects == null) {
	    	System.out.println("No objects found.");
	    } else {
	    	// As multiple objects can have the same name, this iterates through returned values and displays each
		for (int i = 0; i < objects.length; i++) {
			System.out.println("Object ID: " + objects[i].getID());
		}
	    }
	    
	    
	}
	 
	/**
	 * Takes a WebIServerSession object, and a String name and searches for the IDs of objects that matches the name provided
	 * 
	 * @param session Object of type WebIServerSession
	 * @param searchName String containing the search String
	 * @param objectType int containing the int value of the object type for the search
	 * @return WebObjectInfo[] containing the objects, if none are found the string will be of value null
	 */
	private static WebObjectInfo[] searchObjectsOfTypeByName(WebIServerSession session, String searchName, int objectType) {
		//Create and populate Factory
		WebObjectSource webObjectSource = session.getFactory().getObjectSource();
		
		//Create blank user and the search object
		WebObjectInfo object = null;
		WebSearch search = webObjectSource.getNewSearchObject();
		
		//Create empty String to store search results
		WebObjectInfo[] objectIDs = null;
		
		try {
			//Populate search parameters
			search.setNamePattern(searchName);
			search.setSearchFlags(search.getSearchFlags() | EnumDSSXMLSearchFlags.DssXmlSearchNameWildCard);
			
			//asynchronized search
			search.setAsync(false);

			//Set Object type for search
			search.types().add(new Integer(objectType));

			//search on repository
			search.setDomain(EnumDSSXMLSearchDomain.DssXmlSearchDomainRepository);
			search.submit();
			
			//Create WebFolder object to store search results
			WebFolder f = search.getResults();
			
			//Finding size of the search results since this value is used multiple times.
			int size = f.size();

			//iterate through search results and add to objectIDs
			if (size > 0) {
				objectIDs = new WebObjectInfo[size];
				for(int i = 0; i < f.size();i++) {
					object = (WebObjectInfo) f.get(i);
					object.populate();
					objectIDs[i] = object;
				}
				return objectIDs;
			}
			
		} catch (Exception e) {
			//print error message to console if an exception occurs.
			System.out.println(e.getMessage());
		}
		//returning null object if no values found
		return objectIDs;
		
	}

}
