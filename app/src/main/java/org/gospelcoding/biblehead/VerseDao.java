package org.gospelcoding.biblehead;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface VerseDao {
    @Query("SELECT * From verse")
    List<Verse> getAll();

    @Query("SELECT * FROM verse ORDER BY (3*(julianday('now') - julianday(lastReview)) - successfulReviews) DESC LIMIT :numberOfVerses")
    List<Verse> getVersesForReview(int numberOfVerses);

    @Insert
    void insert(Verse verse);

    @Update
    void update(Verse verse);
}