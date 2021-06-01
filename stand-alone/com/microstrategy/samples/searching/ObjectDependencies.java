package com.microstrategy.samples.searching;

import com.microstrategy.samples.sessions.SessionManager;
import com.microstrategy.web.objects.WebFolder;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectInfo;
import com.microstrategy.web.objects.WebObjectSource;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.WebObjectsFactory;
import com.microstrategy.web.objects.WebSearch;
import com.microstrategy.webapi.EnumDSSXMLObjectSubTypes;
import com.microstrategy.webapi.EnumDSSXMLSearchDomain;
import com.microstrategy.webapi.EnumDSSXMLSearchFlags;

/**
 * Sample code using the WebSearch Object to find:
 * 1.- Dependent objects from an object given its ID. 
 * 2.- Component objects from an object given its ID
 */

public class ObjectDependencies {

  public static void main(String[] args) {
    // Set server connection information
    final String INTELLIGENCE_SERVER_NAME = "10.23.1.249";
    final String PROJECT_NAME = "MicroStrategy Tutorial";
    final String MICROSTRATEGY_USERNAME = "Administrator";
    final String MICROSTRATEGY_PASSWORD = "";

    //Create IServer session
    WebIServerSession iServerSession = SessionManager.getSessionWithDetails(INTELLIGENCE_SERVER_NAME, PROJECT_NAME, MICROSTRATEGY_USERNAME, MICROSTRATEGY_PASSWORD);
    
    //ID and object type of the object to find dependencies
    String objectId = "CD57B0474B257586F19627BF085C794C";
    int objectType = EnumDSSXMLObjectSubTypes.DssXmlSubTypeReportCube;
    
    //Find components of objectId
    WebFolder componentObjects = getComponents(iServerSession, objectId, objectType);
    System.out.println("Printing components for object: " + objectId);
    showObjectsInfo(componentObjects);

    //Find dependent objects of objectId
    WebFolder dependentObjects = getDependentObjects(iServerSession, objectId, objectType);
    System.out.println("Printing dependants from object: " + objectId);
    showObjectsInfo(dependentObjects);
  }
  
  /**
   * Returns a WebFolder object which contains all components of the object given by objectId which object type is objectType.
   * 
   * @param iServerSession - WebIServerSession object to use.
   * @param objectId - String - ID of the object to find components
   * @param objectType - String - Type or subtype of the object to find components. This is given by EnumDSSXMLObjectTypes or EnumDSSXMLObjectSubTypes
   * @return - WebFolder - Folder object which contains the requested objects
   */
  public static WebFolder getComponents(WebIServerSession iServerSession, String objectId, int objectType ){
    WebFolder dependentObjects = null;
    WebObjectsFactory factory = iServerSession.getFactory();
    WebObjectSource objectSource = factory.getObjectSource();
    WebSearch webSearch = objectSource.getNewSearchObject();
    webSearch.setDomain(EnumDSSXMLSearchDomain.DssXmlSearchDomainProject);
    WebObjectInfo object;

    try {
      object = objectSource.getObject(objectId, objectType);
      webSearch.usedBy().add(object);
      webSearch.setAsync(false);
      webSearch.submit();
      dependentObjects = webSearch.getResults();
    } catch (WebObjectsException | IllegalArgumentException e) {
      e.printStackTrace();
    }
    return dependentObjects;
  }
  
  /**
   * Returns a WebFolder object which contains all dependent objects of the object given by objectId which object type is objectType.
   * @param iServerSession - WebIServerSession object to use.
   * @param objectId - String - ID of the object to find components
   * @param objectType - String - Type or subtype of the object to find dependents. This is given by EnumDSSXMLObjectTypes or EnumDSSXMLObjectSubTypes
   * @return - WebFolder - Folder object which contains the requested objects
   */
  public static WebFolder getDependentObjects(WebIServerSession iServerSession, String objectId, int objectType ){
    WebFolder dependentObjects = null;
    WebObjectsFactory factory = iServerSession.getFactory();
    WebObjectSource objectSource = factory.getObjectSource();
    WebSearch webSearch = objectSource.getNewSearchObject();
    webSearch.setDomain(EnumDSSXMLSearchDomain.DssXmlSearchDomainProject);
    WebObjectInfo object;

    try {
      object = objectSource.getObject(objectId, objectType);
      webSearch.uses().add(object);
      webSearch.setAsync(false);
      webSearch.submit();
      dependentObjects = webSearch.getResults();
    } catch (WebObjectsException | IllegalArgumentException e) {
      e.printStackTrace();
    }
    return dependentObjects;
  }

  /**
   * Loops through WebFolder object and prints the name of each child object
   * @param objectsFolder - WebFolder - Folder containing the object to print their information
   */
  public static void showObjectsInfo(WebFolder objectsFolder) {
    System.out.println("#objects :" + objectsFolder.getChildCount());
    for (int index = 0 ; index < objectsFolder.size(); index++) {
      System.out.println(objectsFolder.get(index).getName());
    }
  }

}
