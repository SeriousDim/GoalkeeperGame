package ru.seriousgames.goalkeeper;
/*
 *  Property of Dmitrii Lykov(Дмитрий Лыков) aka Serious Games
 *  2018
 *
 *  Класс ObjectStorage
 *  хранит игровые объекты, а также некоторые переменные, которые не явл. константами (изменяются)
 */

public class ObjectStorage {

    final Ball ball;
    final Gates gates;
    final GatePlayer goalkepper;
    float arrowAngle; // угол стрелки, задающий угол, она же красная стрелка; в градусах
    boolean leftRot;  // должна ли красная стрелка поворачиваться влево

    float forceArrowX;  // абсцисса стрелки силы
    boolean forceArrowLeft; // должна ли стрлека силы двигаться влево

    public ObjectStorage(Ball ball, Gates gates, GatePlayer goalkepper){
        this.ball = ball;
        this.gates = gates;
        this.goalkepper = goalkepper;
        this.arrowAngle = 230f;
        this.leftRot = false;
        this.forceArrowLeft = false;
    }

}
