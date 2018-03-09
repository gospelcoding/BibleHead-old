package org.gospelcoding.biblehead;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface BibleVersionDao {

    @Query("SELECT * FROM bibleversion ORDER BY lang, name")
    List<BibleVersion> getAll();

    @Insert
    long insert(BibleVersion bibleVersion);

    @Delete
    void delete(BibleVersion bibleVersion);
}