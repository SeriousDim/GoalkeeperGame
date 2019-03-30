package ru.seriousgames.goalkeeper.database;

import android.os.AsyncTask;

import ru.seriousgames.goalkeeper.ThisApp;

public class InsertRecordTask extends AsyncTask<Record, Void, Void> {

    RecordDao dao;
    GoalDatabase database;

    @Override
    protected Void doInBackground(Record... records){
        database = ThisApp.getInstance().getDatabase();
        dao = database.getRecordDao();

        dao.insert(records);

        return null;
    }

}
