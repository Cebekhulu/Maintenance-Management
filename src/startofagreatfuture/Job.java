/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package startofagreatfuture;

import forms.databaseHelper;
import static forms.databaseHelper.executeQuery;
import static forms.databaseHelper.executeUpdate;
import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author bheki
 */
public class Job implements Comparable<Job> {

    LocalDateTime requestTime, startTime, stopTime;
    String expectedDuration, instantiator, jobDescription, query, equipmentName, SRNumber, frequency, status = null, equipmentID;
    LocalDateTime expectedStopTime;
    int projectNumber;
    LocalDateTime now;
    int jobCardNumber, project;
    List<Job> myJobList;
    List<String> SRNAlreadyInJob;

    public Job() {
    }

    /**
     * This is the bare minimum required parameters to create an instance of the
     * Job object.
     *
     * @param expectedDuration
     * @param equipment
     * @param jobDescription
     * @param repetition how frequently this job repeats. Null if it doesn't.
     */
    public Job(String expectedDuration, PlantEquipment equipment, String jobDescription, String repetition) {
        this.requestTime = LocalDateTime.now();
        this.expectedDuration = expectedDuration;
        this.status = "u";
        this.equipmentID = equipment.getEquipmentID();
        this.jobDescription = jobDescription;
        this.instantiator = "00001";
        this.frequency = repetition;

    }

    /**
     * Jobs from database should contain all settable variables.
     *
     * @param description job's description
     * @param equipmentName
     * @param startTimes Starting time if present, null if it isn't initialised
     * in the DB
     * @param JCN Job card number
     * @param expectedDuration
     * @param SRNumber
     * @param requestTime when the job was requested
     * @param equipmentID
     * @param status
     * @param stopTime
     * @param instantiator
     * @param project
     * @param frequency
     */
    public Job(LocalDateTime requestTime, LocalDateTime startTimes, String expectedDuration,
            LocalDateTime stopTime, String instantiator, String status, String equipmentName,
            String equipmentID, int project, String description, int JCN, String SRNumber, String frequency) {
        this.jobDescription = description;
        this.equipmentName = equipmentName;
        this.startTime = startTimes;
        this.jobCardNumber = JCN;
        this.expectedDuration = expectedDuration;
        this.requestTime = requestTime;
        this.SRNumber = SRNumber;
        this.status = status;
        this.stopTime = stopTime;
        this.instantiator = instantiator;
        this.projectNumber = project;
        this.equipmentID = equipmentID;
        this.frequency = frequency;
    }

    public void setStartTime(LocalDateTime startTime)//<editor-fold defaultstate="collapsed" desc="comment">
    {
        this.startTime = startTime;
    }
//</editor-fold>

    public void setExpectedStopTime(LocalDateTime expectedStopTime) //<editor-fold defaultstate="collapsed" desc="comment">
    {
        this.expectedStopTime = expectedStopTime;
    }
//</editor-fold>

    public void setEndTime(LocalDateTime endTime) //<editor-fold defaultstate="collapsed" desc="comment">
    {
        this.stopTime = endTime;
    }
//</editor-fold>

    public void setEquipmentID(String equipmentID) {
        this.equipmentID = equipmentID;
    }

