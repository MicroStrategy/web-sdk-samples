/* FOREWORD:

The code enables searching through a search directory for folders that can either be either visible-only or hidden.
The change is only a matter of commenting out the object flag on search: EnumDSSXMLObjectFlags.DssXmlObjectFindHidden
If ObjectFindHidden is on - it find all files (including the hidden ones) If the flag is not there - only visible
folders will be returned. Later it also lists the folders with all the details. 

You can also use it for searching for other types of files (apart from folders) by changing the flag on searchTypes: 
search.types().add(EnumDSSXMLObjectTypes.DssXmlTypeFolder. 

The information that you have to fill in is: 
1. The package
2. The details of te I-Server session
3. The GUID of the folder that you want to search through - the sample one used is the Public Objects folder.

Apart from those details the code is ready to run.

Made with love by Edgar, based on [278906/DE124645/KB16830]
Did not use object.populate() but search.getResults() instead. Chose to search folders as needed it for practical purposes.

Find the code below, and ENJOY. */

package com.edgar.standalone;

import com.microstrategy.web.objects.WebFolder;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectSource;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.WebObjectsFactory;
import com.microstrategy.web.objects.WebSearch;
import com.microstrategy.webapi.EnumDSSXMLApplicationType;
import com.microstrategy.webapi.EnumDSSXMLObjectFlags;
import com.microstrategy.webapi.EnumDSSXMLObjectTypes;
import com.microstrategy.webapi.EnumDSSXMLSearchFlags;

public class FirstClass {

private static WebObjectsFactory factory = WebObjectsFactory.getInstance();;	
private static WebIServerSession serverSession = null;

public static void main(String[] args) throws WebObjectsException{ 

// Starting the session

System.out.println("Initiating the session.");

serverSession = factory.getIServerSession();
serverSession.setServerName("SERVER"); 
serverSession.setServerPort(0);
serverSession.setProjectName("MicroStrategy Tutorial");
serverSession.setLogin("Administrator"); 
serverSession.setPassword(""); 
serverSession.setApplicationType(EnumDSSXMLApplicationType.DssXmlApplicationCustomApp);

System.out.println("Performing the search.");

WebObjectSource source = serverSession.getFactory().getObjectSource();
WebSearch search = source.getNewSearchObject();

String folderGUID = "98FE182C2A10427EACE0CD30B6768258"; 

search.setSearchRoot(folderGUID);
search.setFlags(EnumDSSXMLSearchFlags.DssXmlSearchNameWildCard | EnumDSSXMLSearchFlags.DssXmlSearchRootRecursive | EnumDSSXMLObjectFlags.DssXmlObjectFindHidden);
search.setAsync(false);

search.types().add(EnumDSSXMLObjectTypes.DssXmlTypeFolder);
search.submit();	

System.out.println("Search submitted.");

WebFolder folderWithResults = search.getResults();
System.out.println("Filtering the results.");

int size = 0;
size = folderWithResults.size();

System.out.println("All of the folders from within the search directory: ");

for (java.util.Enumeration e = folderWithResults.elements(); e.hasMoreElements();) {
    System.out.println(e.nextElement());
   }

System.out.println("Size of the folder is: " + size);

	}
}
