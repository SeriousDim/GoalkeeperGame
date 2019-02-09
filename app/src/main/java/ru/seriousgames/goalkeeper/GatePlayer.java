package ru.seriousgames.goalkeeper;
/*
 *  Property of Dmitrii Lykov(Дмитрий Лыков) aka Serious Games
 *  2018
 *
 *  Класс GatePlayer
 *  описывает свойства и поведение вратаря
 */

import java.util.Random;

public class GatePlayer {

    final float MAX_DISTANCE;
    final float MAX_SPEED_PER_MS;
    final float WIDTH, HEIGHT;
    final float CENTER_X, CENTER_Y;
    final float WIDTH_MARGIN, HEIGHT_MARGIN;

    float x, y;
    float c[]; // x, y, width, height of hitbox
    //float centerX;
    float distance;
    boolean left, reflected;

    public GatePlayer(float width, float height){
        WIDTH = width*0.1232f;
        HEIGHT = height*0.023f;
        CENTER_X = width*0.429f;
        CENTER_Y = height*0.1875f;
        this.x = CENTER_X;
        this.y = CENTER_Y;
        this.c = new float[4];
        WIDTH_MARGIN = width*0.0086f;
        HEIGHT_MARGIN = height*0.0048f;
        this.c[0] = x + WIDTH_MARGIN;
        this.c[1] = y + HEIGHT_MARGIN;
        this.c[2] = c[0] + WIDTH;
        this.c[3] = c[1] + HEIGHT;
        //this.centerX = width*0.5f;
        MAX_DISTANCE = width*0.277f;
        MAX_SPEED_PER_MS = MAX_DISTANCE/1000f;
        this.reflected = false;
        setRandomParams();
    }

    public final void setRandomParams(){
        Random rnd = new Random();
        if (rnd.nextFloat() < 0.3f)
            this.distance = rnd.nextFloat()* 0.2f * MAX_DISTANCE;
        else
            this.distance = rnd.nextFloat() * MAX_DISTANCE;
        this.left = rnd.nextBoolean();
    }

    public final void setCoords(float x){
        this.x = x;
        this.c[0] = x + WIDTH_MARGIN;
        this.c[2] = c[0] + WIDTH;
    }

    public final void checkCollisions(Ball b, float sX, float sY){
        if (b.x+b.radius+sX >= this.c[0] && isLeft(b.x, b.y)) {
            b.sX = -b.sX;
            this.reflected = true;
        }
        else if (b.x-b.radius+sX <= this.c[2] && isRight(b.x, b.y)) {
            b.sX = -b.sX;
            this.reflected = true;
        }
        else if (b.y+b.radius+sY >= this.c[1] && isBehind(b.x, b.y)) {
            b.sY = -b.sY;
        }
        else if (b.y-b.radius+sY <= this.c[3] && isInFront(b.x, b.y)) {
            b.sY = -b.sY;
            this.reflected = true;
        }
    }

    public final boolean isInFront(float x, float y) {
        return x > this.c[0] &&
                x < this.c[2] &&
                y > this.c[3];
    }

    public final boolean isBehind(float x, float y){
        return x > this.c[0] &&
                x < this.c[2] &&
                y < this.c[1];
    }

    public final boolean isLeft(float x, float y){
        return x < this.c[0] &&
                y > this.c[1] &&
                y < this.c[3];
    }

    public final boolean isRight(float x, float y){
        return x > this.c[2] &&
                y > this.c[1] &&
                y < this.c[3];
    }

}