    public String getSRNumber() {
        return SRNumber;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public void setWorkers(List<String> workers, int jobCardNumber, Connection conn) throws SQLException {
        this.jobCardNumber = jobCardNumber;
        ResultSet rs;
        myJobList = new ArrayList<>();

        SRNAlreadyInJob = new ArrayList<>();
        query = "SELECT equipment.name, requestTime,startTime, expectedDuration, stopTime, instantiator, job.status,job.equipmentID,"
                + " project,description, SRNumber,jobCardNumber, frequency FROM job "
                + "INNER JOIN equipment ON equipment.equipmentID=Job.equipmentID where jobCardNumber =" + jobCardNumber;
        rs = executeQuery(conn.prepareStatement(query), conn);
        //LocalDateTime requestTime = null, startTime = null, stopTime = null;
        //String expectedDuration = null, repeats = null, equipmentName, instantiator = null, status = null, equipmentID = null, jobDescription = null, SRNumber = null;

        while (rs.next()) {
            try {
                requestTime = (rs.getTimestamp("requestTime").toLocalDateTime());
            } catch (NullPointerException e) {
            }
            try {
                startTime = rs.getTimestamp("startTime").toLocalDateTime();
            } catch (NullPointerException e) {
            }
            try {
                expectedDuration = rs.getString("expectedDuration");
            } catch (NullPointerException e) {
            }
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
            this.jobCardNumber = rs.getInt("jobCardNumber");
            SRNumber = rs.getString("SRNumber");
            frequency = rs.getString("frequency");

            now = LocalDateTime.now();

            SRNAlreadyInJob.add(SRNumber);
            myJobList.add(new Job(requestTime, startTime, expectedDuration, stopTime, instantiator, status, equipmentName,
                    equipmentID, project, jobDescription, this.jobCardNumber, SRNumber, frequency));
        }
        if (project > 0) {
            setWorkersForAProjectJob(workers, conn);
            return;
        }
        if (myJobList.size() == 1) {

            query = "UPDATE job set startTime =?, status = 'w' where jobCardNumber =?";

            String x;
            
                x = "w";
            
            PreparedStatement pstmt = conn.prepareStatement(query);

            
                pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            
            pstmt.setInt(2, jobCardNumber);

            executeUpdate(pstmt, conn);

            

            String prefix = "INSERT INTO job (requestTime, startTime, expectedDuration,"
                    + " instantiator, status, equipmentID, project, description, "
                    + "jobCardNumber, frequency, SRNumber) VALUES(?,?,'" + expectedDuration
                    + "','" + instantiator + "','" + x + "','" + equipmentID + "', '" + project
                    + "',?,'" + jobCardNumber + "', '" + frequency + "', ?)";
            PreparedStatement pstmt2 = conn.prepareStatement(prefix);
            pstmt2.setTimestamp(1, Timestamp.valueOf(requestTime));
            pstmt2.setTimestamp(2, this.startTime instanceof Object ? Timestamp.valueOf(this.startTime) : Timestamp.valueOf(LocalDateTime.now()));
            pstmt2.setString(3, jobDescription);

            for (String SRN : workers) {
                pstmt2.setString(4, SRN);
                pstmt2.addBatch();

            }
            pstmt2.executeBatch();
            JDBCUtil.commit(conn);
        } else {
            String query = "UPDATE job set startTime =?, status = 'w' where jobCardNumber= ?;";
            String x;
            if (project > 0) {
                query = query.replace(", status = 'w'", "");
                x = "u";

            } else {
                x = "w";
            }

            PreparedStatement pstmt = conn.prepareStatement(query);
            if (project == 0) {
                pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            }
            pstmt.setInt(2, jobCardNumber);

            SRNAlreadyInJob.stream().filter(a -> workers.contains(a)).forEach(a -> workers.remove(a));

            String queryPrefix = "INSERT INTO job (requestTime, startTime, expectedDuration, instantiator, status,"
                    + " equipmentID, project, description, jobCardNumber, frequency, SRNumber) "
                    + "values ( ?,?,'" + expectedDuration + "', '" + instantiator + "', '" + status + "', '"
                    + equipmentID + "', '" + project + "', '" + jobDescription + "', '" + jobCardNumber + "', '"
                    + frequency + "', ?);";
            PreparedStatement pstmt2 = conn.prepareStatement(queryPrefix);
            pstmt2.setTimestamp(1, Timestamp.valueOf(requestTime));

            if (project == 0) {
                pstmt2.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            } else {
                pstmt2.setNull(2, Types.TIMESTAMP);
            }

            workers.stream().forEach(a
                    -> {
                try {
                    pstmt2.setString(3, a);
                    executeUpdate(pstmt2, conn);
                } catch (SQLException ex) {
                    JDBCUtil.rollback(conn);
                    Logger.getLogger(Job.class.getName()).log(Level.SEVERE, null, ex);
                }

            });
            conn.commit();

        }
    }
    private List<Artisan> workers;

    public void removeWorkers(List<String> workers, int JCN) {//read about queries within queries
        StringBuilder querySB = new StringBuilder("DELETE FROM JOB WHERE jobCardNumber ='" + JCN + "' AND (SRNumber =(SELECT artisan.SRNumber FROM artisan WHERE name =?)");

        for (int a = 1; a < workers.size(); a++) {
            querySB = querySB.append(" OR (SRNumber = (SELECT artisan.SRNumber FROM artisan WHERE name =?))");
        }

        Connection conn = JDBCUtil.getConnection();
        querySB = querySB.append(")");

        try {
            PreparedStatement pstmt = conn.prepareStatement(querySB.toString());
            for (int a = 1; a < workers.size() + 1; a++) {
                pstmt.setString(a, workers.get(a - 1));
            }
            executeUpdate(pstmt, conn);
            StringBuilder anotherSB = new StringBuilder("UPDATE artisan set status = 'a' WHERE name =?");
            for (int a = 1; a < workers.size(); a++) {
                anotherSB.append(" OR name =?");
            }
            PreparedStatement pstmt1 = conn.prepareStatement(anotherSB.toString());
            for (int a = 1; a < workers.size() + 1; a++) {
                pstmt1.setString(a, workers.get(a - 1));
            }
            executeUpdate(pstmt1, conn);
        } catch (SQLException sQLException) {
            JDBCUtil.rollback(conn);
        }
    }

    @Override
    public int compareTo(Job jobs) {
        return jobs.stopTime.compareTo(startTime);
    }

    public void addParticipants(Artisan... workers) {
        this.workers.addAll(Arrays.asList(workers));
        this.workers.stream().forEach((a) -> a.updateStatus("BUSY"));
        //back this up in a database somewhere
    }

    public static void completeJob(int JCN) throws SQLException {
        List<String> SRNumbers = new ArrayList<>();
        String equipmentID = null;
        boolean onlyOnce = true;
        boolean bool = true;
        Connection conn = JDBCUtil.getConnection();
        String query = "SELECT startTime, stopTime, status, equipmentID, SRNumber from Job WHERE jobCardNumber = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, JCN);

        ResultSet rs = executeQuery(pstmt, conn);
        while (rs.next()) {
            while (bool) {

                if (!(rs.getDate("startTime") instanceof Object)) {
                    System.out.println("Job hasnt initiated");
                    return;
                }
                if (rs.getDate("stopTime") instanceof Object) {
                    System.out.println("Job already finished");
                    return;
                }
                if (rs.getString("status").equals("a")) {
                    JOptionPane.showMessageDialog(null, "This job awaits missing spares", "Unable to complete", JCN);
                    return;
                }
                while (onlyOnce) {
                    equipmentID = rs.getString("equipmentID");
                    onlyOnce = false;
                }
                bool = false;
            }
            SRNumbers.add(rs.getString("srNumber"));

        }
        LocalDateTime now = LocalDateTime.now();
        query = "UPDATE Job SET stopTime=?, status = 'd' WHERE jobCardNumber ='" + JCN + "'";
        PreparedStatement pstmt6 = conn.prepareStatement(query);
        pstmt6.setObject(1, now);
        executeUpdate(pstmt6, conn);

        SRNumbers.remove("await");
        StringBuilder q = new StringBuilder("UPDATE artisan SET status = 'a' WHERE SRNumber = ?");

        for (int n = 0; n < SRNumbers.size() - 1; ++n) {
            q.append(" OR SRNumber = ?");
        }
        PreparedStatement pstmt2 = conn.prepareStatement(q.toString());

        for (int index = 1; index < SRNumbers.size() + 1; index++) {
            pstmt2.setString(index, SRNumbers.get(index - 1));
        }
        System.out.println("pstmt2: " + pstmt2);
        executeUpdate(pstmt2, conn);

        query = "UPDATE equipment SET status = 'a' WHERE equipmentID =?";
        PreparedStatement pstmt3 = conn.prepareStatement(query);
        pstmt3.setString(1, equipmentID);
        executeUpdate(pstmt3, conn);
        conn.commit();
        conn.close();
    }

    public LocalDateTime getEndTime() {
        return stopTime;
    }

    public String getExpectedDuration() {
        return expectedDuration;
    }

    public static LocalDateTime getExpectedEndTime(String duration, LocalDateTime starting) {
        return starting.plus(getDuration(duration));
    }

    public static Duration getDuration(String describer) {
        int amount = Integer.parseInt(describer.substring(0, describer.length() - 1));
        switch (describer.charAt(describer.length() - 1)) {
            case 'D':
                return Duration.of(amount, ChronoUnit.DAYS);
            case 'W':
                return Duration.of(7 * amount, ChronoUnit.DAYS);
            case 'M':
                return Duration.of(30 * amount, ChronoUnit.DAYS);
            case 'H':
                return Duration.of(amount, ChronoUnit.HOURS);

        }
        throw new RuntimeException("No facken duration like that");
    }

    public static void initialiseRoutine() {

    }

    public LocalDateTime getExpectedEndTime() {
        if (this.expectedStopTime instanceof LocalDateTime) {
            return this.expectedStopTime;
        }
        
        if (this.startTime instanceof Object) {
            return startTime.plus(getDuration(expectedDuration));
        } else {
            LocalDateTime e = LocalDateTime.now();
            return e.plus(getDuration(expectedDuration));

        }
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public double getDuration() {
        if (expectedStopTime instanceof LocalDateTime)
        return ChronoUnit.HOURS.between(this.startTime, this.expectedStopTime);
        else
            return ChronoUnit.HOURS.between(this.startTime, getExpectedEndTime(expectedDuration,startTime));
    }

    public int getJobCardNumber() {
        return jobCardNumber;
    }

    @Override
    public String toString() {

        String html = UpdateHomeList.template(requestTime, startTime, expectedDuration, stopTime, instantiator, status, equipmentName,
                equipmentID, projectNumber, jobDescription, jobCardNumber, frequency);

        return html;
    }

    public void commitToDatabase(Connection conn) throws SQLException {
        System.out.println("Committing to Database");

        jobDescription = this.getJobDescription();

        if (jobCardNumber == 0) {
            jobCardNumber = getFreshJobCardNumber(null, conn);
        }

        if (!(status instanceof Object)) {
            status = "u";
        }

        query = "INSERT INTO job"
                + "(requestTime,instantiator,status,equipmentID,project,description,jobCardNumber,SRNumber,frequency,expectedDuration)"
                + "VALUES(?,?,?,?,?,?,?,?,?,?)";

        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setObject(1, LocalDateTime.now());
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
        pstmt.setInt(5, this.projectNumber);
        pstmt.setString(6, jobDescription);
        pstmt.setString(7, jobCardNumber + "");
        pstmt.setString(8, "await");
        pstmt.setString(9, frequency);
        pstmt.setString(10, expectedDuration);

        System.out.println("pastm " + pstmt);

        executeUpdate(pstmt, conn);
    }

    public void changeToProject(Connection conn) throws SQLException {

        if (this.getStartTime() instanceof Object) {
            JOptionPane.showMessageDialog(null, "Cant change a started job to project yet", "Eish", JOptionPane.INFORMATION_MESSAGE);
        } else {
            int project = getFreshJobCardNumber("I want to change this to a project", conn);
            String query = "UPDATE JOB SET expectedDuration =?,project =?,frequency ='n' WHERE jobCardNumber =?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setNull(1, Types.VARCHAR);
            pstmt.setInt(2, project);
            pstmt.setInt(3, jobCardNumber);
            executeUpdate(pstmt, conn);
            JDBCUtil.commit(conn);
        }
    }

    protected static int getFreshJobCardNumber(String args, Connection conn) throws SQLException {
        String query = "SELECT MAX(JOBCARDNUMBER) as number FROM JOB";
        String q1 = "SELECT MAX(project) as number FROM job";
        if (args instanceof Object) {
            query = q1;
        }
        PreparedStatement pstmt = conn.prepareStatement(query);
        ResultSet rs = databaseHelper.executeQuery(pstmt, conn);
        while (rs.next()) {
            String xc = rs.getString("number");
            if (!(xc instanceof Object)) {
                return 1;
            } else {
                return 1 + Integer.parseInt(xc);
            }
        }
        throw new RuntimeException("getFresh JCN fresh ukuthini");
    }

    private void setWorkersForAProjectJob(List<String> workers, Connection conn) throws SQLException {

        String query = "UPDATE job set status = 'p' where jobCardNumber= ?;";

        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));

        pstmt.setInt(1, jobCardNumber);
        pstmt.executeUpdate();

        SRNAlreadyInJob.stream().filter(a -> workers.contains(a)).forEach(a -> workers.remove(a));

        String queryPrefix = "INSERT INTO job (requestTime, expectedDuration, instantiator, status,"
                + " equipmentID, project, description, jobCardNumber, frequency, SRNumber) "
                + "values ( ?,'" + expectedDuration + "', '" + instantiator + "', 'p', '"
                + equipmentID + "', '" + project + "', '" + jobDescription + "', '" + jobCardNumber + "', '"
                + frequency + "', ?);";
        PreparedStatement pstmt2 = conn.prepareStatement(queryPrefix);

        System.out.println("Workers to be added "+workers);
        workers.stream().forEach(a
                -> {
            try {
                pstmt2.setTimestamp(1, Timestamp.valueOf(requestTime));
                pstmt2.setString(2, a);
                pstmt2.addBatch();
                System.out.println("added SRN " + a);

            } catch (SQLException ex) {
                JDBCUtil.rollback(conn);
                Logger.getLogger(Job.class.getName()).log(Level.SEVERE, null, ex);
            }

        });
        pstmt2.executeBatch();
        System.out.println("about to commit");
        conn.commit();

    }

    public Color getColor() {
    
        switch (status) {
            case "w"://WIP Blue
                return Color.CYAN;
            case "p"://Awaiting spares Black
                return Color.GRAY;
            case "u"://unassigned Yellow
                return Color.YELLOW;
            case "o"://overdue Red
                return Color.RED;
            case "d"://done Green
                return Color.GREEN;
            default:
                throw new RuntimeException("Got an unfamiliar status of Job");
        }
    }

    public int getProject() {
        return project;
    }
}
