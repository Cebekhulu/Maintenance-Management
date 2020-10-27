/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package startofagreatfuture;

import static forms.databaseHelper.executeQuery;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;
//import static startofagreatfuture.PlantEquipment.connection;

/**
 *
 * @author bheki
 */
public class Artisan implements Comparable<String>{
    private String name;
    private String SRNumber;
    private Statement stmt;
    private enum Status{BUSY, FREE}
    private Status s;
    private String skillsType;
    DefaultMutableTreeNode root=new DefaultMutableTreeNode("Artisans"),
            autoMechanic= new DefaultMutableTreeNode("Auto Mechanic"),
            boilerMaker= new DefaultMutableTreeNode("Boilermakers"),
            electrician=new DefaultMutableTreeNode("Electricians"),
            fitter=new DefaultMutableTreeNode("Fitters"),
            motorMechanic =new DefaultMutableTreeNode("Motor mechanics"),
            plumber=new DefaultMutableTreeNode("Plumbers"),
            currentNode, underroot= new DefaultMutableTreeNode();
    
    public Artisan( ) {
        
    }
    public Artisan(String name, String skillsType,String SRNumber)
    {
        this.skillsType = skillsType;
        this.name = name;
        this.SRNumber = SRNumber;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void updateStatus(String status) {
        System.out.println("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        
    }

    public String getSRNumber() {
        return this.SRNumber;
    }
    
    public Status getStatus()
    {
        return s;
    }
    public int compareTo(String s)
    {
        return s.compareTo(name);
    }
    public String getSkillsType()
    {
        return this.skillsType;
    }
    public DefaultMutableTreeNode getArtisanTree()
    {
        underroot.add(root);
        root.add(autoMechanic);root.add(boilerMaker);root.add(electrician);root.add(fitter);root.add(plumber);root.add(motorMechanic);
        Connection conn =JDBCUtil.getConnection();
        PreparedStatement pstmt=null;
        try {
            pstmt = conn.prepareStatement("SELECT Name, SkillsType, SRNumber FROM `artisan` WHERE Status ='a' ORDER BY SkillsType");
        } catch (SQLException ex) {
            Logger.getLogger(Artisan.class.getName()).log(Level.SEVERE, null, ex);
        }
        ResultSet rs=null;
        try {
            rs = executeQuery(pstmt,conn);
            while (rs.next()) {
                
                Artisan pe = new Artisan(rs.getString("Name"), rs.getString("SkillsType"), rs.getString("SRNumber"));
                currentNode = new DefaultMutableTreeNode(pe);
                skillsType = pe.getSkillsType();
                switch (skillsType) {
                    case "a":
                        autoMechanic.add(currentNode);
                        break;
                    case "b":
                        boilerMaker.add(currentNode);
                        break;
                    case "e":
                        electrician.add(currentNode);
                        break;
                    case "f":
                        fitter.add(currentNode);
                        break;
                    case "m":
                        motorMechanic.add(currentNode);
                        break;
                    case "p":
                        plumber.add(currentNode);
                        break;
                }
                
            }
        } catch (SQLException e) {e.printStackTrace();
        }
        return root;}

    @Override
    public String toString() {
        return name;
    }
        
    }


