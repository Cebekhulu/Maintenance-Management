/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package forms;

import static forms.databaseHelper.executeQuery;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import startofagreatfuture.Dates;
import startofagreatfuture.JDBCUtil;
import startofagreatfuture.Job;

/**
 *
 * @author Expression Bheki is undefined on line 12, column 14 in Templates/Classes/Class.java.
 */
public class AngieList {
    public static Vector<String> headerFile;
    List<String> headerDetails = Arrays.asList("Name","# Of Jobs","Status","Trade","Utilisation","Speed");
    public static Vector<Vector<Object>> workerSummaryList= new Vector<>();
    List<List<Object>> dataList = new ArrayList<>();
    List<String> nameList = new ArrayList<>();
    Connection conn;
    
    public AngieList()
    {
        headerFile = new Vector(headerDetails);
        fetchData();
    }

    private void fetchData() {
        conn=JDBCUtil.getConnection();
        String query = "SELECT name, "
                + "(SELECT COUNT(job.jobCardNumber) FROM job WHERE job.SRNumber = artisan.SRNumber) "
                + "AS jobs, Status, skillsType as trade FROM `artisan`";
            PreparedStatement pstmt=null;
        try {
            pstmt = conn.prepareStatement(query);
        } catch (SQLException ex) {
            Logger.getLogger(AngieList.class.getName()).log(Level.SEVERE, null, ex);
        }
        ResultSet rs=null;
        try {
            rs = executeQuery(pstmt,conn);
        } catch (SQLException ex) {
            Logger.getLogger(AngieList.class.getName()).log(Level.SEVERE, null, ex);
        }
        try{
            while(rs.next()){
                Vector<Object> v = new Vector<>();
                String name = rs.getString("name");
                v.add(name);
                v.add(rs.getInt("jobs"));
                v.add(rs.getString("Status"));
                v.add(rs.getString("trade"));
                workerSummaryList.add(v);
                nameList.add(name);
            }
        }
        catch(SQLException e){
            e.printStackTrace();JDBCUtil.closeConnection(conn);
        }
        finally{}
        
        
        String timesQuery = "SELECT artisan.Name, startTime, job.stopTime, job.expectedDuration "
                + "from job INNER JOIN artisan ON artisan.SRNumber=job.SRNumber "
                + "WHERE job.SRNumber = (SELECT artisan.SRNumber from artisan where job.SRNumber=artisan.SRNumber)";
        PreparedStatement pstmt2=null;
        ResultSet rs1 =null;
        try {
            pstmt2 = conn.prepareStatement(timesQuery);
            rs1 = executeQuery(pstmt2,conn);
                    
            while (rs1.next()) {
                Vector<Object> a = new Vector<>();
                a.add(rs1.getString("name"));
                try{
                a.add(rs1.getTimestamp("startTime").toLocalDateTime());
                }
                catch(RuntimeException e){
                    a.add(LocalDateTime.now());
                }
                String stp = rs1.getString("stopTime");
                if (stp instanceof Object) {
                    a.add(rs1.getTimestamp("stopTime").toLocalDateTime());
                } else {
                    a.add(LocalDateTime.now());
                }
                a.add(rs1.getString("expectedDuration"));
                dataList.add(a);
            }
        } catch (SQLException e) {
            JDBCUtil.rollback(conn);
            e.printStackTrace();
        }
        finally{JDBCUtil.closeConnection(conn);}
        Comparator<List> byStartTime= (a,b)->{
            return (((LocalDateTime)(((Vector)a).get(1))).compareTo((LocalDateTime)(((Vector)b).get(1))));
        };
        Comparator<List> byEndTime= (a,b)->{
            return (((LocalDateTime)(((Vector)a).get(2))).compareTo((LocalDateTime)(((Vector)b).get(2))));
        };
        Double totalAvailableTime;
        
        if (!dataList.isEmpty()) {
            LocalDateTime startTime = (LocalDateTime) (dataList.stream().min(byStartTime).get().get(1));
            LocalDateTime endTime = (LocalDateTime) (dataList.stream().max(byEndTime).get().get(1));
            totalAvailableTime = (double) ChronoUnit.DAYS.between(startTime, endTime) * 8;

            //for every artisan name, calculate sum of times differences
            for (int currentWSLIndex = 0; nameList.size() > currentWSLIndex; currentWSLIndex++) {
                Double totalHours = 0.0;
                Double forTimesilenessHours = 1.0;
                
                for (List<Object> c : dataList) {
                    //Calculating time differences
                    if (c.get(0).equals(nameList.get(currentWSLIndex))) {
                        LocalDateTime start = (LocalDateTime) c.get(1);
                        Dates e = new Dates(start, (LocalDateTime) c.get(2));
                        totalHours += e.getHourDifference();
                        
                        Dates f = new Dates(start, Job.getExpectedEndTime((String) (c.get(3)), start));
                        forTimesilenessHours += f.getHourDifference();
                        
                    }
                }
                workerSummaryList.get(currentWSLIndex).add(totalHours / totalAvailableTime);
                workerSummaryList.get(currentWSLIndex).add(totalHours / forTimesilenessHours);
                
            }
        }
        
    }

}
