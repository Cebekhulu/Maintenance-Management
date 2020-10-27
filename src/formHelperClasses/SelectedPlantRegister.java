/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formHelperClasses;

import static forms.GanttChart.getFriendlyTime;
import static forms.databaseHelper.executeQuery;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import startofagreatfuture.JDBCUtil;

/**
 *
 * @author b-ok.org
 */
public class SelectedPlantRegister {
    //
static long totalRepairTime = 0;
    /**
     *Fill out maintenance tab;Specs tab;Components and general details.
     * 
     * @param equipmentID SELECT * FROM `pe.getEquipmentID`;
     * @return
     */
    public static Map getMaintenanceDetails(String equipmentID){
        Connection connie=JDBCUtil.getConnection();
        
        HashMap myzuzu = new HashMap();
        List repairDates =new ArrayList<ArrayList<LocalDateTime>>();
        
        //Filling out general details and specs regarding the item
        try {
            PreparedStatement privy = connie.prepareStatement("SELECT * FROM `" + equipmentID+ "`");
            ResultSet rs = executeQuery(privy, connie);
            while (rs.next()) {
                myzuzu.put(rs.getString("property"),rs.getString("value"));
            }
            

        }catch(SQLException e){
            e.printStackTrace();
        }
        
        //Listing dates of jobs to calculate MTBF; MTTR and last repair date
        try{
            String quey="SELECT DISTINCT job.jobCardNumber, job.startTime,"
                    + "job.stopTime FROM job "
                    + "WHERE job.startTime > ? AND job.equipmentID = ?";
            System.out.println(quey);
            
            PreparedStatement pstmt= connie.prepareStatement(quey);
            
            pstmt.setString(2, equipmentID);
            pstmt.setTimestamp(1,Timestamp.valueOf(
                    LocalDateTime.now()
                            .withMonth(1)
                            .withDayOfMonth(1)
                            .withHour(0)
                            .withMinute(0)));
            
            
            ResultSet res = executeQuery(pstmt,connie);
            while(res.next()){
                repairDates.add(Arrays.asList(
                        res.getTimestamp("startTime").toLocalDateTime(),
                        res.getTimestamp("stopTime").toLocalDateTime()
                
                ));
            }
            
            
            
        }catch(SQLException e){
            e.printStackTrace();
        }
        
        //Calculating mean time between failures
        //Total amount of time divided by number of failures
        long noOfRepairs=repairDates
                .stream()
                .filter(
                        a->((List<LocalDateTime>)a)
                                .get(0)
                                .isAfter(LocalDateTime.of(LocalDate.now().withDayOfYear(1), LocalTime.MIN)))
                .count();
        long timeDiff = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.now().withDayOfYear(1));
        
        long mtbf = timeDiff/noOfRepairs;
        
        myzuzu.put("MTBF",mtbf);
        
        //Calculating mean time to repair
        //Total repair time divided by number of repairs
        
        
        
        repairDates
                .stream()
                .filter(
                        a->((List<LocalDateTime>)a)
                                .get(0)
                                .isAfter(LocalDateTime.of(LocalDate.now().withDayOfYear(1), LocalTime.MIN)))
                .forEach(a->totalRepairTime+=ChronoUnit.HOURS.between(((List<LocalDateTime>)a).get(0),((List<LocalDateTime>)a).get(1)));
                
        long MTTR = totalRepairTime/noOfRepairs;
        
        myzuzu.put("MTTR", MTTR);
        
        //Last repair and artisan
        //Select artisan.name, job.startTime from job INNER JOIN ARTISAN
        //ON artisan.SRNumber = job.SRNumber WHERE job.equipmentID =?
        //ORDER BY id DESC top 1/I dont know how to select only 1 result
        
        String masQ = "SELECT artisan.name, job.startTime FROM job INNER "
                + "JOIN ARTISAN ON artisan.SRNumber = job.SRNumber WHERE "
                + "job.equipmentID =? ORDER BY no DESC";
        try{
            PreparedStatement ps = connie.prepareStatement(masQ);
            ps.setString(1, equipmentID);
            ResultSet rs = executeQuery(ps,connie);
            rs.next();
            String artisanName = rs.getString("name");
            String lastWorked=
                    getFriendlyTime(rs.getTimestamp("startTime").toLocalDateTime());
            myzuzu.put("Last Repair", lastWorked);
            myzuzu.put("Artisan", artisanName);
        }catch(SQLException e){
            e.printStackTrace();
        }
        
        return myzuzu;
    }
    
}
