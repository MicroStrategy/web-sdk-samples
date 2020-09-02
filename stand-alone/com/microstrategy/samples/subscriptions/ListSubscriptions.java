package com.microstrategy.samples.subscriptions;

import com.microstrategy.samples.sessions.SessionManager;
import com.microstrategy.web.objects.SimpleList;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.WebSubscription;
import com.microstrategy.web.objects.WebSubscriptionsSource;

public class ListSubscriptions {
  public static void main(String[] args) {		

  // Set server connection information
  String intelligenceServerName = "10.23.1.124"; 
  String projectName = "MicroStrategy Tutorial"; 
  String microstrategyUsername =  "administrator";
  String microstrategyPassword = "";

  // Create an IServer session
  WebIServerSession iServerSession = SessionManager.getSessionWithDetails(intelligenceServerName, projectName, microstrategyUsername, microstrategyPassword);

  //Retrieve subscriptions from server
  SimpleList subscriptionsList = retrieveSubscriptions(iServerSession);

  //printSubscriptionContent(subscriptionsList);
  printSubscriptionsContent(subscriptionsList);
  
  }
  /**
   * 
   * @param subscriptionsList - List of subscriptions to print the information.
   */
  private static void printSubscriptionsContent(SimpleList subscriptionsList) {
    for( int i = 0 ; i < subscriptionsList.size(); i++) {
      WebSubscription subscription = (WebSubscription) subscriptionsList.item(i);
      printSubscriptionContent(subscription);
    }
  }
  
  /**
   * Returns a list of subscriptions.
   * @param iServerSession
   * @return SimpleList - List of subscriptions
   */
  private static SimpleList retrieveSubscriptions(WebIServerSession iServerSession) {
    
    WebSubscriptionsSource subscriptionsSource = iServerSession.getFactory().getSubscriptionsSource();
    SimpleList subscriptionsList = null;
    
    try {
      subscriptionsList = subscriptionsSource.getSubscriptions();
    } 
    catch (WebObjectsException e) {
      e.printStackTrace();
    }
    
    return subscriptionsList;
  }
  
  /**
   * Print a specific subscription's content
   * @param subscription - Subscription containing the info to print
   */
  private static void printSubscriptionContent(WebSubscription subscription) {
    try {
      System.out.println("Subscription name: " + subscription.getContent().getName() + "\n");
      System.out.println("Subscription ID: " + subscription.getContent().getID() + "\n");
      System.out.println("-----\n\n");
    } 
    catch (WebObjectsException e) {
      e.printStackTrace();
    }
  }
  
}
