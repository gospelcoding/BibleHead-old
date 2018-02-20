package org.gospelcoding.biblehead;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Database(entities = {Verse.class}, version = 2)
@TypeConverters({AppDatabase.Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase dbInstance;

    public abstract VerseDao verseDao();

    public static synchronized AppDatabase getDatabase(Context context){
        if (dbInstance == null)
         return Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "biblehead")
                    .addMigrations(MIGRATION_1_2).build();
        else
            return dbInstance;
    }

    public static class Converters {
        @TypeConverter
        public Date fromString(String s){
            try {
                SimpleDateFormat df = getSimpleDateFormat();
                return df.parse(s);
            }
            catch (ParseException e){
                Log.e("BibleHead DB", e.getMessage());
                e.printStackTrace();
                return null;
            }
        }

        @TypeConverter
        public String dateToString(Date d){
            SimpleDateFormat df = getSimpleDateFormat();
            return df.format(d);
        }

        private SimpleDateFormat getSimpleDateFormat(){
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    }


    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE verse ADD COLUMN bibleBookNumber INTEGER NOT NULL DEFAULT 0");
        }
    };
}

