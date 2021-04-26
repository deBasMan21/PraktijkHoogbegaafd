package nl.avans.praktijkhoogbegaafd.logic;

import android.app.Application;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import nl.avans.praktijkhoogbegaafd.dal.FeelingDAO;
import nl.avans.praktijkhoogbegaafd.dal.FeelingEntity;
import nl.avans.praktijkhoogbegaafd.dal.FeelingsDB;
import nl.avans.praktijkhoogbegaafd.domain.DayFeeling;

public class FeelingsEntityManager {
    private FeelingsDB feelingsDB;
    private FeelingDAO feelingDAO;

    public FeelingsEntityManager(Application application){
        feelingsDB = FeelingsDB.getDatabase(application);
        feelingDAO = feelingsDB.getFeelingDAO();
    }

    public void insertFeelings(FeelingEntity[] feelings){
        FeelingsDB.databaseWriteExecuter.execute(new Runnable() {
            @Override
            public void run() {
                feelingDAO.insertFeelings(feelings);
            }
        });
    }

    public DayFeeling getFeelingsForDay(String date, boolean parent){
        FeelingEntity[] feelings = feelingDAO.getFeelingsForDay(date, parent);
        ArrayList<FeelingEntity> parsedfeelings = new ArrayList<>();
        parsedfeelings.addAll(Arrays.asList(feelings));
        return new DayFeeling(date, parsedfeelings);
    }

    public int getHighestId(){
        return feelingDAO.getHighestId();
    }
}
