package ru.seriousgames.goalkeeper.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Record.class}, version = 1)
public abstract class GoalDatabase extends RoomDatabase {

    public abstract RecordDao getRecordDao();
}
