package org.gospelcoding.biblehead;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity
public class Verse {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String text;

    // Reference
    public String bibleBook;
    public int chapterStart;
    public int chapterEnd;
    public int verseStart;
    public int verseEnd;

    // Reviews
    public Date lastReview;
    public int successfulReviews;

    Verse(String text, String bibleBook, int chapterStart, int chapterEnd, int verseStart, int verseEnd){
        this.text = text;
        this.bibleBook = bibleBook;
        this.chapterStart = chapterStart;
        this.chapterEnd = chapterEnd;
        this.verseStart = verseStart;
        this.verseEnd = verseEnd;

        this.lastReview = new Date();
        this.successfulReviews = 0;
    }

    public String getReference(){
        String ref = bibleBook
                + ' ' + String.valueOf(chapterStart)
                + ':' + String.valueOf(verseStart);

        if(chapterEnd != chapterStart){
            ref += '-' + String.valueOf(chapterEnd)
                       + ':' + String.valueOf(verseEnd);
        }
        else if(verseEnd != verseStart){
            ref += '-' + String.valueOf(verseEnd);
        }

        return ref;
    }

    public void markReviewed(boolean success){
        lastReview = new Date();
        if (success)
            ++successfulReviews;
    }

}
