package org.gospelcoding.biblehead;

import org.json.JSONException;
import org.json.JSONObject;

public class DBLLanguage {

    private String code;
    private String name;

    public DBLLanguage(JSONObject languageJson) throws JSONException{
        code = languageJson.getString("language_family_code");
        name = languageJson.getString("language_family_name");
    }

    public String getCode(){ return code; }
    public String getName(){ return name; }

    public String toString(){
        return getName();
    }
}
