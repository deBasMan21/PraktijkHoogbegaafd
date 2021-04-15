package nl.avans.praktijkhoogbegaafd.domain;

import java.util.ArrayList;

import nl.avans.praktijkhoogbegaafd.dal.FeelingEntity;

public class DayFeeling {
    private ArrayList<FeelingEntity> feelingsForDay = new ArrayList<>();
    private String date = "";

    public DayFeeling(String date){
        this.date = date;
    }

    public DayFeeling(String date, ArrayList<FeelingEntity> feelings){
        this.feelingsForDay = feelings;
    }

    public ArrayList<FeelingEntity> getFeelingsForDay() {
        return feelingsForDay;
    }

    public void setFeelingsForDay(ArrayList<FeelingEntity> feelingsForDay) {
        this.feelingsForDay = feelingsForDay;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void addFeeling(FeelingEntity fe){
        feelingsForDay.add(fe);
    }
}
