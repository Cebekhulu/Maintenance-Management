/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package startofagreatfuture;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

/**
 *
 * @author bheki
 */
public class ListOfThingsRenderer extends DefaultListCellRenderer
{
    protected DefaultListCellRenderer defaultRenderer =new DefaultListCellRenderer();
    protected static Border cellsBorder = new EmptyBorder(10,10,10,10);
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean iss, boolean chf)
    {
        super.getListCellRendererComponent(list, value, index, iss, chf);
        JLabel renderer = (JLabel)defaultRenderer.getListCellRendererComponent(list, value, index, iss, chf);
        renderer.setBorder(cellsBorder);
        renderer.setVisible(true);
             
        return renderer;
    }
}
