package nl.avans.praktijkhoogbegaafd.dal;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FeelingDAO {

    @Query("SELECT * FROM FeelingEntity WHERE Date = :date")
    List<FeelingEntity> getFeelingsForDay(String date);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFeelings(FeelingEntity[] feelings);
}
