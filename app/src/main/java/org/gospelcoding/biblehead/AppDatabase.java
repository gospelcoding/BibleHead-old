package org.gospelcoding.biblehead;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Verse.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract VerseDao verseDao();
}
