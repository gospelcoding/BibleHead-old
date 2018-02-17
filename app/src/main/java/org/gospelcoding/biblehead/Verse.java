package org.gospelcoding.biblehead;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Verse {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String text;
    public String bibleBook;
    public int chapterStart;
    public int chapterEnd;
    public int verseStart;
    public int verseEnd;

    Verse(String text, String bibleBook, int chapterStart, int chapterEnd, int verseStart, int verseEnd){
        this.text = text;
        this.bibleBook = bibleBook;
        this.chapterStart = chapterStart;
        this.chapterEnd = chapterEnd;
        this.verseStart = verseStart;
        this.verseEnd = verseEnd;
    }

//    Verse(int id, String text){
//        this.id = id;
//        this.text = text;
//    }

}
