package org.gospelcoding.biblehead;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Verse {
    @PrimaryKey(autoGenerate = true)
    public int id;

    private String text;

    Verse(String text){
        this.text = text;
    }

//    Verse(int id, String text){
//        this.id = id;
//        this.text = text;
//    }

    public String getText(){
        return text;
    }
}
