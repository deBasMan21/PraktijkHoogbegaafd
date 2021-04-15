package nl.avans.praktijkhoogbegaafd.logic;

import android.app.Application;

import nl.avans.praktijkhoogbegaafd.dal.FeelingsDB;
import nl.avans.praktijkhoogbegaafd.dal.InfoDAO;
import nl.avans.praktijkhoogbegaafd.dal.InfoEntity;

public class InfoEntityManager {
    private FeelingsDB feelingsDB;
    private InfoDAO infoDAO;


    public InfoEntityManager(Application application){
        feelingsDB = FeelingsDB.getDatabase(application);
        infoDAO = feelingsDB.getInfoDAO();
    }

    public void updateInfo(String oldName, InfoEntity info){
        FeelingsDB.databaseWriteExecuter.execute(new Runnable() {
            @Override
            public void run() {
                infoDAO.updateInfo(oldName, info.getName(), info.getBirthday(), info.getBegeleidster(), info.getParent(), info.isParentalControl());
            }
        });
    }

    public void insertInfo(InfoEntity info){
        FeelingsDB.databaseWriteExecuter.execute(new Runnable() {
            @Override
            public void run() {
                infoDAO.insertInfo(info);
            }
        });
    }

    public InfoEntity getInfo(){
        return infoDAO.getInfo();
    }

    public boolean hasInfo(){
        return infoDAO.getInfo() != null;
    }
}
