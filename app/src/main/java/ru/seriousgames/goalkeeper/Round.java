package ru.seriousgames.goalkeeper;
/*
 *  Property of Dmitrii Lykov(Дмитрий Лыков) aka Serious Games
 *  2018
 *
 *  Класс Round
 *  описывает логику игровых раундов
 */

public class Round {

    final int FIRST_R_TIME = 60000;
    final int PLUS_TIME = 30000;
    final int FIRST_R_GOALS = 5;
    final int PLUS_GOALS = 5;
    final int num, maxGoals;
    int time;

    public Round(int num){
        this.num = num;
        this.maxGoals = FIRST_R_GOALS + PLUS_GOALS * (num-1);
        this.time = FIRST_R_TIME + PLUS_TIME * (num-1);
    }

}
