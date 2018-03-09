package org.gospelcoding.biblehead;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class BibleVersionContainer {

    private TreeSet<Language> languages;
    private TreeSet<BibleVersion> bibleVersions;

    public BibleVersionContainer(){
        languages = new TreeSet<>();
        bibleVersions = new TreeSet<>();
    }

    public void add(BibleVersion bibleVersion) {
        languages.add(bibleVersion.getLanguage());
        bibleVersions.add(bibleVersion);
        Log.d("BH ABS", "[" + bibleVersions.size() + "] " + bibleVersion.getName() + "(" + bibleVersion.getAbsId() + ")" + " - " + bibleVersion.langName + "(" + bibleVersion.getLang() + ")");
    }

    public List<Language> getLanguages() {
        return new ArrayList<>(languages);
    }

    public List<BibleVersion> getBibleVersions(Language lang) {
        BibleVersion min = new BibleVersion(null, lang.getCode(), null);
        BibleVersion max = new BibleVersion(null, lang.getCode() + "a", null);
        SortedSet<BibleVersion> matchingVersions = bibleVersions.subSet(min, max);
        return new ArrayList<>(matchingVersions);
    }
}
