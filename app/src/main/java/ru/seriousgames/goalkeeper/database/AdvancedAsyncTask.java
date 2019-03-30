package ru.seriousgames.goalkeeper.database;

import android.content.Context;
import android.os.AsyncTask;

public abstract class AdvancedAsyncTask<Par, Prog, Out> extends AsyncTask<Par, Prog, Out> {

    GoalDatabase database;
    RecordDao recordDao;
    Context ctx;

    public AdvancedAsyncTask(Context ctx){
        this.ctx = ctx;
    }

    public abstract void onTaskComplete(Out arr);

    @Override
    protected void onPostExecute(Out o){
        onTaskComplete(o);
    }
}
