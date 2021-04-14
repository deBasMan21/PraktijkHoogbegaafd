package nl.avans.praktijkhoogbegaafd.logic;

import android.app.Application;

import java.util.List;

import nl.avans.praktijkhoogbegaafd.dal.FeelingDAO;
import nl.avans.praktijkhoogbegaafd.dal.FeelingEntity;
import nl.avans.praktijkhoogbegaafd.dal.FeelingsDB;

public class FeelingsEntityManager {
    private FeelingsDB feelingsDB;
    private FeelingDAO feelingDAO;

    public FeelingsEntityManager(Application application){
        feelingsDB = FeelingsDB.getDatabase(application);
        feelingDAO = feelingsDB.getMovieDAO();
    }

    public void insertFeelings(FeelingEntity[] feelings){
        FeelingsDB.databaseWriteExecuter.execute(new Runnable() {
            @Override
            public void run() {
                feelingDAO.insertFeelings(feelings);
            }
        });
    }

    public FeelingEntity getFeelingsForDay(String date){
        return feelingDAO.getFeelingsForDay(date);
    }
}
