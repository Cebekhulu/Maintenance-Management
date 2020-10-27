/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package startofagreatfuture;

import forms.SelectEquipment;
import static forms.databaseHelper.executeUpdate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;
import javax.swing.JOptionPane;
import static startofagreatfuture.Job.getFreshJobCardNumber;

/**
 *
 * @author bheki
 */
public class Project extends Job {

    List<Job> jobList = new ArrayList<>();
    PlantEquipment workItem;
    String status, equipmentID;
    public static LocalDateTime initiationDate;

    public Project(List<Job> jobList) {
        this.jobList = jobList;

    }

    public Project(Connection conn, Object... JCNs) throws SQLException {
        Integer a = 1, b = 1;

        Statement stmt = conn.createStatement();
        a = getFreshJobCardNumber("Creating project from Job Card Numbers", conn);
        b = getFreshJobCardNumber(null, conn);

        String query = "INSERT INTO job(jobCardNumber, requestTime, instantiator, equipmentID, project, status, description)"
                + " values('" + b + "' , '" + LocalDateTime.now() + "' , '00001' ,'" + SelectEquipment.selection.getEquipmentID()
                + "', '" + a + "', 'w', '" + forms.HomeScreen.jobDescriptionTextArea.getText() + "')";
        stmt.executeUpdate(query);

        query = "UPDATE job SET project = '" + a + "' where jobCardNumber = ";
        String o = query;
        Arrays.asList(JCNs).stream().peek(d -> System.out.println("peek" + d)).forEach((c)
                -> {
            try {
                stmt.executeUpdate(o + c.toString());
            } catch (SQLException e) {
                JDBCUtil.rollback(conn);
                e.printStackTrace();
            }
        });
    }

    public Project(PlantEquipment equipment, String jobDesc) {
        super(null, equipment, jobDesc, null);
        this.equipmentID = equipment.getEquipmentID();
        jobDescription = jobDesc;
    }

    /**
     * Project as taken from the database.
     *
     * @param status
     * @param equipment
     * @param projectNumber
     * @param description
     */
    public Project(String status, PlantEquipment equipment, int projectNumber, String description) {
        super(null, equipment, description, null);
        this.status = status;
        this.projectNumber = projectNumber;
        this.workItem = equipment;
        this.jobDescription = description;
        equipmentID = equipment.getEquipmentID();
    }

    @Override
    public void commitToDatabase(Connection conn) throws SQLException {
        query = "INSERT INTO job"
                + "(requestTime,instantiator,status,equipmentID,project,description,jobCardNumber,SRNumber,frequency)"
                + "VALUES(?,?,?,?,?,?,?,?,?)";

        
        if(!jobList.isEmpty()){
            query= "UPDATE job SET startTime = ? where jobCardNumber =?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            for(Job j:jobList){
            pstmt.setTimestamp(1,Timestamp.valueOf(j.getStartTime()));
            pstmt.setInt(2,j.getJobCardNumber());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            return;
        }
        System.out.println("Committing to Database");

        PreparedStatement pstmt = conn.prepareStatement(query);
        jobDescription = this.getJobDescription();

        jobCardNumber = getFreshJobCardNumber(null, conn);
        if (projectNumber == 0) {
            projectNumber = getFreshJobCardNumber("This is my project number", conn);
        }

        if (!(status instanceof Object)) {
            status = "u";
        }

        
        pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
        if (instantiator instanceof Object) {
            pstmt.setString(2, instantiator);
        } else {
            pstmt.setString(2, "00001");
        }
        if (status instanceof Object) {
            pstmt.setString(3, status);
        } else {
            pstmt.setString(3, "u");
        }
        pstmt.setString(4, equipmentID);
        pstmt.setInt(5, projectNumber);
        pstmt.setString(6, jobDescription);
        pstmt.setString(7, jobCardNumber + "");
        pstmt.setString(8, "await");
        pstmt.setString(9, "n");

        executeUpdate(pstmt, conn);
    }

    public void addJobs(Job... jobs) {
        jobList.addAll(Arrays.asList(jobs));
    }

    public List<Job> getJobs() {
        return jobList;
    }

    public void addJob(Job job) {

    }

    public void setDescription(String description) {
        jobDescription = description;
    }

    @Override
    public String toString() {
        String html = "<html><head><meta>PN" + projectNumber
                + "</meta></head><body><p>" + jobDescription
                + "</p><p>" + workItem
                + "</p></br></hr>"
                + "</body></html>";
        return html;
    }


    public void initialiseJobs(LocalDateTime now){
        initiationDate = now;
        //Check if jobs are all assigned
        String checkJobCardNos = "SELECT COUNT(SRNumber) FROM job WHERE jobCardNumber=?";
        Connection conn = JDBCUtil.getConnection();

        List<Integer> updateCount = new ArrayList<>();
        try {
            PreparedStatement pstmt = conn.prepareStatement(checkJobCardNos);
            
            jobList.stream().peek((a)->System.out.println("JCN"+a.jobCardNumber)).forEach((a) -> {
                try {
                    pstmt.setInt(1, a.jobCardNumber);
                    ResultSet r=pstmt.executeQuery();
                    r.next();
                    updateCount.add(r.getInt("COUNT(SRNumber)"));
                } catch (SQLException e) {
                    System.out.println("Goiing nowhere");
                    throw new RuntimeException(e);
                }
            System.out.println(pstmt+" ppstmt");
            });
            System.out.println("up Count "+updateCount);
        } catch (SQLException e) {
        e.printStackTrace();
        }
        Collections.sort(updateCount);
        if (Collections.binarySearch(updateCount, 1) >-1) {
            throw new RuntimeException("Assign jobs to artisans");
        }
        now=now.toLocalDate().atTime(LocalTime.MIN).plusHours(8);
        System.out.println("Now "+now);
        
        
        String query = "UPDATE job SET startTime = ? WHERE jobCardNumber =?;";
        
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (Job a : jobList) {
                a.setStartTime(now);
                pstmt.setTimestamp(1, Timestamp.valueOf(a.getStartTime()));
                pstmt.setInt(2, a.getJobCardNumber());
                pstmt.addBatch();
                LocalDateTime endTime = Job.getExpectedEndTime(a.getExpectedDuration(), now);
                now = endTime;
                System.out.println("Start " + a.getStartTime() + "\nEnd " + now);
            }
        pstmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            JDBCUtil.rollback(conn);
        }
        
        
        

    }
    public void initialiseJobs(){
        LocalDateTime now= LocalDateTime.now();
        for (Job a : jobList) {
                a.setStartTime(now);
                
                LocalDateTime endTime = Job.getExpectedEndTime(a.getExpectedDuration(), now);
                expectedStopTime=now;
            }
        
    }
}
