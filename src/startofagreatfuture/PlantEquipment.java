/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package startofagreatfuture;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.tree.DefaultMutableTreeNode;
import static forms.databaseHelper.executeUpdate;
import java.sql.PreparedStatement;
import java.sql.Types;

/**
 *
 * @author bheki
 */
public class PlantEquipment {

    public static Connection conn;
    private static Statement stmt;
    private String name;
    private int level;
    private String equipmentID;
    private String availability;
    DefaultMutableTreeNode root=new DefaultMutableTreeNode("Root");
    DefaultMutableTreeNode currentLevel1, currentLevel2, currentLevel3,currentLevel4,currentLevel5,currentNode;
    private DefaultMutableTreeNode currentLevel0;
    
    public PlantEquipment()
    {
        
    }
    
    public PlantEquipment(String equipmentID)
    {
        this.equipmentID=equipmentID;
    }

    public PlantEquipment(String string, String equipmentName, String availability) {
        equipmentID=string;
        name = equipmentName;
        this.availability = availability;
    }
    
    public DefaultMutableTreeNode getEquipmentTree(int ...args)
    {
        String query ="Select equipmentID, name from Equipment where status='a' order by EquipmentID";
        if (args.length==1)
            query="Select equipmentID, name from Equipment order by EquipmentID";
        try
        {
            conn= JDBCUtil.getConnection();
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next())
            {
                PlantEquipment pe = new PlantEquipment(rs.getString("EquipmentID"),rs.getString("name"),"a");
                currentNode = new DefaultMutableTreeNode(pe);
                level=getLevel(pe);
                switch(level)
                {
                    case 0:
                        currentLevel0= currentNode;
                        root.add(currentNode);
                        break;
                    case 1:
                        currentLevel1= currentNode;
                        if(currentLevel0 instanceof Object)
                        currentLevel0.add(currentNode);
                        break;
                    case 2:
                        currentLevel2 = currentNode;
                        if(currentLevel1 instanceof Object)
                        currentLevel1.add(currentNode);
                        break;
                    case 3:
                        currentLevel3 = currentNode;
                        if(currentLevel2 instanceof Object)
                        currentLevel2.add(currentNode);
                        break;
                    case 4:
                        currentLevel4 = currentNode;
                        if(currentLevel3 instanceof Object)
                        currentLevel3.add(currentNode);
                        break;
                    case 5:
                        currentLevel5 = currentNode;
                        if(currentLevel4 instanceof Object)
                        currentLevel4.add(currentNode);
                        break;
                }
                
            }
        }
        catch(Exception e)
        {
            JDBCUtil.rollback(conn);
            e.printStackTrace();
        }
        return currentLevel0;
    }
    private boolean isComponent()
    {
        return true;
    }
    private boolean isMachine()
    {
        return true;
    }
    
    public String getEquipmentID()
    {
        return equipmentID;
    }
    public static void decommission(String equipmentID, Connection conn) throws SQLException
    {
        System.out.println("Decommissioning this");
        String query = "UPDATE EQUIPMENT SET status = 'u' where equipmentID =?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, equipmentID);
        executeUpdate(pstmt, conn);
    }
    public int getLevel(PlantEquipment e)
    {
        if(Long.parseLong(e.equipmentID)==0) return 0;
        else if(Long.parseLong(e.equipmentID.substring(1))==0) return 1;
        else if(Long.parseLong(e.equipmentID.substring(4))==0) return 2;
        else if(Long.parseLong(e.equipmentID.substring(7))==0) return 3;
        else if(Long.parseLong(e.equipmentID.substring(10))==0) return 4;
        else if(Long.parseLong(e.equipmentID.substring(13))==0) return 5;
        else throw new RuntimeException("Received an unknown level");
    }
    public String getChildEquipmentID(int number){
        int level=getLevel(this);
        switch(level)
                {
                    case 0:
                        if (number<10)
                            return number+"0000000000000";
                        else if (number<100)
                            return number+"000000000000";
                        else if(number<1000)
                            return number+"00000000000";
                    case 1:
                        if(number<10)
                            return this.getEquipmentID().substring(0,3)+number+"00000000000";
                        else if (number<100)
                            return this.getEquipmentID().substring(0,2)+number+"00000000000";
                        else if(number<1000)
                            return this.getEquipmentID().substring(0,1)+number+"00000000000";
                    case 2:
                        if(number<10)
                            return this.getEquipmentID().substring(0,6)+number+"00000000";
                        else if (number<100)
                            return this.getEquipmentID().substring(0,5)+number+"00000000";
                        else if(number<1000)
                            return this.getEquipmentID().substring(0,4)+number+"00000000";
                    case 3:
                        if(number<10)
                            return this.getEquipmentID().substring(0,9)+number+"00000";
                        else if (number<100)
                            return this.getEquipmentID().substring(0,8)+number+"00000";
                        else if(number<1000)
                            return this.getEquipmentID().substring(0,7)+number+"00000";
                    case 4:
                        if(number<10)
                            return this.getEquipmentID().substring(0,6)+number+"00";
                        else if (number<100)
                            return this.getEquipmentID().substring(0,5)+number+"00";
                        else if(number<1000)
                            return this.getEquipmentID().substring(0,4)+number+"00";
                    case 5:
                        currentLevel5 = currentNode;
                        if(currentLevel4 instanceof Object)
                        currentLevel4.add(currentNode);
                        break;
                }
        throw new RuntimeException();
    }

    void commission(PlantEquipment pe, Connection conn) {
        System.out.println("Unable to commission as of yet");
    }

    public Connection getConnection() {
        return conn;
    }
    public void commitToDatabase(Connection conn)throws SQLException{
        String query1 = "INSERT INTO equipment(equipmentID,status,name,criticality)values(?,?,?,?)";
        String query2 = "CREATE TABLE `cmms`.`"+equipmentID+"` ( `Property` VARCHAR(20) NOT NULL , `Value` VARCHAR(20) NOT NULL ) ENGINE = MyISAM;";
        PreparedStatement pstmt1 = conn.prepareStatement(query1),pstmt2=conn.prepareStatement(query2);
        pstmt1.setString(1, equipmentID);
        pstmt1.setString(2, "a");
        pstmt1.setString(3, name);
        pstmt1.setNull(4, Types.VARCHAR);
        
        pstmt2.executeUpdate();
        pstmt1.executeUpdate();
        
    }
    
    @Override
    public String toString() {
        return name ;
    }

    public void setName(String x) {
        name=x;
    }
    
    
}
