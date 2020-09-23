package com.microstrategy.samples.jobs;

import java.util.Enumeration;

import com.microstrategy.samples.sessions.SessionManager;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectsFactory;
import com.microstrategy.web.objects.admin.WebObjectsAdminException;
import com.microstrategy.web.objects.admin.monitors.CountSettings;
import com.microstrategy.web.objects.admin.monitors.EnumWebMonitorType;
import com.microstrategy.web.objects.admin.monitors.JobDetails;
import com.microstrategy.web.objects.admin.monitors.JobResults;
import com.microstrategy.web.objects.admin.monitors.JobSource;
import com.microstrategy.webapi.EnumDSSXMLJobInfo;
import com.microstrategy.webapi.EnumDSSXMLLevelFlags;

//Class to retrieve the jobs existing in the Intelligent Server and print their details.
public class ListJobs {

  public static void main(String[] args) {

    // Set server connection information
    String intelligenceServerName = "10.23.1.124"; 
    String projectName = "MicroStrategy Tutorial"; 
    String microstrategyUsername =  "administrator";
    String microstrategyPassword = "";

    // Create an IServer session
    WebIServerSession iServerSession = SessionManager.getSessionWithDetails(intelligenceServerName, projectName, microstrategyUsername, microstrategyPassword);

    //Retrieve list of jobs
    Enumeration<JobDetails> jobsDetails = retrieveListJobsDetails(iServerSession);

    //Print every job's details
    printJobsDetails( jobsDetails);

  }
  
  /**
   * Returns a list of Exisiting jobs in the IServer.
   * @param iServerSession
   * @return Enumeration<JobDetails> - List of JobDetails objects corresponding to existing jobs in the IServer
   */
  private static Enumeration<JobDetails> retrieveListJobsDetails(WebIServerSession iServerSession){

    WebObjectsFactory factory = iServerSession.getFactory();
    JobSource jobSource = (JobSource) factory.getMonitorSource(EnumWebMonitorType.WebMonitorTypeJob);
    CountSettings settings = jobSource.getCountSettings();
    
    settings.add(EnumDSSXMLJobInfo.DssXmlJobInfoUserName);
    settings.add(EnumDSSXMLJobInfo.DssXmlJobInfoProjectName);
    
    jobSource.setLevel(EnumDSSXMLLevelFlags.DssXmlCountLevel);
    jobSource.setLevel(EnumDSSXMLLevelFlags.DssXmlDetailLevel);
    
    JobResults jobResults;
    Enumeration<JobDetails> jobsDetails = null;
    try {
      jobResults = jobSource.getJobs();
      jobsDetails = jobResults.elements();
    } catch (WebObjectsAdminException e) {
      e.printStackTrace();
    }

    return jobsDetails;
  }

  /**
   * Loop through a list of jobs to print each job's details
   * @param jobsDetails - List of job elements
   */
  private static void printJobsDetails(Enumeration<JobDetails> jobsDetails) {

    while (jobsDetails.hasMoreElements()) {
      JobDetails jobDetail = jobsDetails.nextElement();
      System.out.println("  Job ID: " + jobDetail.getJobID());
      System.out.println("  -------------");
      System.out.println("  - Job Creation time: " + jobDetail.getCreationTime());
      System.out.println("  - Job Description: " + jobDetail.getDescription());
      System.out.println("  - Job duration: " + jobDetail.getDuration());
      System.out.println("  - Job Status: " + jobDetail.getJobStatus());
      System.out.println("  - Job Priority: " + jobDetail.getJobPriority());
      System.out.println("  - Job Uesr Login Name: " + jobDetail.getUserLoginName());
    }
  }
}
					
