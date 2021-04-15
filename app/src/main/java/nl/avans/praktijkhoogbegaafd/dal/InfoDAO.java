package nl.avans.praktijkhoogbegaafd.dal;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface InfoDAO {

    @Query("UPDATE InfoEntity SET name = :newName, birthday = :birthday, begeleidster = :begeleidster, parent = :parent, parentalControl = :parentalControl WHERE Name = :oldName")
    void updateInfo(String oldName, String newName, String birthday, String parent, String begeleidster, boolean parentalControl);

    @Insert
    void insertInfo(InfoEntity info);

    @Query("SELECT * FROM InfoEntity")
    InfoEntity getInfo();

}
