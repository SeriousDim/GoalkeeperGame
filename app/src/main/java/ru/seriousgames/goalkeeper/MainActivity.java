package ru.seriousgames.goalkeeper;
/*
 *  Property of Dmitrii Lykov(Дмитрий Лыков) aka Serious Games
 *  2018
 *
 *  Класс MainActivity
 *  основной Activity: слушает нажатия, взаимодействует с View-компонентами,
 *  принимает сообщения от DrawThread
 */

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    MySurfaceView sv;
    static Handler h;
    static EndGameTask task;
    Group cl;
    TextView pointsView, goalsView, roundView, timeView, alert;

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

    public final void endGame(){
        sv.thread.interrupt();
        boolean retry = true;
        while (retry) {
            try {
                sv.thread.join();
                retry = false;
            } catch (InterruptedException e) {}
        }
        RelativeLayout rl = (RelativeLayout)findViewById(R.id.rel);
        rl.removeAllViews();
        cl = (Group) findViewById(R.id.mainMenu);
        cl.setVisibility(View.VISIBLE);
        cl = (Group)findViewById(R.id.gamepanel);
        cl.setVisibility(View.GONE);
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
}
