package com.microstrategy.samples.searching;

/*
 * SDK sample
 * Purpose: to find a user's ID using their name
 * 
 * Notes: The method searchUser can be used as a standalone function.
 * 
 * Based off of KB13809 and modyified to return userID and enhanced for better functionality
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
	    final String intelligenceServerName = "sup-w-001643.labs.microstrategy.com";
	    final String projectName = "MicroStrategy Tutorial";
	    final String microstrategyUsername = "Administrator";
	    final String microstrategyPassword = "";
	    
	    // User Name for search
	    final String searchName = "administrator";
	    
	    // Create our I-Server Session
	    final WebIServerSession session = SessionManager.getSessionWithDetails(intelligenceServerName, projectName, microstrategyUsername, microstrategyPassword);
	    
	    // Find User and output search results
	    String UserID = searchUser(session, searchName);
	    System.out.println("User ID: " + UserID);
	    
	}
	 
	/**
	 * Takes a WebIServerSession object, and a String name and searches for the User ID of the user that matches the name provided
	 * 
	 * @param session Object of type WebIServerSession
	 * @param searchName String containing the search String
	 * @return User ID if object is found, "User not found" if the user is not found
	 */
	private static String searchUser(WebIServerSession session, String searchName) {
		//Create and populate Factory
		WebObjectSource webObjectSource = session.getFactory().getObjectSource();
		
		//Create blank user and the search object
		WebUser user = null;
		WebSearch search = webObjectSource.getNewSearchObject();
		
		//Create empty String to store search results
		String userID = null;
		
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
				userID = user.getID();
			}
			
		} catch (Exception e) {
			//print error message to console if an exception occurs.
			System.out.println(e.getMessage());
		}
		
		//Checking to see if user was found
		if(userID != null) {
			//Returning User ID if the object was found
			return userID;
		}else {
			//Returning error message if the User is not found.
			return "User not found";
		}
	}
	    
}
