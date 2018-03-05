package org.gospelcoding.biblehead;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class DBLBible {

    private String dam;
    private String name;

    public DBLBible(JSONObject bibleJson) throws JSONException {
        dam = bibleJson.getString("dam_id").substring(0, 6);
        name = bibleJson.getString("version_name");
        if (name.length() < 4){
            String volumeName = bibleJson.getString("volume_name");
            if (volumeName.length() > name.length())
                name = volumeName;
        }
    }

    public boolean alreadyIn(List<DBLBible> bibles){
        for (DBLBible existingBible : bibles)
            if (existingBible.getDam().equals(dam))
                return true;
        return false;
    }

    public String getDam(){ return dam; }
    public String getName(){ return name; }

    public String toString(){ return getName(); }
}
