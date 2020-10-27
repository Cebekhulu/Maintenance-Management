/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forms;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import javax.swing.JTextArea;

/**
 *
 * @author b-ok.org
 */
public class statPanel extends javax.swing.JPanel{
    int totalWidth, totalHeight;
    JTextArea gu = new JTextArea();
    double[] valueAndTotalArgs;
    double value;
    double total;
    int topCornerX;
    int topCornerY;
    Color green = new Color(0,204,0);
    Color maroon = new Color(204,0,0);
    String cr;
    
    public statPanel(String textTop,String criticality, double...valueAndTotalArgs){
        totalWidth=(((HomeScreen.jPanel15.getSize().width)/2)-4)>100?(((HomeScreen.jPanel15.getSize().width)/2)-10):300;
        totalHeight=(int) (totalWidth/1.61803399);
        gu.setWrapStyleWord(true);
        gu.setLineWrap(true);
        gu.setText(textTop);
        gu.setSize(totalWidth,(int)(totalHeight/1.61803399));
        gu.setBackground(Color.LIGHT_GRAY);
        gu.setEditable(false);
        gu.setEnabled(false);
        
        
        this.valueAndTotalArgs=valueAndTotalArgs;
        this.add(gu);
        this.setBackground(Color.WHITE);
        this.setPreferredSize(new java.awt.Dimension(totalWidth,totalHeight));
        
        value=valueAndTotalArgs[0];
        if(valueAndTotalArgs.length==2){
            total=valueAndTotalArgs[1];
        }
        
        cr= criticality;
        
        
    }
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        System.out.println("Inside paintcomponent");
        g.setColor(Color.LIGHT_GRAY);
        
        int Diameter = (int)(0.5*totalHeight);
        g.fillRect(0, 0, totalWidth, (int)((double)totalHeight/3));
        
        topCornerY=(int)(((double)5/12)*(double)totalHeight);
        topCornerX=(int)(totalWidth*0.5 -0.5*Diameter);
        
        
        
        
        if(valueAndTotalArgs.length==2){
        g.setColor(Color.LIGHT_GRAY);
        g.fillOval(topCornerX,topCornerY,Diameter,Diameter);
        
        g.setColor(Color.WHITE);
        g.fillOval(topCornerX+8,topCornerY+8 ,Diameter-16,Diameter-16);
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(cr.equals("r")?maroon:green);
        g2d.setStroke(new BasicStroke(8.0f));
        g2d.draw(new Arc2D.Double(topCornerX+4,topCornerY+4,Diameter-8,Diameter-8,90,(int)(360*value/total),Arc2D.OPEN));
        
        FontMetrics fm= g.getFontMetrics();
        
        g.setColor(Color.GRAY);
        g.setFont(new Font("Centaur",Font.PLAIN,15));
        
        int textWidth = fm.stringWidth(value+"");
        
        g.drawString((value+"").contains(".")?(value+"").substring(0,(value+"").indexOf(".")+2):value+"", (int) (topCornerX+Diameter/2d-textWidth/2d), (int)( topCornerY+Diameter/2d));
        
        g.setFont(new Font("Calibri",Font.PLAIN,12));
        
        textWidth = fm.stringWidth("of "+total);
        
        g.drawString("of "+total, topCornerX+Diameter/2-textWidth/2, topCornerY+Diameter/2+18);
        
        
        }
        else{
            g.setFont(new Font("Centaur",Font.PLAIN, Diameter/2));
            FontMetrics fm = g.getFontMetrics();
            int textWidth=fm.stringWidth((value+"").contains(".")?(value+"").substring(0,(value+"").indexOf(".")+2):value+"");
            g.drawString((value+"").contains(".")?(value+"").substring(0,(value+"").indexOf(".")+2):value+"", (int) (totalWidth/2d-textWidth/2d), topCornerY+Diameter);
            
        }
        
    }
    
}
