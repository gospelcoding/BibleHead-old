package org.gospelcoding.biblehead;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Database(entities = {Verse.class}, version = 1)
@TypeConverters({AppDatabase.Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract VerseDao verseDao();

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
}
