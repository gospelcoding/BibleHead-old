package org.gospelcoding.biblehead;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BiblesOrgJsonParser {

    public static BibleVersionContainer parseVersions(JSONObject response) throws JSONException {
        BibleVersionContainer container = new BibleVersionContainer();
        JSONArray versions = response
                                .getJSONObject("response")
                                .getJSONArray("versions");
        for (int i=0; i<versions.length(); ++i) {
            JSONObject versionJson = versions.getJSONObject(i);
            String absId = versionJson.getString("id");
            String lang = versionJson.getString("lang");
            String name = versionJson.getString("name");
            BibleVersion bibleVersion = new BibleVersion(absId, lang, name);
            bibleVersion.abbreviation = versionJson.getString("abbreviation");
            bibleVersion.langName = versionJson.getString("lang_name");
            bibleVersion.copyright = versionJson.getString("copyright");
            container.add(bibleVersion);
        }
        return container;
    }
}
