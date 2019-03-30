package ru.seriousgames.goalkeeper;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.ContentValues;
import android.support.annotation.NonNull;

import java.util.concurrent.Executors;

import ru.seriousgames.goalkeeper.database.GoalDatabase;
import ru.seriousgames.goalkeeper.database.InsertRecordTask;
import ru.seriousgames.goalkeeper.database.Keys;
import ru.seriousgames.goalkeeper.database.Record;

public class ThisApp extends Application {

    public static ThisApp instance;
    private GoalDatabase database;
    public MutableLiveData<String> appLiveData = new MutableLiveData<>();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(this, GoalDatabase.class, "goal_database")
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Record[] records = {
                                new Record(Keys.LEVEL, 1),
                                new Record(Keys.MONEY, 100),
                                new Record(Keys.ROUNDS_MODE1, 0),
                                new Record(Keys.POINTS_MODE1, 0)
                        };
                        ContentValues v = new ContentValues();
                        for (int i = 0; i < records.length; i++){
                            v.put("id", records[i].id);
                            v.put("value", records[i].value);
                            db.insert("Record", 0, v);
                        }
                        //getInstance().getDatabase().getRecordDao().insert(records);
                        getInstance().appLiveData.postValue("ok");
                    }
                    @Override
                    public void onOpen(@NonNull SupportSQLiteDatabase db){
                        super.onOpen(db);
                        getInstance().appLiveData.postValue("ok");
                    }
                })
                .build();
    }

    public static ThisApp getInstance() {
        return instance;
    }

    public GoalDatabase getDatabase() {
        return database;
    }

}
