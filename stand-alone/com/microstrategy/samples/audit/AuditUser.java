package com.microstrategy.samples.audit;

import com.microstrategy.samples.sessions.SessionManager;
import com.microstrategy.web.objects.WebClusterAdmin;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectSource;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.WebObjectsFactory;
import com.microstrategy.web.objects.admin.licensing.LicenseSource;
import com.microstrategy.web.objects.admin.licensing.LicensedUsers;
import com.microstrategy.web.objects.admin.licensing.NamedUserLicense;
import com.microstrategy.web.objects.admin.licensing.UserLicenseAudit;
import com.microstrategy.web.objects.admin.users.WebUserEntity;
import com.microstrategy.webapi.EnumDSSXMLObjectTypes;


//Standalone class to  present audit information
public class AuditUser {
  public static void main(String[] args) {
  
    // Set server connection information
    final String INTELLIGENCE_SERVER_NAME = "10.23.3.184";
    final String PROJECT_NAME = "MicroStrategy Tutorial";
    final String MICROSTRATEGY_USERNAME = "Administrator";
    final String MICROSTRATEGY_PASSWORD = "";

    // Create an IServer session
    WebIServerSession iServerSession = SessionManager.getSessionWithDetails(INTELLIGENCE_SERVER_NAME, PROJECT_NAME, MICROSTRATEGY_USERNAME, MICROSTRATEGY_PASSWORD);
    WebObjectsFactory factory = iServerSession.getFactory();
    WebObjectSource objectSource = factory.getObjectSource();

    try {
      WebUserEntity userEntity = (WebUserEntity) objectSource.getObject("C82C6B1011D2894CC0009D9F29718E4F",EnumDSSXMLObjectTypes.DssXmlTypeUser, true);
      LicenseSource licenseSource = factory.getLicenseSource();
      WebClusterAdmin webClusterAdmin = factory.getClusterAdmin();
      webClusterAdmin.setRequestTimeOutMillis(15 * 60 * 1000);

      //Execute license audit.
      UserLicenseAudit userLicenseAudit = licenseSource.auditUsers(userEntity);

      //Present unlicensed users
      showUnlicensedUsers(userLicenseAudit);

      //Present licensed users for every license type
      showLicensedUsers(licenseSource, userLicenseAudit);
    } catch (WebObjectsException | IllegalArgumentException e) {
      e.printStackTrace();
    }
}

  /**
   * Retrieve and print unlicensed users.
   * @param userLicenseAudit
   */
  static void showUnlicensedUsers(UserLicenseAudit userLicenseAudit) {

    try {
      LicensedUsers unLicensedUsers = userLicenseAudit.getUnlicensedUsers();
      System.out.println("Unlicensed users");
      System.out.println("----------------");

      for (int i = 0; i < unLicensedUsers.size(); i++) {
        System.out.println(unLicensedUsers.get(i).getDisplayName());
      }
    } catch (WebObjectsException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Retrieve and present licensed users organized by license type
   * @param licenseSource
   * @param userLicenseAudit
   */
  static void showLicensedUsers(LicenseSource licenseSource, UserLicenseAudit userLicenseAudit) {

    try {
      NamedUserLicense[] namedUserLicense = licenseSource.getNamedUserCompliance();
      System.out.println("Licensed users per license type");
      System.out.println("-------------------------------");

      for( int i = 0; i < namedUserLicense.length; i++) {
        //Get license name
        System.out.println("License type: " + namedUserLicense[i].getName());

        //Get licensed users for every license type.
        System.out.println("Licensed Users: " + userLicenseAudit.getLicensedUsers(namedUserLicense[i].getLicenseType()).size());

        //Get the maximum usage for every license type.
        System.out.println("Max Usage: " + namedUserLicense[i].getMaximumUsage() + "\n");
      }
    } catch (WebObjectsException e) {
      e.printStackTrace();
    }
  }
  
}
