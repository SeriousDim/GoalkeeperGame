package ru.seriousgames.goalkeeper.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface RecordDao {

    @Query("select * from record")
    List<Record> getAllRecords();

    @Query("select * from record where id = :id")
    Record getById(String id);

    @Insert
    void insert(Record... records);

    @Update
    void update(Record... record);

}
