package com.microstrategy.samples.subscriptions;

import java.util.ArrayList;

import com.microstrategy.samples.sessions.SessionManager;
import com.microstrategy.web.objects.EnumWebSubscriptionContentFormatTypes;
import com.microstrategy.web.objects.SimpleList;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectInfo;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.WebObjectsFactory;
import com.microstrategy.web.objects.WebSubscription;
import com.microstrategy.web.objects.WebSubscriptionContent;
import com.microstrategy.web.objects.WebSubscriptionContentDocument;
import com.microstrategy.web.objects.WebSubscriptionsSource;
import com.microstrategy.web.objects.rw.RWExportSettings;
import com.microstrategy.webapi.EnumDSSXMLApplicationType;
import com.microstrategy.webapi.EnumDSSXMLNCSInstanceSourceField;
import com.microstrategy.webapi.EnumDSSXMLSubscriptionDeliveryType;
import com.microstrategy.webapi.EnumDSSXMLViewMedia;

public class ChangeDossierPDFSettings {
    public static void main(String[] args) {
        final String INTELLIGENCE_SERVER = "SUP-W-003423";
        final String PROJECT = "MicroStrategy Tutorial";
        final String USERNAME = "Administrator";
        final String PASSWORD = "1234";
        final String PDF_HEIGHT = "8.5";
        final String PDF_WIDTH = "11";
        // Initialize the session to the Intelligence Server
        WebIServerSession session = SessionManager.getSessionWithDetails(INTELLIGENCE_SERVER, PROJECT, USERNAME, PASSWORD);
        try {
            WebSubscription dossierSubscription = getDossierSubscription(session);
            modifyPDFExportSettings(dossierSubscription, PDF_HEIGHT, PDF_WIDTH);
        } catch (Exception webObjEx) {
            System.out.println("Error fetching object definition or fetching elements from an attribute: " + webObjEx.getMessage());
        }
        System.out.println("EOF");
    }

    /**
     * Takes a dossier email subscription, adjusts the PDF Export Settings for height and width if the requested values are different than the
     * existing PDF Export values. If a change is made, this will be propogated to the MD / Intelligence Server Note: This can be used for documents
     * as well. This code specifically utilizes a Dossier due to the code contained in getDossierSubscription method.
     * 
     * @param ws
     * @param height
     * @param width
     */
    private static void modifyPDFExportSettings(WebSubscription ws, String height, String width) {
        try {
            RWExportSettings exportSettings = ((WebSubscriptionContentDocument) ws.getContent()).getExportSettings();
            System.out
                .println("Current height x width values are: " + exportSettings.getExportPaperHeight() + " x " + exportSettings.getExportPaperWidth());
            // If both height and width match the existing exportSettings, no changes will be made
            if ((height.equalsIgnoreCase(exportSettings.getExportPaperHeight())) && width.equalsIgnoreCase(exportSettings.getExportPaperWidth())) {
                System.out
                    .println("The requested height x width settings are already applied to the given dossier subscription. No changes will be made");
            } else {
                // requested height x width are different than the existing export settings on the dossier subscription
                System.out.println("Attempting to modify PDF Export Settings of height x width: " + height + " x  " + width);
                // Edit the exportSettings according to the height / width dimensions
                exportSettings.setExportPaperHeight(height);
                exportSettings.setExportPaperWidth(width);

                // Save the subscription
                ws.save();
                System.out.println("Subscription with id " + ws.getID() + " has been saved");
                RWExportSettings exportSettingsCheck = ((WebSubscriptionContentDocument) ws.getContent()).getExportSettings();
                System.out.println("Adjusted height x width values are now: " + exportSettingsCheck.getExportPaperHeight() + " x "
                    + exportSettingsCheck.getExportPaperWidth());
            }
        } catch (Exception webObjEx) {
            System.out.println("Error fetching object definition " + webObjEx.getMessage());
        }
    }

    /**
     * Uses an existing MicroStrategy session to obtain the first MicroStrategy Dossier email subscription and return its reference so that it can be
     * modified
     * 
     * @param session
     * @return
     */
    private static WebSubscription getDossierSubscription(WebIServerSession session) {
        try {
            WebSubscriptionsSource wss = (WebSubscriptionsSource) session.getFactory().getSubscriptionsSource();
            wss.setFlags(EnumDSSXMLNCSInstanceSourceField.DssXmlNCSInstanceAllInstancesLightWeightField);
            // filter by email
            SimpleList targetSubs = wss.getSubscriptionsByDeliveryMode(EnumDSSXMLSubscriptionDeliveryType.DssXmlDeliveryTypeEmail);
            ArrayList<WebSubscription> list = targetSubs.toArrayList();
            System.out.println(list.size() + " raw subscriptions object found from project " + session.getProjectName());
            for (WebSubscription ws : list) {
                WebSubscription currentWS = (WebSubscription) wss.getObject(ws.getID(), ws.getType(), true);
                WebSubscriptionContent content = (WebSubscriptionContent) currentWS.getContent();
                WebObjectInfo oi = content.getTarget();
                // filter by Dossier
                int viewMedia = oi.getViewMediaSettings().getDefaultMode();
                // filter by format
                int format = content.getFormat().getType();
                if (format == EnumWebSubscriptionContentFormatTypes.SUBSCRIPTION_CONTENT_FORMAT_PDF &&
                    viewMedia == EnumDSSXMLViewMedia.DSSXmlViewMediaHTML5Dashboard) {
                    // Found Dossier subscription!
                    System.out.println("Identified first subscription " + currentWS.getID());
                    return currentWS;
                }
            }
        } catch (Exception webObjEx) {
            System.out.println("Error fetching object definition or fetching elements from an attribute: " + webObjEx.getMessage());
        }
        System.out.println("No dossier subscriptions found for project " + session.getProjectName() + ". Exiting program");
        System.exit(0);
        return null;
    }
}