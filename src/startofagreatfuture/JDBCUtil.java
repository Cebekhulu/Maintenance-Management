/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package startofagreatfuture;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCUtil {

    public static Connection getConnection(String ...args) {

        try {
            String dbURL = "jdbc:mySQL://localhost:3306/cmms?useSSL=false";
            if (args.length == 1)
                dbURL = "jdbc:mySQL://localhost:3306/meter readings?useSSL=false";
            String userId = "root";
            String password = "";
            
            Connection conn = DriverManager.getConnection(dbURL, userId, password);
            
            conn.setAutoCommit(false);
            return conn;
        } catch (SQLException sQLException) {
            sQLException.printStackTrace(System.err);
            throw new RuntimeException();
        }
        finally{
            
        }
    }

    public static void closeConnection(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeStatement(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeResultSet(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void commit(Connection conn) {
        try {
            if (conn != null) {
                conn.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void rollback(Connection conn) {
        try {
            if (conn != null) {
                conn.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Connection conn = null;
       
            conn = JDBCUtil.getConnection();
            System.out.println("Connetced to the database.");
        
    }
}
