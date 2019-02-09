package ru.seriousgames.goalkeeper;
/*
 *  Property of Dmitrii Lykov(Дмитрий Лыков) aka Serious Games
 *  2018
 *
 *  Класс MySurfaceView
 *  является SurfaceView, служит для отрисовки всей игры, кроме компонентов View
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    GameThread thread;
    final Graphics graphics;
    boolean started = false;

    public MySurfaceView(Context ctx){
        super(ctx);
        getHolder().addCallback(this);
        this.graphics = new Graphics(ctx.getResources());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!started && thread != null){
            float f = (float)(getWidth()) / (float)(thread.graphics.field.getWidth());
            this.graphics.setGraphicalParams(f, getWidth(), getHeight());

            thread.time = System.currentTimeMillis();
            thread.start();
            started = true;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        /*boolean retry = true;
        thread.interrupt();
        while (retry){
            try{
                thread.join();
                retry = false;
            } catch(InterruptedException e){}
        }*/
    }

}
