package nl.avans.praktijkhoogbegaafd.dal;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class InfoEntity {

    @PrimaryKey @NonNull
    public String name;

    @ColumnInfo
    public String birthday;

    @ColumnInfo
    public String begeleidster;

    @ColumnInfo
    public String parent;

    @ColumnInfo
    public boolean parentalControl;


    public InfoEntity(String name, String birthday, String begeleidster, String parent, boolean parentalControl) {
        this.name = name;
        this.birthday = birthday;
        this.begeleidster = begeleidster;
        this.parent = parent;
        this.parentalControl = parentalControl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getBegeleidster() {
        return begeleidster;
    }

    public void setBegeleidster(String begeleidster) {
        this.begeleidster = begeleidster;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public boolean isParentalControl() {
        return parentalControl;
    }

    public void setParentalControl(boolean parentalControl) {
        this.parentalControl = parentalControl;
    }
}
