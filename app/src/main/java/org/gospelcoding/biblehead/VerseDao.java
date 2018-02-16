package org.gospelcoding.biblehead;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface VerseDao {
    @Query("SELECT * From verse")
    List<Verse> getAll();

    @Insert
    void insert(Verse verse);
}
