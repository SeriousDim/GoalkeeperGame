package ru.seriousgames.goalkeeper.database;

import android.content.Context;
import android.os.AsyncTask;

import ru.seriousgames.goalkeeper.ThisApp;

@Deprecated
public class UpdateRecordTask extends AsyncTask<Record, Void, Void> {

    RecordDao dao;
    GoalDatabase database;
    Context ctx;
    TaskListener listener;

    public UpdateRecordTask(Context ctx, TaskListener listener){
        this.ctx = ctx;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Record... records){
        database = ThisApp.getInstance().getDatabase();
        dao = database.getRecordDao();

        dao.update(records);

        return null;
    }

    @Override
    protected void onPostExecute(Void res){
        listener.onTaskCompleted(res);
    }

}
