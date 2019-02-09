package ru.seriousgames.goalkeeper;
/*
 *  Property of Dmitrii Lykov(Дмитрий Лыков) aka Serious Games
 *  2018
 *
 *  Класс Ball
 *  описывает свойства и поведение футбольного мяча
 */

public class Ball {

    float x, y, radius, speed;
    float sX, sY;

    public Ball(float x, float y, float radius){
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public final void setAngle(float deg){
        float rad = (float)Math.toRadians(deg);// 308 градусов - попадет в угол
        this.sY = (float)Math.sin(rad);
        this.sX = (float)Math.cos(rad);
    }
    public final void setCoords(float x, float y){
        this.x = x;
        this.y = y;
    }

}
