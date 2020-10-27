/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package startofagreatfuture;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Expression Bheki is undefined on line 12, column 14 in
 * Templates/Classes/Class.java.
 */
public class EquipmentRepairsTable extends JTextArea implements TableCellRenderer {

    List<List<Object>> resultSetData = new ArrayList<>();
    static Vector<Vector<Object>> forTheTable = new Vector<>();
    public static Vector<String> headerFile = new Vector<>();
    List<Dates> myDatesList = new ArrayList<>();
    double totalAvailableTime;
    Dates myDatesWithAngelica = new Dates();
    String currentEquipment = "";
    Set<String> names = new HashSet<>();
    private String period;
    public static final DefaultTableCellRenderer DEFAULT_RENDERER
            = new DefaultTableCellRenderer();

    List<String> n= new ArrayList<>();
    List<List<String>> critEqpmtID = new ArrayList<>();
    List<String> critEqpmtIDLikes
            = Arrays.asList("'1001%'", "'1002%'", "'1003%'", "'1004%'", "'2001%'",
                    "'2002%'", "'2004%'", "'3001%'", "'3002%'", "'3003%'", "'3004%'");
    private Color currentColour = Color.WHITE;
    

    public EquipmentRepairsTable(String period) {
        setLineWrap(true);
        setWrapStyleWord(true);
        Font font = new Font("Tahoma",Font.PLAIN,11);
        setFont(font);
        setEditable(false);
        this.period = period;
        headerFile.clear();
        headerFile.add("Equipment");
        headerFile.add("Availability");
        headerFile.add("Nature Of Problem");
        headerFile.add("Lost Time");
        headerFile.add("Status");
        forTheTable.clear();
        extractData();
    }

    public static Vector<Vector<Object>> getForTheTable() {
        return forTheTable;
    }

