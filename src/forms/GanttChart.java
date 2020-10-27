/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forms;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import static java.lang.Math.abs;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import javax.swing.JPanel;
import startofagreatfuture.Job;
import startofagreatfuture.Project;

/**
 *
 * @author b-ok.org
 */
public class GanttChart extends JPanel {

    LocalDateTime projectStart;
    LocalDateTime projectEnd;
    Long totalDuration;
    int totalWidth = /*HomeScreen.projectGantt.getWidth();*/ 200;

    int totalHeight;

    private int buffer = 3;
    private int preferredHeight =/*HomeScreen.projectGantt.getHeight();*/ 100;
    int xCord;
    private int remainingHeight;
    private int remainingWidth  ;

    Stream s;
    List<Job> args;
    Comparator<Job> startTimeComparator;
    double itemHeight;
    Job a;

    /*public GanttChart(Job job)
    {
    System.out.println("No ConsArgs constructor in gantt");
    }*/
    public GanttChart(List ConsArgs) {
        if(ConsArgs.size()==0)return;
        totalWidth = HomeScreen.JobGantt.getWidth() - 2;
        totalHeight = HomeScreen.JobGantt.getHeight() - 20;
        this.args = ConsArgs;
        preferredHeight = HomeScreen.JobGantt.getHeight();
        remainingHeight = preferredHeight - 16;
        remainingWidth = totalWidth - 2 * buffer;

        s = ConsArgs.stream();
        a = (Job) ConsArgs.get(0);

        //For uninitialised jobs and projects
        if (!(a.getStartTime() instanceof Object)) {
            startTimeComparator = ((a, b)
                    -> {
                return ((int) (a.getJobCardNumber() - b.getJobCardNumber()));
            });
            Collections.sort(ConsArgs, startTimeComparator);
            Project myp = new Project(ConsArgs);
            myp.initialiseJobs();
        }

        //For initialised jobs and projects with initialised jobs
        startTimeComparator = ((a, b)
                -> {
            return ((int) (a.getStartTime().toEpochSecond(ZoneOffset.UTC) - b.getStartTime().toEpochSecond(ZoneOffset.UTC)));
        });
        Collections.sort(ConsArgs, startTimeComparator);
        Comparator<Job> byEndTime = (Job s1, Job sy)
                -> {
            return (int) (s1.getExpectedEndTime().toEpochSecond(ZoneOffset.UTC) - sy.getExpectedEndTime().toEpochSecond(ZoneOffset.UTC));
        };
        Job end = (Job) (s.max(byEndTime).get());

        Job start = (Job) (ConsArgs.stream().min(startTimeComparator).get());

        projectStart = start.getStartTime();
        projectEnd = end.getExpectedEndTime();

        totalDuration = ChronoUnit.MINUTES.between(projectEnd, projectStart);
        itemHeight = ((int) (((remainingHeight) / (ConsArgs.size() + 2)) / 2)) * 2 + 1;
        if (itemHeight > 20) {
            itemHeight = 20;
        } else if (itemHeight < 14) {
            itemHeight = 14;
        }
        if (preferredHeight > (ConsArgs.size() + 2) * itemHeight) {
            totalHeight = preferredHeight;
        } else {
            totalHeight = (int) ((ConsArgs.size()) * itemHeight) + 24 + 16;
        }
        this.setVisible(true);

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        this.setPreferredSize(HomeScreen.JobGantt.getSize());
        g.setColor(Color.black);
        g.drawRect(0, 0, totalWidth, totalHeight - 1);

        s = args.stream();
        FontMetrics fm = g.getFontMetrics();
        g.setFont(new Font("SanSerif", Font.PLAIN, 12));
        s.sorted(startTimeComparator)
                .forEach(
                        (a) -> {
                            int yCord = (int) abs((args.indexOf(a)) * itemHeight + 8 + 2);
                            xCord = (int) getItemPositionX((Job) a);
                            g.setColor(((Job) a).getColor());
                            g.fillRect(
                                    (xCord),
                                    (yCord),
                                    ((int) getItemWidth((Job) a)),
                                    ((int) itemHeight - 1));
                            g.setColor(Color.BLACK);
                            g.drawString(
                                    ((Job) a).getJobDescription(),
                                    getTextPositionX(fm.stringWidth(((Job) a).getJobDescription()), xCord),
                                    (int) (yCord + (int) (((itemHeight - 12) / 2) + 12) - fm.getDescent() + 1));
                            remainingWidth-=(int) getItemWidth((Job) a);
                        });
        remainingWidth = totalWidth - 2 * buffer;
        if(args.isEmpty()){
            this.removeAll();
            return;
        }
        if(args.get(0).getProject()==0){
        g.setColor(Color.yellow);
        g.fillOval(getItemPositionX() - 4, 1, 8, 8);
        g.drawLine(getItemPositionX(), 1, getItemPositionX(), totalHeight - buffer);
        g.fillOval(getItemPositionX() - 4, totalHeight - 9, 8, 8);}
        g.setColor(Color.black);
        xCord = abs((int) (((double) (ChronoUnit.MINUTES.between(LocalDateTime.now(), projectStart)) / (double) totalDuration) * ((double) totalWidth)));

        String myS = LocalDate.now().getDayOfMonth() + " " + LocalDate.now().getMonth().toString().substring(0, 3) + " " + LocalTime.now().toString().substring(0, 5);
        g.drawString(myS,
                getTextPositionX(fm.stringWidth(myS), xCord) + 6, totalHeight - 1);

        int actualWidth = fm.stringWidth(getFriendlyTime(projectEnd));

        g.setColor(Color.black);
        g.drawString(getFriendlyTime(projectStart), buffer + 1, totalHeight - 12);
        g.drawString(getFriendlyTime(projectEnd), (totalWidth - buffer - actualWidth), totalHeight - 12);
        this.setVisible(true);
    }

