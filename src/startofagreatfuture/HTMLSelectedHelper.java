/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package startofagreatfuture;

import java.time.LocalDateTime;
import static startofagreatfuture.Job.getDuration;
import static startofagreatfuture.Job.getExpectedEndTime;
import static startofagreatfuture.UpdateHomeList.parseTheDate;

/**
 *
 * @author b-ok.org
 */
public class HTMLSelectedHelper {

    String requestTimeString;
    String startTimeString;
    String expectedDuration;
    String stopTimeString;
    String instantiator;
    String statusOfJob;
    String serviceItemName;
    String equipmentID;
    String project;
    String jobDetails;
    int jobCardNumber;
    String expectedStopTimeString;
    String frequency;

    LocalDateTime requestTime, startTime = null, stopTime = null, expectedStopTime = null;

    public HTMLSelectedHelper(String wholeText) {
        requestTimeString = wholeText.substring(wholeText.indexOf("RT") + 2, wholeText.indexOf("/RT"));
        jobDetails = wholeText.substring(wholeText.indexOf("JD") + 2, wholeText.indexOf("/JD"));
        serviceItemName = wholeText.substring(wholeText.indexOf("SI") + 2, wholeText.indexOf("/SI"));
        expectedDuration = wholeText.substring(wholeText.indexOf("EXPD") + 4, wholeText.indexOf("/EXPD"));
        int miko = wholeText.indexOf("SOJ") + 3;
        statusOfJob = wholeText.substring(miko, miko + 1);
        equipmentID = wholeText.substring(wholeText.indexOf("SIID") + 4, wholeText.indexOf("/SIID"));;
        jobCardNumber = Integer.parseInt(wholeText.substring(wholeText.indexOf("JCN") + 3, wholeText.indexOf("/JCN")));
        project = wholeText.substring(wholeText.indexOf("PN") + 2, wholeText.indexOf("/PN"));
        startTimeString = wholeText.substring(wholeText.indexOf("STRT") + 4, wholeText.indexOf("/STRT"));
        stopTimeString = wholeText.substring(wholeText.indexOf("STPT") + 4, wholeText.indexOf("/STPT"));
        instantiator = wholeText.substring(wholeText.indexOf("INST") + 4, wholeText.indexOf("/INST"));
        frequency = wholeText.substring(wholeText.indexOf("FRE") + 3, wholeText.indexOf("/FRE"));

        if (requestTimeString.length() < 19) {
            requestTimeString = requestTimeString + ":00";
        }
        requestTime = parseTheDate(requestTimeString);
        if ((startTimeString instanceof Object) && startTimeString.length() > 10) {
            try {
                startTime = parseTheDate(startTimeString);
            } catch (NullPointerException e) {
                System.err.println("StartTimeString that induced the error" + startTimeString);
            }
            if (stopTimeString.length() > 10) {
                try {
                    stopTime = parseTheDate(stopTimeString);
                } catch (NullPointerException e) {
                }
            }
        }

    }

    public Job getJob() {
        Job j = new Job(requestTime, startTime, expectedDuration, stopTime, instantiator, statusOfJob, serviceItemName, equipmentID,
                Integer.parseInt(project), jobDetails, jobCardNumber, "", frequency);
        if (startTime instanceof Object) {
            expectedStopTime = getExpectedEndTime(expectedDuration, startTime);
        } else {
            expectedStopTime = getExpectedEndTime(expectedDuration, LocalDateTime.now());
        }
        j.setExpectedStopTime(expectedStopTime);

        return j;
    }

    public int getJobCardNumber() {
        return jobCardNumber;
    }

    public String getExpectedDuration() {
        return expectedDuration;
    }

    public String getProject() {
        return project;
    }

    public String getEquipmentID() {
        return equipmentID;
    }

    public String getStatusOfJob() {
        return statusOfJob;
    }

    public String getServiceItemName() {
        return serviceItemName;
    }

    public String getJobDetails() {
        return jobDetails;
    }

    public String getExpectedStopTimeString() {
        return expectedStopTimeString;
    }

    public String getStartTimeString() {
        return startTimeString;
    }

    public String getRequestTimeString() {
        return requestTimeString;
    }

    public String getStopTimeString() {
        return stopTimeString;
    }

    public String getInstantiator() {
        return instantiator;
    }

    public String getFrequency() {
        return frequency;
    }

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getStopTime() {
        return stopTime;
    }

    public LocalDateTime getExpectedStopTime() {
        return expectedStopTime;
    }

}
