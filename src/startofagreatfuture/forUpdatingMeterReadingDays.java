/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package startofagreatfuture;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Expression Bheki is undefined on line 12, column 14 in Templates/Classes/Class.java.
 */
public class forUpdatingMeterReadingDays {
    int index = -1;
    String dateQuery = "SELECT date FROM water";
    String dbURL = "jdbc:mySQL://localhost:3306/meter readings?useSSL=false";
    String userId = "root";
    String password = "";
    Connection conn;

    void method() throws SQLException {
        conn = DriverManager.getConnection(dbURL, userId, password);
        conn.setAutoCommit(false);
        PreparedStatement pstmt = conn.prepareStatement(dateQuery);
        List<LocalDateTime> dateList = new ArrayList<>();
        ResultSet rs = forms.databaseHelper.executeQuery(pstmt, conn);
        while (rs.next()) {
            dateList.add(rs.getTimestamp("Date").toLocalDateTime());
        }

        List<List<Object>> LocalDateList = new ArrayList<>();
        dateList.stream()
                .map(a -> a.plusDays(588L))
                .forEach(a -> {
                    ++index;
                    List m = Arrays.asList(a, dateList.get(index));
                    LocalDateList.add(m);
                });
        String replacementQuery = "UPDATE water SET date =? where date =?";
        PreparedStatement pstmt11 = conn.prepareStatement(replacementQuery);
        
        LocalDateList.stream().forEach(a -> {
            try {
                pstmt11.setTimestamp(1, Timestamp.valueOf((LocalDateTime) a.get(0)));
                pstmt11.setTimestamp(2,  Timestamp.valueOf((LocalDateTime)a.get(1)));
                pstmt11.addBatch();
                pstmt11.executeBatch();
            } catch (SQLException ex) {
                Logger.getLogger(thisClassIsSolelyForUpdatingDatesInMeterReadings.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        System.out.println(pstmt11);

        LocalDateList.stream().forEach(a -> {
            System.out.println("Format " + (LocalDateTime) a.get(0) + " String " + a.get(1));
        });
    }

    public static void main(String[] args) {
        try {
            new forUpdatingMeterReadingDays().method();
            JDBCUtil.commit(new forUpdatingMeterReadingDays().conn);
        } catch (SQLException ex) {
            JDBCUtil.rollback(new forUpdatingMeterReadingDays(). conn);
            Logger.getLogger(forUpdatingMeterReadingDays.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
