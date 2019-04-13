package ru.seriousgames.goalkeeper;
/*
 *  Property of Dmitrii Lykov(Дмитрий Лыков) aka Serious Games
 *  2018
 *
 *  Класс Values
 *  хранит и объявляет константы
 */

public class Values {

    final float BALL_CENTER_X, BALL_CENTER_Y;                           // центр экрана - там появляется мяч
    final float ROTATION_PER_MS;                                        // скорость вращения: градусы/мс
    final float FORCE_BAR_X, FORCE_BAR_Y;                               // координаты шкалы "силы"
    // высота стрелки силы: костанта, т. к. стрелка двигается только по горизонтали
    // мин. и макс. возможная координата Х стрелки силы
    final float FORCE_ARROW_Y, FORCE_ARROW_START_X, FORCE_ARROW_END_X;
    final float FORCE_ARROW_SPEED;                                      // скорость стрелки "силы"
    // мин. скорость мяча; коэф. увеличения скорости мяча = макс. скорость - мин. скорость
    final float BALL_MIN_SPEED, BALL_PLUS_SPEED;
    final float PLAYER_1_SPEED;                                         // макс. возможная скорость вратаря

    /* КОНСТРУКТОР
     * width, height - ширина и высота экрана
     * fHeight - высота спрайта футбольного поля
     */
    public Values(float width, float height, float ff, float fHeight){
        BALL_CENTER_X = width*0.5f;
        BALL_CENTER_Y = fHeight*0.375f;
        ROTATION_PER_MS = 0.12f; //настоящая величина = 0.12f и +0.025f каждый раунд
        FORCE_BAR_X = width*0.0972f;
        FORCE_BAR_Y = height-332*ff;
        FORCE_ARROW_Y = FORCE_BAR_Y+100*ff;
        FORCE_ARROW_END_X = width*0.8472f;
        FORCE_ARROW_SPEED = width*0.0022f; //0.002f
        FORCE_ARROW_START_X = width*0.151f;
        BALL_MIN_SPEED = 0.1f;
        BALL_PLUS_SPEED = 0.7f; //0.6f
        PLAYER_1_SPEED = width*0.00022f;
    }

}
