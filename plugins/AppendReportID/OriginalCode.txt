/* FOREWORD:

This plugin appends the name of each report file subscription with a report ID.
The final file will have the standard name along with a ReportID appended to it.
Works for creating subscriptions from within a report and creating them from within the file menu.

Based on KB47631.
*/

package com.microstrategy.samples.plugins;

import com.microstrategy.web.beans.AggregatedEventHandler;
import com.microstrategy.web.beans.RequestKeys;
import com.microstrategy.web.beans.SubscriptionBean;
import com.microstrategy.web.beans.WebBeanException;
import com.microstrategy.web.beans.WebEvent;
import com.microstrategy.web.beans.WebException;
import com.microstrategy.web.objects.WebObjectsException;

public class ReportAppendID extends AggregatedEventHandler { 
 
    public ReportAppendID() {
        super();
    }

    public boolean processRequest(RequestKeys keys) throws WebException {

        WebEvent event = getWebEvent(keys);
        if(event == null) {
            return super.handleDefaultRequest(keys);
        }
        System.out.println("The event is" + event.getID());
        String reportID = null;
        switch(event.getID()) {
            case 256010 :
            	
                // The event mentioned: saveFileSubscription

                SubscriptionBean sb = (SubscriptionBean) getWebComponent();

                // We first retrieve the ID and the name of the report
                
                try {
                    System.out.println("ID: " + sb.getSubscription().getContent().getID());
                    System.out.println("Name: " + sb.getSubscription().getContent().getName());
                    reportID = sb.getSubscription().getContent().getID();
                } catch (WebObjectsException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (WebBeanException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                // We then append the ID to the fileName and print the new name
                
                System.out.println("reportID: " + reportID);
                String fname = keys.getValue("fileName");
                keys.setValue("fileName", fname + reportID, 0);
                System.out.println("new filename: " + keys.getValue("fileName"));

                return super.handleDefaultRequest(keys);
        }
        return super.handleDefaultRequest(keys);

    }

}
 