/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package startofagreatfuture;

import forms.HomeScreen;
import static forms.databaseHelper.executeQuery;
import forms.statPanel;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author bheki
 */
public class Stats {

    private double MTBF;
    private Long totalWorkHours = 0L;
    long numberOfHours;
    Connection conn;

    List<statPanel> myJ = new ArrayList<>();

    public Stats() {
    }

    public Stats(Connection conn) {
        this.conn = conn;
        System.out.println("Inside stats at "+LocalDateTime.now());

        try {
            myJ.add(new statPanel("Overdue jobs", "r", getOverdueJobs()));
            myJ.add(new statPanel("Outstanding jobs", "r", getOutstandingJobs()));
            myJ.add(new statPanel("Mean reaction time in hours", "r", getMeanReactionTime()));
            myJ.add(new statPanel("Critical equipment Available", "g", 7, 8));
            myJ.add(new statPanel("Idle artisans", "r", 6,30));
            myJ.add(new statPanel("Mean reaction time in hours", "r", getMeanReactionTime()));
            myJ.add(new statPanel("Critical equipment Available", "g", 7, 8));
            myJ.add(new statPanel("Available Artisans", "r", 6,30));
            myJ.add(new statPanel("Mean reaction time in hours", "r", getMeanReactionTime()));
        } catch (SQLException e) {
            JDBCUtil.rollback(conn);
            e.printStackTrace();
        }

        myJ.forEach(a -> {
            SwingUtilities.invokeLater(() -> HomeScreen.jPanel15.add(a));
        });

    }

    public static double getMTBF() throws SQLException {

        return 1.0;
    }

    public int getOverdueJobs() throws SQLException {
        UpdateHomeList u = new UpdateHomeList(8);
        long count = u.fromJDBC
                .stream()
                .filter(a -> (a.getSOJ()).equals("o"))
                .count();

        /*LocalDateTime now  = LocalDateTime.now();
        String query = "SELECT COUNT(jobCardNumber) FROM job WHERE ?<?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setTimestamp(1, Timestamp.valueOf(now));
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        int overdueJobs = rs.getInt("COUNT(jobCardNumber)");*/
        return (int) count;
    }

    public Long getTotalWorkHours() {
        return totalWorkHours;
    }

    public long getNumberOfHours() {
        return numberOfHours;
    }

    public int getOutstandingJobs() throws SQLException {
        String query = "SELECT COUNT(jobCardNumber) FROM job "
                + "WHERE startTime>0 AND stopTime IS NULL";

        PreparedStatement pstmt = conn.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        int overdueJobs = rs.getInt("COUNT(jobCardNumber)");

        return overdueJobs;
    }

        public double getMeanReactionTime() throws SQLException {
        String query = "SELECT requestTime, startTime FROM job "
                + "WHERE startTime>0 and project = 0 AND FREQUENCY ='n'";
        PreparedStatement pstmt = conn.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery();
        double total = 0;
        double minutes = 0;
        while (rs.next()) {
            LocalDateTime request = 
                    rs.getTimestamp("requestTime").toLocalDateTime();
            LocalDateTime start = 
                    rs.getTimestamp("startTime").toLocalDateTime();
            minutes += Math.abs(ChronoUnit.MINUTES.between(request, start));
            ++total;
        }
        System.out.println("total " + total + "\nMinutes " + minutes);
        return (minutes / (60 * total));
    }

    public float getReactiveWorkPercentage() throws SQLException {
        return 0.1f;
    }

    public int getLabourUtilisation() //<editor-fold defaultstate="collapsed" desc="get labour Usage method">
    {

        List<Dates> listOfDates = new ArrayList<>();
        String query = "SELECT DISTINCT jobCardNumber, startTime, stopTime FROM job WHERE expectedDuration>0 AND startTime>0 AND status !='p'";
        Connection connection = JDBCUtil.getConnection();
        PreparedStatement prepareStatement = null;
        try {
            prepareStatement = connection.prepareStatement(query);
        } catch (SQLException ex) {
            JDBCUtil.rollback(connection);
            Logger.getLogger(Stats.class.getName()).log(Level.SEVERE, null, ex);
        }
        ResultSet rs = null;
        try {
            rs = executeQuery(prepareStatement, connection);
        } catch (SQLException ex) {
            Logger.getLogger(Stats.class.getName()).log(Level.SEVERE, null, ex);
        }
        Comparator<Dates> byStartTime = (a, b) -> (a.getStart()).compareTo(b.getStart());
        Comparator<Dates> byEndTime = (a, b) -> (a.getEnd()).compareTo(b.getEnd());
        try {
            while (rs.next()) {
                LocalDateTime now = LocalDateTime.now();
                try {
                    now = rs.getTimestamp("stopTime").toLocalDateTime();
                } catch (NullPointerException e) {
                }
                listOfDates.add(new Dates(rs.getTimestamp("startTime").toLocalDateTime(), now));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (!(listOfDates.isEmpty())) {
            LocalDateTime maxTime = listOfDates.stream().max(byEndTime).get().getEnd();
            LocalDateTime minTime = listOfDates.stream().min(byStartTime).get().getStart();

            numberOfHours = 13 * 8 * ChronoUnit.DAYS.between(minTime, maxTime);
            if (numberOfHours == 0) {
                numberOfHours = ChronoUnit.HOURS.between(minTime, maxTime);
            }
            if (numberOfHours == 0) {
                return 0;
            }

            //Calculating work hours
            for (Dates thisDate : listOfDates) {
                totalWorkHours += thisDate.getHourDifference();
            }

            return (int) (100 * totalWorkHours / numberOfHours);
        } else {
            return 0;
        }
    }
//</editor-fold>

    public double getArtisanUtilisation(List<Dates> timesList) {

        throw new RuntimeException();
    }

}
