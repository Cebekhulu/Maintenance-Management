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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;
import startofagreatfuture.JDBCUtil;
import startofagreatfuture.PlantEquipment;

/**
 *
 * @author bheki
 */
public class SelectEquipment extends HomeScreen{

    /**
     * Creates new form newJobp
     */
    public static PlantEquipment selection = new PlantEquipment();
    public static Connection conn;
    Vector<Vector<String>> myvuvu = new Vector<>(10);
    Vector<String> propertyVectorHeadings = new Vector<>();

    public SelectEquipment() {
        super(1);
        propertyVectorHeadings.add("Property");
        propertyVectorHeadings.add("Value");
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        equipmentTree = new javax.swing.JTree(selection.getEquipmentTree());
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        addSubComponentButton = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocation(new java.awt.Point(600, 300));

        conn = selection.getConnection();
        TreeSelectionModel tsm = equipmentTree.getSelectionModel();
        tsm.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        equipmentTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                equipmentTreeValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(equipmentTree);

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton1.setText("Select Component");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Cancel");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        addSubComponentButton.setText("Add SubComponent");
        addSubComponentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSubComponentButtonActionPerformed(evt);
            }
        });

        jButton4.setText("Edit Component");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton4)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2)
                    .addComponent(addSubComponentButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {addSubComponentButton, jButton1, jButton2, jButton4});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4)
                    .addComponent(addSubComponentButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTable2.setModel(new javax.swing.table.DefaultTableModel(myvuvu, propertyVectorHeadings) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            @Override
            public int getRowCount() {
                return myvuvu.size();
            }

            @Override
            public int getColumnCount() {
                return 2;
            }
            @Override
            public String getColumnName(int columnIndex) {
                return propertyVectorHeadings.get(columnIndex);
            }
            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return myvuvu.get(rowIndex).get(columnIndex);
            }

            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.setDoubleBuffered(true);
        jScrollPane3.setViewportView(jTable2);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 490, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 261, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        JDBCUtil.rollback(conn);
        JDBCUtil.closeConnection(conn);
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) equipmentTree.getLastSelectedPathComponent();
        selection = (PlantEquipment) node.getUserObject();
        workItemButton.setText(selection.toString());
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void equipmentTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_equipmentTreeValueChanged
        // TODO add your handling code here:
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) equipmentTree.getLastSelectedPathComponent();
        PlantEquipment pe = (PlantEquipment) node.getUserObject();
        workItemButton.setText(selection.toString());
        Connection connie = JDBCUtil.getConnection();
        String query = "SELECT * FROM `" + pe.getEquipmentID() + "`";

        myvuvu.clear();
        try {
            PreparedStatement privy = connie.prepareStatement(query);
            System.out.println(privy);
            ResultSet rs = executeQuery(privy, connie);
            while (rs.next()) {
                Vector<String> myV = new Vector<>();
                myV.add(rs.getString("property"));
                myV.add(rs.getString("value"));
                myvuvu.add(myV);
            }
            SwingUtilities.invokeLater(() -> {
                jTable2.repaint();
                jScrollPane3.repaint();
                jPanel1.repaint();
                jTable2.repaint();
                jScrollPane3.repaint();
                jPanel1.repaint();
            });

            System.out.println(myvuvu);
        } catch (SQLException ex) {
            System.out.println("Unknowns ought to be getting caught here");
            node = (DefaultMutableTreeNode) equipmentTree.getLastSelectedPathComponent();
            pe = (PlantEquipment) node.getUserObject();

            String x = JOptionPane.showInputDialog(this, "What's the component's name", "Insert Component", JOptionPane.QUESTION_MESSAGE);
            pe.setName(x);
            Connection conn = JDBCUtil.getConnection();
            try {
                pe.commitToDatabase(conn);
                JDBCUtil.commit(conn);
            } catch (SQLException ex1) {
                JDBCUtil.rollback(conn);
                Logger.getLogger(SelectEquipment.class.getName()).log(Level.SEVERE, null, ex1);
            }

        }
    }//GEN-LAST:event_equipmentTreeValueChanged

    private void addSubComponentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSubComponentButtonActionPerformed
        // TODO add your handling code here:
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) equipmentTree.getLastSelectedPathComponent();
        PlantEquipment pe = (PlantEquipment) node.getUserObject();
        String equipmentID = null;
        List<String> myList = new ArrayList<>();
        Enumeration<TreeNode> children = node.children();
        while (children.hasMoreElements()) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) children.nextElement();
            PlantEquipment pes = (PlantEquipment) child.getUserObject();
            myList.add(pes.getEquipmentID());

        }
        boolean bool = true;
        int counter = 1;
        while (bool) {
            if (myList.contains(pe.getChildEquipmentID(counter))) {
                counter = counter + 1;
                System.out.println("pe.getChildEquipmentID(counter)" + pe.getChildEquipmentID(counter) + "counter" + counter);
            } else {
                equipmentID = pe.getChildEquipmentID(counter);
                System.out.println(equipmentID + "This is the EID");
                bool = false;

            }
        }

        node.add(new DefaultMutableTreeNode(new PlantEquipment(equipmentID, "Edit name of this component", "a")));
        equipmentTree.expandPath(equipmentTree.getSelectionPath());
        SwingUtilities.invokeLater(() -> jPanel1.repaint());
        
        SwingUtilities.invokeLater(() -> {equipmentTree.repaint();
        equipmentTree.collapsePath(equipmentTree.getSelectionPath());
        
        equipmentTree.repaint();
        jScrollPane3.repaint();
        jPanel1.repaint();
        equipmentTree.expandPath(equipmentTree.getSelectionPath());
        });
        equipmentTree.repaint();
        jScrollPane3.repaint();
    }//GEN-LAST:event_addSubComponentButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Windows look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SelectEquipment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SelectEquipment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SelectEquipment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SelectEquipment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SelectEquipment().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addSubComponentButton;
    javax.swing.JTree equipmentTree;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable2;
    // End of variables declaration//GEN-END:variables
}
