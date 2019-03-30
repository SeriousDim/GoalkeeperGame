package ru.seriousgames.goalkeeper;
/*
 *  Property of Dmitrii Lykov(Дмитрий Лыков) aka Serious Games
 *  2018
 *
 *  Класс MainActivity
 *  основной Activity: слушает нажатия, взаимодействует с View-компонентами,
 *  принимает сообщения от DrawThread
 */

import android.animation.ObjectAnimator;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;

import ru.seriousgames.goalkeeper.database.AddMoneyTask;
import ru.seriousgames.goalkeeper.database.CompareAndUpdateTask;
import ru.seriousgames.goalkeeper.database.GoalDatabase;
import ru.seriousgames.goalkeeper.database.Keys;
import ru.seriousgames.goalkeeper.database.Record;
import ru.seriousgames.goalkeeper.database.RecordDao;
import ru.seriousgames.goalkeeper.database.TaskListener;
import ru.seriousgames.goalkeeper.database.UpdateRecordTask;

public class MainActivity extends AppCompatActivity {

    MySurfaceView sv;
    GoalDatabase database;
    RecordDao recordDao;
    static Handler h;
    static EndGameTask task;
    Group cl, backgr, info, shop;
    TableLayout stats;
    TextView pointsView, goalsView, roundView, timeView, alert;
    TextView lvl, money, header, roundRecord, pointsRecord;
    Button start;
    MutableLiveData<String> liveData;
    int opened;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        h = new Handler(){
            @Override
            public void handleMessage(Message msg){
                switch(msg.what){
                    case 1:
                        int[] nums = (int[])msg.obj;
                        setRound(nums[0]);
                        setGoals(nums[1], nums[4]);
                        setPoints(nums[2]);
                        setTime(nums[3]);
                        break;
                    case 2:
                        setGoals(msg.arg1, msg.arg2);
                        break;
                    case 3:
                        setPoints(msg.arg1);
                        break;
                    case 4:
                        setTime(msg.arg1);
                        break;
                    case 5:
                        switch(msg.arg1){
                            case 0: //убрать текст
                                alert.setText("");
                                break;
                            case 1: // показать номер раунда
                                alert.setTextColor(getResources().getColor(R.color.yellow));
                                alert.setText(getResources().getString(R.string.round)+msg.arg2);
                                break;
                            case 2: // показать гол
                                alert.setTextColor(getResources().getColor(R.color.green));
                                alert.setText(getResources().getString(R.string.alert)+"\n+"+msg.arg2);
                                break;
                            case 3: // промах
                                alert.setTextColor(getResources().getColor(R.color.red));
                                alert.setText(getResources().getString(R.string.alert2)+"\n"+msg.arg2);
                                break;
                            case 4: // недолет
                                alert.setTextColor(getResources().getColor(R.color.red));
                                alert.setText(getResources().getString(R.string.alert3)+"\n"+msg.arg2);
                                break;
                            case 5: // мяч отбит
                                alert.setTextColor(getResources().getColor(R.color.red));
                                alert.setText(getResources().getString(R.string.alert4)+"\n"+msg.arg2);
                                break;
                            case 6: // конец игры
                                alert.setTextColor(getResources().getColor(R.color.red));
                                alert.setText(getResources().getString(R.string.alert5));
                                task = new EndGameTask();
                                task.execute();
                                break;
                            case 7: //штанга
                                alert.setTextColor(getResources().getColor(R.color.red));
                                alert.setText(getResources().getString(R.string.alert6));
                                break;
                        }
                        break;
                }
            }
        };

        setContentView(R.layout.activity_main);

        lvl = findViewById(R.id.lvl);
        money = findViewById(R.id.money);
        database = ThisApp.getInstance().getDatabase();
        recordDao = database.getRecordDao();

        backgr = findViewById(R.id.background);
        header = findViewById(R.id.header);
        info = findViewById(R.id.info);
        shop = findViewById(R.id.shop_group);
        stats = findViewById(R.id.stats_table);

        roundRecord = findViewById(R.id.round_view);
        pointsRecord = findViewById(R.id.points_view);