    private long getItemWidth(Job x) {
        long d;

        if (args.size() == 1) {
            return totalWidth - 2 * buffer;
        }

        double durationOfTheJob = abs((double) (60 * x.getDuration()));
        double totDuration = abs(totalDuration);
        double remainingWidthAfterInserts = totalWidth - 2 * buffer;
        double dabuli = (durationOfTheJob / totDuration) * remainingWidthAfterInserts;
        d = (long) dabuli;

        return d;
    }

    int getTextPositionX(int textLength, int ipx) {
        if (textLength > abs(remainingWidth)) {
            return totalWidth - buffer - textLength;
        }
        return ipx + 3;
    }

    private int getItemPositionX() {
        double timeDiff = abs(ChronoUnit.MINUTES.between(LocalDateTime.now(), projectStart));
        double totDur = (double) totalDuration;
        double totWid = totalWidth;
        double result = abs((timeDiff / totDur) * totWid);
        if (result > totWid) {
            return (int) totWid - 2;
        }

        return abs((int) result) - 2;
    }

    private long getItemPositionX(Job x) {
        double timeDiff = ChronoUnit.MINUTES.between(projectStart, x.getStartTime());
        double totDur = (double) totalDuration;
        double totWid = totalWidth;
        double result = (timeDiff / totDur) * totWid;
        return (long) abs(result) + buffer;
    }

    /**
     *
     * @param time
     * @return A date in human readable terms. Day of month is its less than a
     * week ago
     */
    public static String getFriendlyTime(LocalDateTime... time) {

        if (time.length == 1) {
            if (!(time[0] instanceof Object)) {
                return "";
            }
            long diff = ChronoUnit.HOURS.between(LocalDateTime.now(), time[0]);
            if (diff > -24 & diff < 24) {
                return time[0].toLocalTime().toString().substring(0, 5);
            }
            diff = ChronoUnit.DAYS.between(LocalDateTime.now(), time[0]);

            if (diff > -7 & diff < 7) {
                return time[0].getDayOfWeek().toString().substring(0, 3) + " " + time[0].toLocalTime().toString().substring(0, 5);
            } else {
                return time[0].getDayOfMonth() + " " + time[0].getMonth().toString().substring(0, 3) + " ";
            }
        } else if (time.length == 2) {

        }
        throw new RuntimeException("A friendly time wasnt gathered");
    }
}
