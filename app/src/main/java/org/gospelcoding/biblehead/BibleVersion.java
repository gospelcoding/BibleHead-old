package org.gospelcoding.biblehead;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class BibleVersion implements Comparable<BibleVersion> {

    @PrimaryKey(autoGenerate = true)
    public int id;

    private String absId;
    private String name;
    private String lang;

    public String abbreviation;
    public String langName;
    public String copyright;


    public BibleVersion(String absId, String lang, String name){
        this.absId = denullify(absId);
        this.lang = denullify(lang);
        this.name = denullify(name);
    }

    private String denullify(String s) {
        return (s==null) ? "" : s;
    }

    public Language getLanguage() {
        return new Language(lang, langName);
    }

    @Override
    public int compareTo(BibleVersion other) {
        int langCompare = lang.compareToIgnoreCase(other.getLang());
        if (langCompare != 0)
            return langCompare;
        return name.compareToIgnoreCase(other.getName());
    }

    public boolean equals(BibleVersion other) {
        return absId.equals(other.getAbsId());
    }

    public String getAbsId() {
        return absId;
    }

    public String getName() {
        return name;
    }

    public String getLang() {
        return lang;
    }

    public String toString() { return name; }
}
