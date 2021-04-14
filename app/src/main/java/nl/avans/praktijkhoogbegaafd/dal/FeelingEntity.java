package nl.avans.praktijkhoogbegaafd.dal;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class FeelingEntity {
    @PrimaryKey @NonNull
    public String date;

    @ColumnInfo
    public int emoto;

    @ColumnInfo
    public int fanti;

    @ColumnInfo
    public int intellecto;

    @ColumnInfo
    public int psymo;

    @ColumnInfo
    public int senzo;

    public FeelingEntity(@NonNull String date, int emoto, int fanti, int intellecto, int psymo, int senzo){
        this.date = date;
        this.emoto = emoto;
        this.fanti = fanti;
        this.intellecto = intellecto;
        this.psymo = psymo;
        this.senzo = senzo;
    }
}