    public void extractData() {
        StringBuilder query = new StringBuilder("SELECT DISTINCT equipment.name,equipment.equipmentID,"
                + "equipment.status, job.requestTime, job.stopTime, job.description\n"
                + "FROM equipment\n"
                + "INNER JOIN job \n"
                + "ON equipment.equipmentID=job.equipmentID\n"
                + "WHERE equipment.criticality>1\n"
                + "AND (job.equipmentID LIKE " + critEqpmtIDLikes.get(0));
        critEqpmtIDLikes.stream().skip(1).forEach(a -> query.append("OR job.equipmentID LIKE " + a));
        query.append(") ORDER BY job.equipmentID");
        Connection conn = JDBCUtil.getConnection();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                List<Object> rowData = new ArrayList<>();
                rowData.add(rs.getString("equipmentID"));
                rowData.add(rs.getString("name"));
                names.add(rs.getString("name"));
                rowData.add(rs.getString("status"));
                LocalDateTime stopTime = null;
                try {
                    stopTime = rs.getTimestamp("stopTime").toLocalDateTime();
                } catch (NullPointerException e) {
                    stopTime = LocalDateTime.now();
                }

                Dates d = new Dates(rs.getTimestamp("requestTime").toLocalDateTime(), stopTime);
                myDatesList.add(d);
                rowData.add(d);
                rowData.add(rs.getString("description"));
                resultSetData.add(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        myDatesWithAngelica.setListOfDates(myDatesList);

        myDatesWithAngelica.trimUnwantedTime(period);
        totalAvailableTime = (double) myDatesWithAngelica.getTotalTime();
        List<Integer> indices = new ArrayList<>();
        myDatesWithAngelica.listOfDates.stream().forEach(a -> {
            for (List myL : resultSetData) {
                if (!(myL.contains(a))) {
                    indices.add(resultSetData.indexOf(myL));
                }
            }
        });
        indices.forEach(a -> resultSetData.remove(a));
        for (List myList : resultSetData) {
            long ds = ((Dates) (myList.get(3))).getHourDifference();
            myList.add(ds);
            double availability = ((totalAvailableTime - ds) / totalAvailableTime);
            myList.add(availability);
        }

        
        for (List ml : resultSetData) {
            ml.set(4, ((String) ml.get(1)).trim()/*.strip()*/ + ": " + ml.get(4));
        }
        String anotherQuery = "SELECT equipment.equipmentID, equipment.name FROM equipment WHERE criticality = 3";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(anotherQuery);
            while (rs.next()) {
                List<String> mm = Arrays.asList(rs.getString("equipmentID"), rs.getString("name"));
                critEqpmtID.add(mm);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        critEqpmtID.forEach(a -> {
            for (List m : resultSetData) {
                if ((((String) (m.get(0))).substring(0, 4)).equals((String) (a.get(0)).substring(0, 4))) {
                    m.set(1, a.get(1).trim()/*.strip()*/);
                }
            }
        });
        //taking equipment IDs that are in the rs and saving in a list
        List<List<Object>> copy = new ArrayList<>(resultSetData);
        List<String> los = new ArrayList<>();
        copy
                .stream()
                .flatMap(a -> a.subList(0, 1).stream())
                .forEach(a -> los.add(a.toString()));
        //Inserting critical eqpmnt without jobs into
        critEqpmtID
                .stream()
                .filter(a -> !los.contains(a.get(0)))
                .forEach(b -> {
                    List<Object> obj = new ArrayList();
                    obj.add(b.get(0));
                    obj.add(b.get(1));//equipment name
                    obj.add("a");
                    obj.add(null);
                    obj.add("No breakdowns");
                    obj.add("0");
                    obj.add(1.0);//availability is 1 since there isnt any jobs for this
                    resultSetData.add(0, obj);
                });
        Collections.sort(resultSetData, (List a, List b) -> {
            return ((String) (a.get(0))).compareTo((String) b.get(0));
        });
        //Making generalisation of equipment names
        for (List myL : resultSetData) {
            if (myL.get(1).toString().length() > 1) {
                if (((String) (myL.get(1))).substring(0, 3).equals("PC ")) {
                    myL.set(1, "PCs");
                } else if (((String) (myL.get(1))).substring(0, 3).equals("FER")) {
                    myL.set(1, "FERMS");
                } else if (((String) (myL.get(1))).substring(0, 3).equals("LAP")) {
                    myL.set(1, "LAPCs");
                } else if (((String) (myL.get(1))).substring(0, 3).equals("LAG")) {
                    myL.set(1, "LAGs");
                } else if (((String) (myL.get(1))).substring(0, 3).equals("Sug")) {
                    myL.set(1, "Sugar Tanks");
                } else if (((String) (myL.get(1))).substring(0, 8).equals("Malt Bin")) {
                    myL.set(1, "G Boxes");
                }
            }
        }

        String whatsAlreadyIn = "";
        boolean bool = true;
        for (List myL : resultSetData) {
            if (((String) myL.get(1)).equals(whatsAlreadyIn)) {
                whatsAlreadyIn = (String) myL.get(1);
                myL.set(1, "");
            } else if (bool) {
                whatsAlreadyIn = (String) myL.get(1);
                bool = false;
            } else {
                whatsAlreadyIn = (String) myL.get(1);
            }

        }
        copy = new ArrayList<>(resultSetData);
        copy
                .stream()
                .filter(a -> a instanceof Object)
                .filter(a -> {
                    return a.get(1).toString().equals("") && !(a.get(3) instanceof Object);
                })
                .forEach(a -> resultSetData.remove(a));

        
        //Finally decorating the table
        resultSetData.stream().forEach(a -> {
            Vector<Object> v = new Vector<>();
            v.add(a.get(1));
            v.add(a.get(6));
            v.add(a.get(4));
            v.add(a.get(5) + " Hours");
            v.add(a.get(2));
            forTheTable.add(v);
        });
    }
    void refreshTableZebra(){
        for(List b:forTheTable){
            if(((String)b.get(0)).length()>1){
                switchColour(currentColour);
                tableZebra.add(currentColour);
            }
            else tableZebra.add(currentColour);
        }
    }

    public static void main(String[] args) {
        EquipmentRepairsTable ert = new EquipmentRepairsTable("m");
    }
    List<Color> tableZebra =new ArrayList<>();

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        Component renderer
                = DEFAULT_RENDERER.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
        Color foreground = Color.BLACK, background;
       

        renderer.setForeground(foreground);
        Angelica: if(forTheTable.size()==tableZebra.size()){
        renderer.setBackground(tableZebra.get(row));
        }
        else {refreshTableZebra();
        break Angelica;}
        
        return renderer;
    }

    int currentRowNumber=0;
    Color getAboveColor(int row) {
        if(row==0)currentRowNumber=0;
        
        if(row ==currentRowNumber){return currentColour;}
        else if ((n.get(row).length()>1)) {
            while(row==currentRowNumber){
                return currentColour;
            }
            ++currentRowNumber;
            return switchColour(currentColour);
        } else {
            ++currentRowNumber;
            return currentColour;
        }
    }

    Color switchColour(Color b) {
        if (b.equals(Color.WHITE)) {
            currentColour = new Color(250,250,250);
            return currentColour;
        } else {
            currentColour = Color.WHITE;
            return Color.WHITE;
        }
    }
}
