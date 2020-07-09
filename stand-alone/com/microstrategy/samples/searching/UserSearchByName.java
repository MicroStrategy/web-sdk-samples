package com.microstrategy.samples.searching;

/*
 * SDK sample
 * Purpose: to find a user's ID using their name
 * 
 * Created by: Michael Scarfi
 * Creation date: 5/29/2020
 */

//SDK sample package
import com.microstrategy.samples.sessions.SessionManager;

//Import MicroStrategy Packages
import com.microstrategy.webapi.*;
import com.microstrategy.web.objects.*;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.admin.users.WebUser;
import com.microstrategy.web.objects.WebFolder;
import com.microstrategy.web.objects.WebObjectSource;

public class UserSearchByName {

	public static void main(String[] args) {
		
	    // Connectivity for the intelligence server
	    final String INTELLIGENCE_SERVER_NAME = "sup-w-001643.labs.microstrategy.com";
	    final String PROJECT_NAME = "MicroStrategy Tutorial";
	    final String MICROSTRATEGY_USERNAME = "Administrator";
	    final String MICROSTRATEGY_PASSWORD = "";
	    
	    // User Name for search
	    String searchName = "administrator";
	    
	    // Create our I-Server Session
	    WebIServerSession session = SessionManager.getSessionWithDetails(INTELLIGENCE_SERVER_NAME, PROJECT_NAME, MICROSTRATEGY_USERNAME, MICROSTRATEGY_PASSWORD);
	    
	    // Find User and output users ID
	    WebUser user = searchUser(session, searchName);
	    if (user == null) {
	    	System.out.println("User not found");
	    }else {
	    	System.out.println("User ID: " + user.getID());
	    }
	    
	}
	 
	/**
	 * Takes a WebIServerSession object, and a String name and searches for the User ID of the user that matches the name provided
	 * 
	 * @param session Object of type WebIServerSession
	 * @param searchName String containing the search String
	 * @return WebUser object if the user is found, WebUser object with value of null if the user is not found
	 */
	private static WebUser searchUser(WebIServerSession session, String searchName) {
		//Create and populate Factory
		WebObjectSource webObjectSource = session.getFactory().getObjectSource();
		
		//Create blank user and the search object
		WebUser user = null;
		WebSearch search = webObjectSource.getNewSearchObject();
		
		try {
			//Populate search parameters
			search.setAbbreviationPattern(searchName);
			search.setSearchFlags(search.getSearchFlags() | EnumDSSXMLSearchFlags.DssXmlSearchAbbreviationWildCard);
			
			//asynchronized search
			search.setAsync(false);

			//search for user
			search.types().add(new Integer(EnumDSSXMLObjectSubTypes.DssXmlSubTypeUser));

			//search on repository
			search.setDomain(EnumDSSXMLSearchDomain.DssXmlSearchDomainRepository);
			search.submit();
			
			//Create WebFolder object to store search results
			WebFolder f = search.getResults();

			//since login id is unique identity, if found, the first one is the user
			if (f.size() > 0) {
				user = (WebUser) f.get(0);
				user.populate();
				return user;
			}
			
		} catch (Exception e) {
			//print error message to console if an exception occurs.
			System.out.println(e.getMessage());
		}
		
		//returning null value if user not found;
		return user;
	}
	    
}
