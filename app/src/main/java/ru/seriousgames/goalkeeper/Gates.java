package ru.seriousgames.goalkeeper;
/*
 *  Property of Dmitrii Lykov(Дмитрий Лыков) aka Serious Games
 *  2018
 *
 *  Класс Gates
 *  описывает свойства и поведение футбольных ворот
 */

import android.graphics.Matrix;
import android.util.Log;

public class Gates {

    final float x, y, rodWidth;
    float newX, newY, bsX, bsY;
    final float[] real; //x, y, width and height of the real gates size
    boolean isGoal, isRod;

    public Gates(float width, float height, float f) {
        this.x = width*0.222f;
        this.y = height*0.04218f;
        this.real = new float[4];
        this.real[0] = this.x + width*0.055f;
        this.real[1] = this.y + height*0.025f;
        this.real[2] = this.real[0] + width*0.44f;
        this.real[3] = this.real[1] + height*0.075f;
        this.rodWidth = width*0.0104f;
        this.isGoal = false;
        this.isRod = false;
    }

    /*
        if (ball.y-ball.radius+shiftY <= storage.gates.real[1])
            ball.sY = - ball.sY;
        else if (ball.x-ball.radius+shiftX <= storage.gates.real[0])
            ball.sX = - ball.sX;
        else if (ball.x+ball.radius+shiftX >= storage.gates.real[2])
            ball.sX = - ball.sX;
    */

    public final boolean topCollide(Ball b, float sX, float sY, Matrix mtx){
        boolean inside = isInside(b.x, b.y);
        boolean result = false;
        newY = 0;
        if ((b.y-b.radius+sY <= real[1] && inside)) {
            b.sY = -b.sY;
            Log.i("collision","Top inside");
            if (!isGoal){
                this.isGoal = true;
                result = true;
            }
            newY = real[1]+b.radius+1;
        }
        else if (b.y+b.radius+sY >= real[1] && isBehindGates(b.x, b.y)) {
            b.sY = -b.sY;
            Log.i("collision","Top");
            newY = real[1]-b.radius-1;
        }
        bsY = newY-b.y;
        return result;
    }

    public final boolean leftCollide(Ball b, float sX, float sY, Matrix mtx){
        boolean inside = isInside(b.x, b.y);
        boolean result = false;
        newX = 0;
        if ((b.x-b.radius+sX <= real[0] && inside)) {
            b.sX = -b.sX;
            Log.i("collision","Left inside");
            if (!isGoal){
                this.isGoal = true;
                result = true;
            }
            newX = real[0]+b.radius+1;
        }
        else if ((b.x+b.radius+sX >= real[0] && isLeftOfGates(b.x, b.y))) {
            b.sX = -b.sX;
            Log.i("collision","Left");
            newX = real[0]-b.radius-1;
        }
        bsX = newX - b.x;
        return result;
    }

    public final boolean rightCollide(Ball b, float sX, float sY, Matrix mtx){
        boolean inside = isInside(b.x, b.y);
        boolean result = false;
        newX = 0;
        if ((b.x+b.radius+sX >= real[2] && inside)) {
            b.sX = -b.sX;
            Log.i("collision","Right inside");
            if (!isGoal){
                this.isGoal = true;
                result = true;
            }
            newX = real[2]-b.radius-1;
        }
        else if ((b.x-b.radius+sX <= real[2] && isRightOfGates(b.x, b.y))) {
            b.sX = -b.sX;
            Log.i("collision","Right");
            newX = real[2]+b.radius+1;
        }
        bsX = newX - b.x;
        return result;
    }

    public final boolean collide(Ball b, float sX, float sY, Matrix mtx){
        bsX = sX;
        bsY = sY;
        mtx.postTranslate(bsX, bsY);
        b.setCoords(b.x+bsX, b.y+bsY);
        return topCollide(b, sX, sY, mtx) || leftCollide(b, sX, sY, mtx) || rightCollide(b, sX, sY, mtx);
    }

    // TODO шатнга
    public final void rodCollide(Ball b, float shX, float shY, Matrix mtx){
        boolean res = false;
        float newY;
        if ((b.x+shX > real[0] && b.x+shX < real[0]+rodWidth ||
                b.x+shX > real[2]-rodWidth && b.x < real[2]) && b.y-b.radius+shY >= real[3] && b.y < real[3]){
            res = true;
            b.sY = -b.sY;
            newY = real[3]+b.radius+1;
            shY = newY - b.y;
            mtx.postTranslate(shX, shY);
            b.setCoords(b.x+shX, b.y+shY);
        }
        this.isRod = res;
    }

    public final boolean isInside(float x, float y){
        return x > real[0] &&
                x < real[2] &&
                y > real[1] &&
                y < real[3];
    }

    public final boolean isBehindGates(float x, float y){
        return y < real[1] &&
                x > real[0] &&
                x < real[2];
    }

    public final boolean isLeftOfGates(float x, float y){
        return x < real[0] &&
                y > real[1] &&
                y < real[3];
    }

    public final boolean isRightOfGates(float x, float y){
        return x > real[2] &&
                y > real[1] &&
                y < real[3];
    }

}
