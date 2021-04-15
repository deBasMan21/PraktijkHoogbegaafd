package nl.avans.praktijkhoogbegaafd.dal;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class FeelingEntity {

    @PrimaryKey @NonNull
    public int id;

    @ColumnInfo
    public String date;

    @ColumnInfo
    public boolean parent;

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

    public FeelingEntity(@NonNull int id, String date, boolean parent, int emoto, int fanti, int intellecto, int psymo, int senzo){
        this.id = id;
        this.date = date;
        this.parent = parent;
        this.emoto = emoto;
        this.fanti = fanti;
        this.intellecto = intellecto;
        this.psymo = psymo;
        this.senzo = senzo;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    public void setDate(@NonNull String date) {
        this.date = date;
    }

    public int getEmoto() {
        return emoto;
    }

    public void setEmoto(int emoto) {
        this.emoto = emoto;
    }

    public int getFanti() {
        return fanti;
    }

    public void setFanti(int fanti) {
        this.fanti = fanti;
    }

    public int getIntellecto() {
        return intellecto;
    }

    public void setIntellecto(int intellecto) {
        this.intellecto = intellecto;
    }

    public int getPsymo() {
        return psymo;
    }

    public void setPsymo(int psymo) {
        this.psymo = psymo;
    }

    public int getSenzo() {
        return senzo;
    }

    public void setSenzo(int senzo) {
        this.senzo = senzo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isParent() {
        return parent;
    }

    public void setParent(boolean parent) {
        this.parent = parent;
    }
}
