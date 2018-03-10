package org.gospelcoding.biblehead;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BibleVersionContainer {

    private ArrayList<Language> languages;
    private ArrayList<BibleVersion> bibleVersions;

    public BibleVersionContainer(){
        languages = new ArrayList<>(300);
        bibleVersions = new ArrayList<>(350);
    }

    public void add(BibleVersion bibleVersion) {
        languages.add(bibleVersion.getLanguage());
        bibleVersions.add(bibleVersion);
        Log.d("BH ABS", "[" + bibleVersions.size() + "] " + bibleVersion.getName() + "(" + bibleVersion.getAbsId() + ")" + " - " + bibleVersion.langName + "(" + bibleVersion.getLang() + ")");
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public List<BibleVersion> getBibleVersions(Language lang) {
        int first = 0;
        while (!lang.equals(bibleVersions.get(first).getLang()))
            ++first;
        int last = first + 1;
        while (last < bibleVersions.size() &&
                lang.equals(bibleVersions.get(last).getLang()))
            ++last;

        return bibleVersions.subList(first, last);
    }
}
