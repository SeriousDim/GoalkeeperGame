package ru.seriousgames.goalkeeper.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Record {
    @PrimaryKey
    @NonNull
    public String id;

    public int value;

    public Record(String id, int value){
        this.id = id;
        this.value = value;
    }
}
