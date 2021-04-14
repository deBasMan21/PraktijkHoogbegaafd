package nl.avans.praktijkhoogbegaafd.dal;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class FeelingEntity {
    @PrimaryKey
    public String date;

    @ColumnInfo
    public int value;

    @ColumnInfo
    public String xCitabillie;

    public FeelingEntity(String date, int value, String xCitabillie){
        this.date = date;
        this.value = value;
        this.xCitabillie = xCitabillie;
    }
}
