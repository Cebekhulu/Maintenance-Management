/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author b-ok.org
 */
public class databaseHelper {

    private static Connection conn;
    private static Statement stmt;

    
    public static ResultSet executeQuery(PreparedStatement pstmt, Connection conn) throws SQLException {
        ResultSet rs = null;

        databaseHelper.conn = conn;
        rs = pstmt.executeQuery();

        return rs;

    }
    
    public static void executeUpdate(PreparedStatement q, Connection conn) throws SQLException {
        q.executeUpdate();
    }

}
