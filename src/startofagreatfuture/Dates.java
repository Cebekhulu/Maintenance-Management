/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package startofagreatfuture;

import static java.lang.Math.abs;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Bheki
 *
 */
public class Dates {

    List<Dates> listOfDates;
    LocalDateTime start, end, requestedStart,requestedStop;
    long totalTime;
    private long workedTime;
    
    public Dates(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }
    public Dates(){
    }
    public void setListOfDates(List<Dates> listOfDates) {
        this.listOfDates = listOfDates;
    }

    public void setRequestedStart(LocalDateTime requestedStart) {
        for (Dates date : listOfDates) {
            if (date.getEnd().isBefore(requestedStart)) {
                listOfDates.remove(date);
            }
            if (date.getStart().isBefore(requestedStart)) {
                date.start = requestedStart;
            }
        }
        this.requestedStart = requestedStart;

    }
    enum DAY_OF_WEEK{
        MONDAY(7),TUESDAY(6),WEDNESDAY(5),THURSDAY(4),FRIDAY(3),SATURDAY(2),SUNDAY(1);
        private int numberth;
        private DAY_OF_WEEK(int numberth){
            this.numberth = numberth;
        }
        public int getNumberthOfWeek(){
            return numberth;
        }
    }
    public long getTotalTime(){
        Comparator<Dates> byStartTime =(a,b)->a.getStart().compareTo(b.getStart());
        Comparator<Dates> byEndTime = (a,b)->a.getEnd().compareTo(b.getEnd());
        if(listOfDates.isEmpty())return 0;
        LocalDateTime min = requestedStart;
        LocalDateTime max = LocalDateTime.now()/*listOfDates.stream().max(byEndTime).get().getStart()*/;
        Dates ds =new Dates(min,max);
        return ds.getHourDifference();
    }
    public long getWorkedTime(){
        listOfDates.stream().forEach(a->totalTime+=a.getHourDifference());
        return workedTime;
    }
    public void trimUnwantedTime(String requestedTime){
        switch (requestedTime){
            case "w":
                requestedStart = LocalDateTime.now().minusDays(DAY_OF_WEEK.valueOf(LocalDateTime.now().getDayOfWeek().toString()).getNumberthOfWeek());
                break;
            case "m":
                requestedStart=LocalDateTime.now().withDayOfMonth(1);
                break;
            case "y":
                requestedStart=LocalDateTime.now().withDayOfMonth(1).withMonth(Month.JANUARY.ordinal());
        }
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }




    public long getHourDifference() {
        if (start.getDayOfMonth() == end.getDayOfMonth() && start.getMonth() == end.getMonth()) {
            return abs(ChronoUnit.HOURS.between(start, end));
        } else if (abs(start.getDayOfMonth() + 1) == end.getDayOfMonth() && start.getMonth() == end.getMonth()||(abs(start.getDayOfMonth() -1) == end.getDayOfMonth())) {
            return abs(getStartDaysHours() + getEndDaysHours());
            
        }else if ((abs(start.getDayOfMonth() -1) == end.getDayOfMonth())) {
            return abs(getStartDaysHours() + getEndDaysHours());}
        else if (abs(ChronoUnit.HOURS.between(end, start)) > 24) {
            return abs(getEndDaysHours() + getStartDaysHours() + 8 * ((ChronoUnit.DAYS.between(end, start)) - (getEndDaysHours() + getStartDaysHours()) / 24));
        } else {return 1;
                
            //System.out.println("Start: " + start + "\nEnd: " + end + "\nStuff that broke my code" + "\nTimeDiff in days" + ChronoUnit.DAYS.between(end, start));
        }
        //throw new RuntimeException();
    }

    private Long getEndDaysHours() {
        LocalDateTime d = LocalDateTime.of(end.getYear(), end.getMonth(), end.getDayOfMonth(), 07, 00);
        return abs(ChronoUnit.HOURS.between(d, end));
    }

    private Long getStartDaysHours() {
        LocalDateTime d = LocalDateTime.of(start.getYear(), start.getMonth(), start.getDayOfMonth(), 16, 00);
        return abs(ChronoUnit.HOURS.between(d, start));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.start);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Dates other = (Dates) obj;
        if (!Objects.equals(this.start, other.start)) {
            return false;
        }
        if (!Objects.equals(this.end, other.end)) {
            return false;
        }
        return true;
    }
    
}
