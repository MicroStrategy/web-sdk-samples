package com.microstrategy.samples.users;

import com.microstrategy.samples.sessions.SessionManager;
import com.microstrategy.web.beans.BeanFactory;
import com.microstrategy.web.beans.UserBean;
import com.microstrategy.web.beans.WebBeanException;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.admin.users.WebNTLoginInfo;
import com.microstrategy.web.objects.admin.users.WebUser;


/**
 * Sample to create MicroStrategy user. This example demonstrates how to create a standard MicroStrategy user and a user which uses Windows NT authentication, which has special requirements.
 *
 */
public class CreateUser {

  public static void main(String[] args) {
    // Connectivity for the intelligence server
    final String INTELLIGENCE_SERVER_NAME = "10.23.1.249";
    final String PROJECT_NAME = "MicroStrategy Tutorial";
    final String MICROSTRATEGY_USERNAME = "Administrator";
    final String MICROSTRATEGY_PASSWORD = "";

    //Standard User info.
    String loginName= "testUser";
    String fullName = "Test User's fullName";

    //NT User info.
    String NTloginName= "NTtestUser";
    String NTfullName = "NT Test User's fullName";
    String NTaccountName = "NTaccountName";

    // Create IServer Session
    WebIServerSession session = SessionManager.getSessionWithDetails(INTELLIGENCE_SERVER_NAME, PROJECT_NAME, MICROSTRATEGY_USERNAME, MICROSTRATEGY_PASSWORD);
  
    //Create standard MicroStrategy user
    WebUser standardUser = createStandardUser(session, loginName, fullName);
  
    //Create MicroStrategy NTUser
    WebUser NTUser = createNTUser(session, NTloginName, NTfullName, NTaccountName);
  }

  /**
   * Create user bean as a base to create users
   * @param session - WebIServerSession - WebIServerSession object to use
   * @return - userBean - UserBean to create a user
   */
  public static UserBean createUserBeanInstance(WebIServerSession session) {
    System.out.println("Starting user creation workflow.");
    UserBean userBean = null;
    userBean = (UserBean) BeanFactory.getInstance().newBean("UserBean");
    userBean.setSessionInfo(session);
    userBean.InitAsNew();
    return userBean;
  }

  /**
   * Creates MicroStrategy standard user
   * @param session - WebIServerSession - WebIServerSession object to use
   * @param loginName - String - Login name for the new user
   * @param fullName - String - Full name for the new user
   * @return - WebUser - Webuser object with the information for the newly created user
   */
  public static WebUser createStandardUser(WebIServerSession session, String loginName, String fullName) {
    UserBean userBean = createUserBeanInstance(session);
    WebUser standardUser = null;
    
    try {
      standardUser = (WebUser) userBean.getUserEntityObject();
      standardUser.setLoginName(loginName);
      standardUser.setFullName(fullName);
      userBean.getObjectInfo().setDescription("User created using MicroStrategy Web SDK.");
      userBean.save();
      System.out.println("Base user created successfully.");
    } catch (WebBeanException e) {
      e.printStackTrace();
    }
    
    return standardUser;
  }

  /**
   * Creates MicroStrategy user with NT Authentication.
   * 
   * Pre-requisites:
   * - NTAccountName must be an existing NT account name to map user to.
   * To set user's NT-specific properties the dll "MBNTVSUP.dll" is invoked. This dll is located under "Common Files/MicroStrategy/" directory.
   * - This code must be executed in Windows environments or having the mentioned dll available under Java PATH variable.  
   * @param session - WebIServerSession - WebIServerSession object to use
   * @param loginName - String - Login name for the new user
   * @param fullName - String - Full name for the new user
   * @param NTaccountName - String - NT account name to map user to
   * @return - WebUser - WebUser object with the information for the newly created user
   */
  public static WebUser createNTUser(WebIServerSession session, String loginName, String fullName, String NTaccountName) {
    UserBean userBean =createUserBeanInstance(session);
    WebUser NTUser = null;
    
    try {
      NTUser = (WebUser) userBean.getUserEntityObject();
      WebNTLoginInfo webNTLoginInfo = NTUser.getNTLoginInfo();
      //Mapping NT account name to user.
      webNTLoginInfo.setWindowsAccountName(NTaccountName);
      NTUser.setLoginName(loginName);
      NTUser.setFullName(fullName);
      userBean.getObjectInfo().setDescription("User created using MicroStrategy Web SDK - NT Authentication.");
      userBean.save();
      System.out.println("NT user created sucessfully.");
    } catch (WebBeanException | WebObjectsException e) {
      e.printStackTrace();
    }
    
    return NTUser;
  }
}
