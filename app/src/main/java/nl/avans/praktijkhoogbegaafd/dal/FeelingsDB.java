package nl.avans.praktijkhoogbegaafd.dal;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = FeelingEntity.class, version = 1)
public abstract class FeelingsDB extends RoomDatabase {

    private static volatile FeelingsDB INSTANCE;
    public static final ExecutorService databaseWriteExecuter = Executors.newFixedThreadPool(4);

    abstract public FeelingDAO getMovieDAO();

    public static FeelingsDB getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (FeelingsDB.class) {
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), FeelingsDB.class, "movie_app_db").allowMainThreadQueries().build();
                }
            }
        }
        return INSTANCE;
    }
}
