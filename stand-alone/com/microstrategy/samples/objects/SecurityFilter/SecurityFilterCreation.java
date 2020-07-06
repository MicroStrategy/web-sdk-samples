package com.microstrategy.samples.objects.SecurityFilter;

/*
 * SDK sample
 * Purpose: create a new Security Filter object
 * 
 * Notes: The method createSecurityFilter can be used as a standalone function
 * 
 * Created by: Michael Scarfi
 * Creation date: 6/29/2020
 */

//SDK sample package
import com.microstrategy.samples.sessions.SessionManager;

//Import MicroStrategy Packages
import com.microstrategy.web.objects.WebExpression;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebFolder;
import com.microstrategy.web.objects.WebObjectSource;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.admin.users.WebMDSecurityFilter;
import com.microstrategy.webapi.EnumDSSXMLObjectTypes;
import com.microstrategy.webapi.EnumDSSXMLFolderNames;

public class SecurityFilterCreation {

	public static void main(String[] args) {	
		// Connectivity for the intelligence server
	    	final String INTELLIGENCE_SERVER_NAME = "localhost";
	    	final String PROJECT_NAME = "MicroStrategy Tutorial";
		final String MICROSTRATEGY_USERNAME = "Administrator";
		final String MICROSTRATEGY_PASSWORD = "";
		
		// Filter information
        	String filterName = "test filter";
		String expression = "[Employee Age]@ID=23";

	    	// Create our I-Server Session
		WebIServerSession session = SessionManager.getSessionWithDetails(INTELLIGENCE_SERVER_NAME, PROJECT_NAME, MICROSTRATEGY_USERNAME, MICROSTRATEGY_PASSWORD);
		
		// Execute the method to create the filter object
		createSecurityFilter(session, expression, filterName);
	}

	/**
	 * Takes a WebIServerSession object, String expression, and String filterName and creates a Security Filter with the given expression and name
	 * 
	 * @param session Object of type WebIServerSession
	 * @param expression String containing the expression for the Security Filter
	 * @param filterName String containing the name for the new filter
	 */
	public static void createSecurityFilter(WebIServerSession session, String expression, String filterName){
		// Create our Object Source, filter, and expression
		WebObjectSource webObjectSource = session.getFactory().getObjectSource();
	    	WebMDSecurityFilter wMDFilter = (WebMDSecurityFilter) webObjectSource.getNewObject(EnumDSSXMLObjectTypes.DssXmlTypeMDSecurityFilter );
		WebExpression wExpr = wMDFilter.getExpression();
		
		try {
			// Create WebFolder object that will allow us to save the filter in the Security filter folder
            		String folderID = webObjectSource.getFolderID(EnumDSSXMLFolderNames.DssXmlFolderNameSystemMDSecurityFilters);
            		WebFolder folder = (WebFolder) webObjectSource.getObject(folderID, EnumDSSXMLObjectTypes.DssXmlTypeFolder);

			// Populate our Filter's expression
			wExpr.populate(expression);
			
			// Save the new Security Filter
            		webObjectSource.save(wMDFilter, filterName, folder);
		} catch (WebObjectsException e) {
			e.printStackTrace();
		} 

	}

}
