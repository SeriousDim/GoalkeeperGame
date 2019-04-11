package ru.seriousgames.goalkeeper;
/*
 *  Property of Dmitrii Lykov(Дмитрий Лыков) aka Serious Games
 *  2018
 *
 *  Класс Graphics
 *  хранит все объекты Bitmap, Matrix, Paint и пр., а также
 *  редактирует их
 */

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

public class Graphics {

    Bitmap field, ball, gates, arrow, forceBar, forceArrow, player1;
    final Matrix m, mRotate, mArrow, mForceArrow, mPlayer1;
    final Paint paint, hitboxShow, pointShow;
    final float FIELD_WIDTH;
    float ff;
    // FFD940

    public Graphics(Resources res){
        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.hitboxShow = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.pointShow = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointShow.setStrokeWidth(5f);
        pointShow.setARGB(255, 255, 0, 0);
        hitboxShow.setARGB(100, 0, 0, 255);
        hitboxShow.setStrokeWidth(3f);

        this.field = BitmapFactory.decodeResource(res, R.drawable.new_field_20x9);
        this.forceBar = BitmapFactory.decodeResource(res, R.drawable.force_bar);
        this.ball = BitmapFactory.decodeResource(res, R.drawable.ball);
        this.gates = BitmapFactory.decodeResource(res, R.drawable.n_gates);
        this.arrow = BitmapFactory.decodeResource(res, R.drawable.arrow);
        this.forceArrow = BitmapFactory.decodeResource(res, R.drawable.new_arrow);
        this.player1 = BitmapFactory.decodeResource(res, R.drawable.player_1);
        this.FIELD_WIDTH = this.field.getWidth();

        this.m = new Matrix();
        this.mRotate = new Matrix();
        this.mArrow = new Matrix();
        this.mForceArrow = new Matrix();
        this.mPlayer1 = new Matrix();
    }

    //TODO: REWRITE using BitmapFactory and SimpleSize variable
    public final void setGraphicalParams(float f, float width, float height){
        this.m.setScale(f,f,0,0);
        this.ff = f;

        this.field = Bitmap.createBitmap(
                this.field,
                0, 0,
                this.field.getWidth(), this.field.getHeight(),
                this.m, false
        );

        this.ball = Bitmap.createBitmap(
                this.ball,
                0, 0,
                this.ball.getWidth(), this.ball.getHeight(),
                this.m, false
        );
        this.gates = Bitmap.createBitmap(
                this.gates,
                0, 0,
                this.gates.getWidth(), this.gates.getHeight(),
                this.m, false
        );
        this.arrow = Bitmap.createBitmap(
                this.arrow,
                0, 0,
                this.arrow.getWidth(), this.arrow.getHeight(),
                this.m, false
        );
        this.forceBar = Bitmap.createBitmap(
                this.forceBar,
                0, 0,
                this.forceBar.getWidth(), this.forceBar.getHeight(),
                this.m, false
        );
        this.forceArrow = Bitmap.createBitmap(
                this.forceArrow,
                0, 0,
                this.forceArrow.getWidth(), this.forceArrow.getHeight(),
                this.m, false
        );
        this.player1 = Bitmap.createBitmap(
                this.player1,
                0, 0,
                this.player1.getWidth(), this.player1.getHeight(),
                this.m, false
        );

        this.mRotate.preTranslate(width*0.5f-(this.ball.getWidth()*0.5f), this.field.getHeight()*0.375f-(this.ball.getWidth()*0.5f));
        this.mArrow.preTranslate(width*0.5f - this.arrow.getWidth()*0.5f, this.field.getHeight()*0.15625f);
        this.mArrow.postRotate(-40f, width*0.5f, this.field.getHeight()*0.375f);
        this.mForceArrow.preTranslate(width*0.151f,height*0.894f);
        this.mPlayer1.preTranslate(width*0.5f-this.player1.getWidth()*0.5f, this.field.getHeight()*0.15f);
    }

}
