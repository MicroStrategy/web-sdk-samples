package com.microstrategy.samples.usergroups;

import com.microstrategy.samples.sessions.SessionManager;
import com.microstrategy.web.objects.WebAccessControlList;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectInfo;
import com.microstrategy.web.objects.WebObjectSource;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.webapi.EnumDSSXMLObjectFlags;
import com.microstrategy.webapi.EnumDSSXMLObjectTypes;

public class RemoveUserGroupACL {


    public static void main(String[] args) throws WebObjectsException {

        // Session information
        final String INTELLIGENCE_SERVER = "SUP-W-003423";
        final String PROJECT = "MicroStrategy Tutorial";
        final String USERNAME = "Administrator";
        final String PASSWORD = "1234";
        // This is the GUID of the User Group that will be modified
        final String USERGROUP_GUID = "0644EFFC46A7628EA869339EA85E751A";
        // Name of Trustee to be removed
        final String TRUSTEE_NAME = "Everyone";

        // Initialize the session to the Intelligence Server
        WebIServerSession session = SessionManager.getSessionWithDetails(INTELLIGENCE_SERVER, PROJECT, USERNAME, PASSWORD);
        WebObjectSource objSource = getObjectSource(session);
        // Obtains the User Group (WebObjectInfo) based off of the USERGROUP_GUID
        WebObjectInfo userGroupInfo = objSource.getObject(USERGROUP_GUID, EnumDSSXMLObjectTypes.DssXmlTypeUser, true);
        // Prints ACLs for a before / after review to see what changes were made
        System.out.println("Printing full list of ACLs prior to any changes");
        printACL(userGroupInfo);
        // Removes the designated TRUSTEE_NAME as an ACL from the USERGROUP_GUID
        removeACL(objSource, userGroupInfo, TRUSTEE_NAME);

        // Prints ACLs for a before / after review to see what changes were made
        System.out.println("\nPrinting full list of ACLs after changes have been pushed to the Intelligence Server");
        printACL(userGroupInfo);
        System.out.println("\nEOF");
    }

    /**
     * Obtains the WebObjectSource which is used to obtain the User Group as well as save the changes to the User Group
     * 
     * @param session
     * @return
     */
    private static WebObjectSource getObjectSource(WebIServerSession session) {
        WebObjectSource objSource = session.getFactory().getObjectSource();
        int flags = objSource.getFlags();
        objSource.setFlags(flags | EnumDSSXMLObjectFlags.DssXmlObjectTotalObject);
        return objSource;
    }

    /**
     * Searches through the Access Control List of the UserGroup and will remove any ACL trustees which match the TRUSTEE_NAME This will also denote
     * if any changes are queued up Until the object source is used to save the userGroupInfo, no changes will be pushed to the Intelligence Server
     * 
     * @param userGroupInfo
     * @param trusteeName
     * @throws WebObjectsException
     */
    private static void removeACL(WebObjectSource objSource, WebObjectInfo userGroupInfo, String trusteeName) throws WebObjectsException {
        System.out.println("Beginning process to remove the Trustee called " + trusteeName);
        boolean changed = false;
        WebAccessControlList acl = userGroupInfo.getSecurity().getACL();
        for (int i = 0; i < acl.size(); i++) {

            // Checks the Trustee Name and if it matches Everyone, removes the ACL from the list
            String currentName = acl.get(i).getTrustee().getDisplayName();
            if (currentName.equals(trusteeName)) {
                acl.remove(i);
                changed = true;
            }
        }
        if (changed) {
            objSource.save(userGroupInfo);
            System.out.println("ACL will be removed");
        } else {
            System.out.println("No ACL will be changed");
        }
        System.out.println("removeACL process has completed");
    }

    /**
     * Searches through the Access Control List of the UserGroup and prints all results
     * 
     * @param userGroupInfo
     */
    private static void printACL(WebObjectInfo userGroupInfo) {
        System.out.println("----------------------------------------");
        WebAccessControlList acl = userGroupInfo.getSecurity().getACL();
        for (int i = 0; i < acl.size(); i++) {
            System.out.println(acl.get(i).getTrustee().getDisplayName());
        }
        System.out.println("----------------------------------------");
    }
}
