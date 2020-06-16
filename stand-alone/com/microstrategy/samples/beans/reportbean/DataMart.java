package com.microstrategy.samples.beans.reportbean;

import com.microstrategy.samples.sessions.SessionManager;
import com.microstrategy.web.beans.BeanFactory;
import com.microstrategy.web.beans.ReportBean;
import com.microstrategy.web.beans.WebBeanException;
import com.microstrategy.web.objects.EnumWebPromptType;
import com.microstrategy.web.objects.WebConstantPrompt;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.WebPrompt;
import com.microstrategy.web.objects.WebPrompts;
import com.microstrategy.web.objects.WebReportInstance;
import com.microstrategy.webapi.EnumDSSXMLExecutionFlags;

public class DataMart {

  public static void main(String[] args) {
    // Connectivity for the intelligence server
    String intelligenceServerName = "APS-TSIEBLER-VM";
    String projectName = "MicroStrategy Tutorial";
    String microstrategyUsername = "Administrator";
    String microstrategyPassword = "";
    
    // Create our I-Server Session
    WebIServerSession session = SessionManager.getSessionWithDetails(intelligenceServerName, projectName, microstrategyUsername, microstrategyPassword);
    
    // The GUID of the data mart report to use
    String reportID = "94FA0D694A2AE80676D21384BEF54BF8";
    
    executeDataMart(reportID, session);
    
    // The GUID of the data mart report with prompts to use
    String promptedReportID = "5FE1427B4F07AFCF7279FF9053BC5C4F";

    //Answer to the prompt.
    String promptAnswer = "01/06/2014";

    //Execute report Datamart when prompt is required.
    executeDataMartWithPrompt(promptedReportID, session, promptAnswer);
  }
  
  // Simple example to execute a data mart report, without any prompt requirements
  public static void executeDataMart(String reportID, WebIServerSession session) {
    ReportBean rb = (ReportBean)BeanFactory.getInstance().newBean("ReportBean");
    
    // Id of the object we wish to execute
    rb.setObjectID(reportID);
    
    // session used for execution
    rb.setSessionInfo(session);
    
    System.out.println("Executing Data Mart Report");
    
    // sets the execution flag to indicate that datamart should be executed
    rb.setExecutionFlags(EnumDSSXMLExecutionFlags.DssXmlExecutionGenerateDatamart); 
    
    try {
      rb.collectData();
      
      /*
       * 3 = successful
       * 6 = waiting for user input
       * https://lw.microstrategy.com/msdz/MSDL/GARelease_Current/docs/ReferenceFiles/reference/com/microstrategy/web/beans/EnumRequestStatus.html
       */
      System.out.println("Execution complete! XML status: " + rb.getXMLStatus());
      
    } catch (WebBeanException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  
  // Simple example to execute a data mart report. Takes into account if he report is prompted.
  public static void executeDataMartWithPrompt(String reportID, WebIServerSession session, String promptAnswer) {
    ReportBean rb = (ReportBean)BeanFactory.getInstance().newBean("ReportBean");

    // Id of the object we wish to execute
    rb.setObjectID(reportID);

    // session used for execution
    rb.setSessionInfo(session);

    //Executing Datamart report.
    System.out.println("Executing Data Mart Report");

    try {
      //Note: Te report is executed when rb.isPrompted() is invoked so setting the appropriate flag before calling rb.isPrompted() method.
      rb.setExecutionFlags(rb.getExecutionFlags() | EnumDSSXMLExecutionFlags.DssXmlExecutionGenerateDatamart);

      if (rb.isPrompted()) {
        System.out.println("Report has prompts.");
        WebReportInstance reportInstance = rb.getReportInstance();
        WebPrompts prompts = reportInstance.getPrompts();
        int promptSize = prompts.size();
        System.out.println("Number of Prompts in report: " + promptSize);

          for (int i = 0; i < prompts.size(); i++) {
            WebPrompt prompt = prompts.get(i);
            int promptType = prompt.getPromptType();
            System.out.println("Prompt type: " + promptType);

            if ( promptType == EnumWebPromptType.WebPromptTypeConstant) {
              System.out.println("Web prompt type: constant");
              WebConstantPrompt constantPrompt =  (WebConstantPrompt) prompt;
              constantPrompt.setAnswer(promptAnswer);
            }
          }//End for.

          prompts.validate();
          prompts.answerPrompts();
      }
      else {
        System.out.println("Reprot has no prompts.");
      }

      //Executing datamart report.
      rb.getReportInstance().setAsync(true);
      rb.getReportInstance().pollStatus();
      rb.collectData();

      /*
      * Report execution status.
      * 3 = successful 
      * 6 = waiting for user input
      * https://lw.microstrategy.com/msdz/MSDL/GARelease_Current/docs/ReferenceFiles/reference/com/microstrategy/web/beans/EnumRequestStatus.html
      */

      System.out.println("Execution complete! XML status: " + rb.getXMLStatus());
    } catch (WebBeanException | WebObjectsException e) {
      e.printStackTrace();
    }
  }//End executeDataMartReport.
}
