package ru.seriousgames.goalkeeper.database;

public abstract class TaskListener <T>{

    abstract public void onTaskCompleted(T... vals);
}