        ((TextView)findViewById(R.id.textView9)).setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView)findViewById(R.id.textView10)).setMovementMethod(LinkMovementMethod.getInstance());


        updateValues();
        /*liveData = ThisApp.getInstance().appLiveData;
        liveData.observeForever(
                new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String s) {
                        if (s.equals("ok"))
                            updateValues();
                    }
                }
        );*/
    }

    private void animateAppear(Group group){
        group.animate().
                alphaBy(0).
                alpha(1).
                setDuration(1000).
                setInterpolator(new LinearInterpolator()).
                start();
    }
    private void animateAppear(View view){
        view.animate().
                alphaBy(0).
                alpha(1).
                setDuration(1000).
                setInterpolator(new LinearInterpolator()).
                start();
    }


    private void setBackgroundVisibility(){
        backgr.setVisibility(
                backgr.getVisibility()==View.GONE ? View.VISIBLE : View.GONE);
    }

    public void openShop(View view){
        setBackgroundVisibility();
        header.setText(getResources().getString(R.string.shop));
        shop.setVisibility(View.VISIBLE);
        opened = 1;
        animateAppear(backgr);
        animateAppear(shop);
    }

    public void openInfo(View view){
        setBackgroundVisibility();
        header.setText(getResources().getString(R.string.about));
        info.setVisibility(View.VISIBLE);
        opened = 3;
        animateAppear(backgr);
        animateAppear(info);
    }

    public void close(View view){
        setBackgroundVisibility();
        switch(opened){
            case 1:

                break;
            case 2:

                break;
            case 3:

                break;
        }
        info.setVisibility(View.GONE);
        shop.setVisibility(View.GONE);
        stats.setVisibility(View.GONE);
    }

    public void openStatistics(final View view){
        setBackgroundVisibility();
        header.setText(getResources().getString(R.string.stats));
        opened = 2;
        stats.setVisibility(View.VISIBLE);
        animateAppear(backgr);
        animateAppear(stats);
    }

    public void setValues(int... arr){
        lvl.setText("LVL "+ arr[0]);
        money.setText("$ "+ arr[1]);
        roundRecord.setText(arr[2]+"");
        pointsRecord.setText(arr[3]+"");
    }

    public final void updateValues(){
        TaskListener listener = new TaskListener<Integer>() {
            @Override
            public void onTaskCompleted(Integer... vals){
                setValues(vals[0], vals[1], vals[2], vals[3]);
            }
        };
        GetRecordQuery task = new GetRecordQuery(listener);
        task.execute(Keys.LEVEL, Keys.MONEY, Keys.ROUNDS_MODE1, Keys.POINTS_MODE1);
    }

    public final void beginGame(View v){
        goalsView = (TextView)findViewById(R.id.goals);
        pointsView = (TextView)findViewById(R.id.points);
        roundView = (TextView)findViewById(R.id.round);
        timeView = (TextView)findViewById(R.id.time);
        alert = (TextView)findViewById(R.id.alert);
        cl = (Group) findViewById(R.id.mainMenu);
        cl.setVisibility(View.GONE);
        cl = (Group)findViewById(R.id.gamepanel);
        cl.setVisibility(View.VISIBLE);
        RelativeLayout rl = (RelativeLayout)findViewById(R.id.rel);

        sv = new MySurfaceView(this);
        sv.thread = new GameThread(sv.getHolder(), this,  sv.graphics, rl.getWidth(), rl.getHeight());
        sv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sv.thread.control();
            }
        });
        rl.addView(sv);
    }

    public final void checkRecords(int round, int points){
        int dol = Math.round(points/10);
        AddMoneyTask addMoney = new AddMoneyTask(this);
        addMoney.execute(dol);

        CompareAndUpdateTask updateTask = new CompareAndUpdateTask(this);
        updateTask.execute(new Record(
                Keys.ROUNDS_MODE1,
                round
        ));

        CompareAndUpdateTask updateTask2 = new CompareAndUpdateTask(this);
        updateTask2.execute(new Record(
                Keys.POINTS_MODE1,
                points
        ));
    }

    public final void endGame(){
        int round = sv.thread.getRound();
        int points = sv.thread.getPoints();
        checkRecords(round, points);

        sv.thread.interrupt();
        boolean retry = true;
        while (retry) {
            try {
                sv.thread.join();
                retry = false;
            } catch (InterruptedException e) {}
        }
        RelativeLayout rl = findViewById(R.id.rel);
        rl.removeAllViews();
        cl =  findViewById(R.id.mainMenu);
        cl.setVisibility(View.VISIBLE);
        cl = findViewById(R.id.gamepanel);
        cl.setVisibility(View.GONE);
        updateValues();
    }

    private final void setGoals(int num, int need){
        goalsView.setText(getResources().getString(R.string.goals)+num+"/"+need);
    }

    private final void setPoints(int num){
        pointsView.setText(getResources().getString(R.string.points)+num);
    }

    private final void setRound(int num){
        roundView.setText(getResources().getString(R.string.round)+num);
    }

    private final void setTime(int ms){
        int min = ms/60000;
        int sec = (ms-(min*60000))/1000;
        String out = "";
        if (sec < 10)
            out = min+":0"+sec;
        else
            out = min+":"+sec;
        timeView.setText(out);
    }

    /*@Override
    protected void onDestroy(){
        super.onDestroy();

    }*/

    class EndGameTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... v){
            long left = 0;
            long now = System.currentTimeMillis();
            while (left < 3000){
                long elapsed = System.currentTimeMillis();
                left += elapsed - now;
                now = elapsed;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v){
            endGame();
        }
    }

    class GetRecordQuery extends AsyncTask<String, Void, Integer[]>{

        TaskListener<Integer> listener;

        public GetRecordQuery(TaskListener listener){
            this.listener = listener;
        }

        @Override
        protected Integer[] doInBackground(String... keys){
            Integer[] out = new Integer[keys.length];
            for (int i = 0; i < keys.length; i++){
                out[i] = (recordDao.getById(keys[i])).value;
            }
            return out;
        }

        @Override
        protected void onPostExecute(Integer[] record){
            listener.onTaskCompleted(record);
        }
    }
}
