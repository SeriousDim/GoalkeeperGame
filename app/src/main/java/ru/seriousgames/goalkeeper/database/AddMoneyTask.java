package ru.seriousgames.goalkeeper.database;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import ru.seriousgames.goalkeeper.R;
import ru.seriousgames.goalkeeper.ThisApp;

public class AddMoneyTask extends AdvancedAsyncTask<Integer, Void, Void> {

    boolean show;
    int data;

    public AddMoneyTask(Context ctx) {
        super(ctx);
    }

    @Override
    public void onTaskComplete(Void v){
        if (show){
            String s2 = String.format(ctx.getResources().getString(R.string.get_money), data);
            Toast.makeText(ctx, s2, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(ctx, ctx.getResources().getString(R.string.no_money), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected Void doInBackground(Integer... add){
        database = ThisApp.getInstance().getDatabase();
        recordDao = database.getRecordDao();

        int money = (recordDao.getById(Keys.MONEY)).value;
        if (add[0] > 0){
            recordDao.update(new Record(
                    Keys.MONEY,
                    money + add[0]
            ));
            show = true;
            data = add[0];
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void v){
        super.onPostExecute(v);
    }

}
