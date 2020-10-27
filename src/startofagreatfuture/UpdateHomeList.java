/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package startofagreatfuture;

/**
 *
 * @author bheki
 */
import static forms.GanttChart.getFriendlyTime;
import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import static startofagreatfuture.Job.getDuration;

public class UpdateHomeList implements Runnable {

    private static void checkIfScheduled(UpdateHomeList get) {
        String query = "SELECT COUNT(jobDescription) FROM job WHERE date =? AND description =?";
        Connection conn = JDBCUtil.getConnection();
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            if(rs.getInt("COUNT(jobDescription)")>0)
                query = "INSERT INTO JOB";
        } catch (SQLException ex) {
            Logger.getLogger(UpdateHomeList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void extractDetails() {
        System.out.println("Got in extractDetails at "+LocalDateTime.now());
        if(!fromJDBC.isEmpty()){
            fromJDBC.stream().filter(a->a.statusOfJob.equals("o")).forEach(a->forms.HomeScreen.overdueJobListModel.addElement(template(a)));
            fromJDBC.stream().filter(a->a.statusOfJob.equals("u")&(a.project ==0)).forEach(a->forms.HomeScreen.unassignedJobListModel.addElement(template(a)));
            fromJDBC.stream().filter(a->a.statusOfJob.equals("w")).forEach(a->forms.HomeScreen.WIPJobListModel.addElement(template(a)));
        }
    }

    int JCN;
    int project = 0;
    String SRNumber, statusOfJob, jobDescription, equipmentName;
    LocalDateTime expectedStopTime, requestTime, startTime, endTime = null;
    public static DefaultListModel JobListModel = new DefaultListModel();
    private String expectedDuration;
    private String equipmentID;
    private String instantiator;
    private String frequency;

    public static List<UpdateHomeList> fromJDBC = new ArrayList<>();
    private List<UpdateHomeList> copyOfListOfThings = new ArrayList<>();
    public static List<UpdateHomeList> listOfRepeaters = new ArrayList<>();
    public static List<UpdateHomeList> listOfPostponed = new ArrayList<>();

    public UpdateHomeList(int a) {
    }

    public UpdateHomeList(LocalDateTime requestTime, LocalDateTime startTime, String expectedDuration,
            LocalDateTime stopTime, String instantiator, String status, String equipmentName, String equipmentID, int project,
            String description, int jobCardNumber, String frequency) {

        this.requestTime = requestTime;
        this.startTime = startTime;
        this.expectedDuration = expectedDuration;
        this.endTime = stopTime;
        this.instantiator = instantiator;
        this.statusOfJob = status;
        this.equipmentName = equipmentName;
        this.equipmentID = equipmentID;
        this.project = project;
        this.jobDescription = description;
        this.JCN = jobCardNumber;
        this.frequency = frequency;

        if (startTime instanceof Object) {
            expectedStopTime = startTime.plus(getDuration(expectedDuration));
        }
    }
int count;
    @Override
    public void run() {
        try {
            System.out.println("count"+count);
            if(count>3){
                System.out.println("invisible? "+!forms.HomeScreen.listOfThings.isVisible());
            if(!forms.HomeScreen.listOfThings.isVisible()){System.out.println("I'm returnung");
                return;}}
            if(count<8)count++;
            
            System.out.print("ddnt retrn");
            Connection conn = JDBCUtil.getConnection();
            Statement stmt = conn.createStatement();
            String query = "SELECT jobCardNumber,job.equipmentID, job.startTime, stopTime, job.SRNumber, job.status,instantiator,"
                    + "job.project,requestTime, expectedDuration, job.description,job.frequency, equipment.name FROM job INNER "
                    + "JOIN equipment ON job.equipmentID = equipment.EquipmentID WHERE SRNumber='await' "
                    + " order by requestTime desc";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            fromJDBC.clear();
            listOfRepeaters.clear();
            listOfPostponed.clear();
            while (rs.next()) {

                LocalDateTime startTime = null, requestTime = null, stopTime = null;
                String instantiator, status, equipmentName, equipmentID, jobDescription, SRNumber, repeats, expectedDuration;
                int project, jobCardNumber;

                try {
                    requestTime = rs.getTimestamp("requestTime").toLocalDateTime();
                } catch (NullPointerException e) {
                }
                try {
                    startTime = rs.getTimestamp("startTime").toLocalDateTime();
                } catch (NullPointerException e) {
                }
                expectedDuration = rs.getString("expectedDuration");
                try {
                    stopTime = rs.getTimestamp("stopTime").toLocalDateTime();
                } catch (NullPointerException e) {
                }

                instantiator = rs.getString("instantiator");
                status = rs.getString("status");
                equipmentName = rs.getString("name");
                equipmentID = rs.getString("equipmentID");
                project = rs.getInt("project");
                jobDescription = rs.getString("description");
                jobCardNumber = rs.getInt("jobCardNumber");
                SRNumber = rs.getString("SRNumber");
                repeats = rs.getString("frequency");

                //Calculating expected stop time; Send it inside the template
                expectedStopTime = null;
                String est = rs.getString("expectedDuration");
                if (startTime instanceof Object) {
                    expectedStopTime = startTime.plus(getDuration(est));
                } else {
                    expectedStopTime = null;
                }
                //Setting status
                if (!(status.equals("a") | status.equals("c"))) {
                    if (startTime instanceof Object) {
                        statusOfJob = "w";
                        if (expectedStopTime.toEpochSecond(ZoneOffset.UTC) < LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) {
                            status = "o";
                        }
                        if ((stopTime instanceof Object)) {
                            status = "d";
                        }
                    }
                } else {
                    status = "u";
                }
                if (!(repeats.equals("n"))) {
                    listOfRepeaters.add(new UpdateHomeList(requestTime, startTime, expectedDuration,
                            stopTime, instantiator, status, equipmentName, equipmentID, project, jobDescription, jobCardNumber,
                            repeats));
                    
                } else if((project==0)||(!(expectedDuration instanceof Object)&&project>0)){
                    fromJDBC.add(new UpdateHomeList(requestTime, startTime, expectedDuration,
                            stopTime, instantiator, status, equipmentName, equipmentID, project, jobDescription, jobCardNumber,
                            repeats));
                }
                else if(status.equals("p")){
                        listOfPostponed.add(new UpdateHomeList(requestTime, startTime, expectedDuration,
                            stopTime, instantiator, status, equipmentName, equipmentID, project, jobDescription, jobCardNumber,
                                repeats));/*
                        System.out.println("LOP size "+listOfPostponed.size());*/
                    }
                
            }
            stmt.close();
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //if(forms.HomeScreen.listOfThings.isVisible())
        compareTheLists(copyOfListOfThings,JobListModel,fromJDBC);
        
    }

//<editor-fold defaultstate="collapsed" desc="Getters; Setters; Equals; HashCode; toString">
    public int getJCN() {
        return JCN;
    }

    public String getSRN() {
        return SRNumber;
    }

    public String getSOJ() {
        return statusOfJob;
    }

    public LocalDateTime getRT() {
        return requestTime;
    }

    public String getJD() {
        return jobDescription;
    }

    public String getEN() {
        return equipmentName;
    }

    public String getExpectedDuration() {
        return expectedDuration;
    }

    public LocalDateTime getEST() {
        return expectedStopTime;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.JCN;
        return hash;
    }

    @Override
    public String toString() {
        return "UpdateJobsListForJListModel{" + "JCN=" + JCN + ", SRN=" + SRNumber + ", SOJ=" + statusOfJob + ", JD=" + jobDescription + ", EN=" + equipmentName + ", EST=" + expectedStopTime + ", requestTime=" + requestTime + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UpdateHomeList other = (UpdateHomeList) obj;
        if (this.JCN != other.JCN) {
            return false;
        }
        if (!Objects.equals(this.statusOfJob, other.statusOfJob)) {
            return false;
        }
        if (!Objects.equals(this.jobDescription, other.jobDescription)) {
            return false;
        }
        return true;
    }
//</editor-fold>

    /**
     *This method ought to be part of servlet class.
     * Initialises planned jobs as they near scheduled date.
     */
    public static void checkPostponers(){
        System.out.println("Checking postponers");
        int x = listOfPostponed.size();
        for(int c =0;c<x;c++){
            LocalDateTime start=listOfPostponed.get(c).startTime;
            LocalDateTime eEnd =listOfPostponed.get(c).startTime.plus(Job.getDuration(listOfPostponed.get(c).getExpectedDuration()));
            LocalDateTime now= LocalDateTime.now();
            if(start.isBefore(now)){
                StringBuilder query =new StringBuilder("UPDATE job SET status = 'w' where jobCardNumber =?");
                if(eEnd.isBefore(now))
                    query.replace(25, 26, "o");
                System.out.println("Update "+query);
                Connection conn = JDBCUtil.getConnection();
                try {
                    PreparedStatement pstmt = conn.prepareStatement(query.toString());
                    pstmt.setInt(1,listOfPostponed.get(c).JCN);
                    pstmt.executeUpdate();
                    JDBCUtil.commit(conn);
                    
                    Thread.sleep(300);
                } catch (SQLException|InterruptedException e) {
                    JDBCUtil.rollback(conn);
                    Logger.getLogger(UpdateHomeList.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
    }
    
    public static void checkRepeaters(){
        System.out.println("Checking repeaters");
        int x = listOfRepeaters.size();
        for(int c =0;c<x;c++){
            String frequency = listOfRepeaters.get(c).frequency;
            UpdateHomeList get = listOfRepeaters.get(c);
            switch(frequency){
                case "w":
                    if(get.requestTime.getDayOfWeek().equals(LocalDate.now().getDayOfWeek())){
                        checkIfScheduled(get);
                    }
                    break;
                case "m":
                    if(get.requestTime.toLocalDate().equals(LocalDate.now())){
                        checkIfScheduled(get);
                    }
                    break;
                case "d":
                    checkIfScheduled(get);
                    break;
            }
            if(listOfPostponed.get(c).startTime.isBefore(LocalDateTime.now())){
                String query ="UPDATE job SET status = w where jobCardNumber =?";
                Connection conn = JDBCUtil.getConnection();
                try {
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setInt(1,listOfPostponed.get(c).JCN);
                    pstmt.executeUpdate();
                    JDBCUtil.commit(conn);
                    
                    Thread.sleep(300);
                } catch (SQLException|InterruptedException e) {
                    JDBCUtil.rollback(conn);
                    Logger.getLogger(UpdateHomeList.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
    }
    

    public static void addToJobListModel() {
        JobListModel.addElement("");
    }

    private void compareTheLists(
            List<UpdateHomeList> copyOfListOfThings,DefaultListModel JobListModel,List<UpdateHomeList> fromJDBC) {
        
        System.out.println("COLOT, JLM, fJDBC"+copyOfListOfThings.size()+", "+JobListModel.size()+", "+fromJDBC.size());
        if (copyOfListOfThings.isEmpty() || JobListModel.isEmpty()) {
            copyOfListOfThings.addAll(fromJDBC);
            copyOfListOfThings
                    .stream()
                    .forEach((a) -> JobListModel.addElement(template(a)));
        }

        for(UpdateHomeList a:fromJDBC){
            if(!copyOfListOfThings.contains(a)){
                copyOfListOfThings.add(fromJDBC.indexOf(a), a);
                JobListModel.add(copyOfListOfThings.indexOf(a), template(a));
            }
        }

        for(UpdateHomeList a:copyOfListOfThings){
            if(!fromJDBC.contains(a)){
                copyOfListOfThings.remove(a);
                JobListModel.remove(copyOfListOfThings.indexOf(a));
            }
        }
        
        
        if(JobListModel.contains("")){
            JobListModel.removeElement("");
        }else{
            JobListModel.addElement("");
        }
    }

    /**
     *
     * @param requestTime
     * @param startTime
     * @param stopTime
     * @param project
     * @param instantiator
     * @param status
     * @param equipmentName
     * @param expectedDuration
     * @param equipmentID
     * @param description
     * @param JCN
     * @param SRN
     * @param freq
     * @return returns an HTML document for displaying at home screen
     */
    public static String template(LocalDateTime requestTime, LocalDateTime startTime, String expectedDuration, LocalDateTime stopTime,
            String instantiator, String status, String equipmentName, String equipmentID, int project, String description,
            int JCN, String freq)//<editor-fold defaultstate="collapsed" desc="comment">
    {
        String colour;
        String to = "";
        if (!(getFriendlyTime(startTime).equals(""))) {
            to = "to";
        }

        LocalDateTime expectedStopTime = null;
        LocalDateTime rt = requestTime;
        LocalDateTime startT = startTime;
        String expD = expectedDuration;
        LocalDateTime stopT = stopTime;
        String insta = instantiator;
        String statusOfJob = status;
        String serviceItem = equipmentName;
        String serviceItemID = equipmentID;
        int projectNumber = project;
        String jobDescription = description;
        int jobCardNumber = JCN;
        String frequency = freq;

        if (startTime instanceof Object) {
            expectedStopTime = startTime.plus(getDuration(expectedDuration));
        }

        switch (statusOfJob) {
            case "w"://WIP Blue
                colour = "blue";
                break;
            case "p"://Awaiting spares Black
                colour = "gray";
                break;
            case "u"://unassigned Yellow
                colour = "yellow";
                break;
            case "o"://overdue Red
                colour = "red";
                break;
            case "d"://done Green
                colour = "green";
                break;
            default:
                throw new RuntimeException("Got an unfamiliar status of Job");
        }
        
        
        if(project>0&&!(startTime instanceof Object))
            colour="black";

        String temp = "<html>"
                + "<head> RT" + rt + "/RT"
                + "STRT" + startT + "/STRT"
                + "EXPD" + expD + "/EXPD"
                + "STPT" + stopT + "/STPT"
                + "INST" + insta + "/INST"
                + "SOJ" + statusOfJob + "/SOJ"
                + "SI" + serviceItem + "/SI"
                + "SIID" + serviceItemID + "/SIID"
                + "PN" + projectNumber + "/PN"
                + "JD" + jobDescription + "/JD"
                + "JCN" + jobCardNumber + "/JCN"
                + "FRE" + frequency + "/FRE"
                + "</head>"
                + "<body>"
                + "<table>"
                + "<tr valign=\"top\">"
                + "<td rowspan=\"2\" width=\"1\" bgcolor=\"" + colour + "\" style=\"background: " + colour + "\" style=\"border: none\">"
                + "</td>"
                + "<td width=\"127\" style=\"background: transparent\" style=\"border: none; padding: 0cm\">"
                + "<p style=\"font-style: normal; font-weight: normal\"si>" + serviceItem + "</p si>"
                + "</td>"
                + "<td>"
                + "<p style=\"font-style: normal; font-weight: normal\"st>" + getFriendlyTime(startT) + " " + to + " " + " </p st>"
                + "</td>"
                + "<td>"
                + "<p style=\"font-style: normal; font-weight: normal\"et>" + getFriendlyTime(expectedStopTime) + "</p et>"
                + "</td>"
                + "</tr>"
                + "<tr><td colspan =\"3\"jd>" + jobDescription + "</td></tr><tr><td></td></tr>"
                + "</table>"
                + "</body>"
                + "</html>";
        return temp;
    }
//</editor-fold>

    public static LocalDateTime parseTheDate(String dateTimeInString) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter tf = DateTimeFormatter.ofPattern(" HH:mm:ss");
        DateTimeFormatter tg = DateTimeFormatter.ofPattern(" HH:mm");

        LocalDate d;
        LocalTime t;

        d = LocalDate.parse(dateTimeInString.substring(0, 10), df);

        if (dateTimeInString.contains("T") && dateTimeInString.length() == 19) {
            t = LocalTime.parse(" " + dateTimeInString.substring(dateTimeInString.indexOf("T") + 1), tf);
        } else if (dateTimeInString.contains("T") && dateTimeInString.length() == 16) {
            t = LocalTime.parse(" " + dateTimeInString.substring(dateTimeInString.indexOf("T") + 1), tg);
        } else {
            t = LocalTime.parse(dateTimeInString.substring(10), tf);
        }

        return LocalDateTime.of(d, t);
    }

    public static String template(UpdateHomeList a) {
        LocalDateTime requestTime = a.getRT();
        LocalDateTime startTime = a.startTime;
        String ed = a.getExpectedDuration();
        LocalDateTime stopTime = a.endTime;
        String instantiator = a.instantiator;
        String statusOfJob = a.getSOJ();
        String serviceItem = a.getEN();
        String equipmentID = a.getEquipmentID();
        int project = a.getProject();
        String jobDescription = a.getJD();
        int JCN = a.getJCN();
        String frequency = a.frequency;

        return template(requestTime, startTime, ed, stopTime, instantiator, statusOfJob, serviceItem, equipmentID,
                project, jobDescription, JCN, frequency);
    }

    private int getProject() {
        return this.project;
    }

    private String getEquipmentID() {
        return equipmentID;
    }

    public static DefaultListModel getJobListModel() {
        return JobListModel;
    }

}
