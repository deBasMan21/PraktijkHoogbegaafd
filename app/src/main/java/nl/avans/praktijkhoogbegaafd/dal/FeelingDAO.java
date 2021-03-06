package nl.avans.praktijkhoogbegaafd.dal;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface FeelingDAO {

    @Query("SELECT * FROM FeelingEntity WHERE date = :date AND parent = :parent")
    FeelingEntity[] getFeelingsForDay(String date, boolean parent);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertFeelings(FeelingEntity[] feelings);

    @Query("SELECT id FROM FeelingEntity ORDER BY id DESC LIMIT 1")
    int getHighestId();

    @Query("DELETE FROM FeelingEntity")
    void deleteAll();
}
