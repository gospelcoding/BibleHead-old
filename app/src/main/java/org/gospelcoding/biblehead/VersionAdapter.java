package org.gospelcoding.biblehead;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class VersionAdapter extends ArrayAdapter<BibleVersion> {

    private Context context;

    public VersionAdapter(Context context, List<BibleVersion> versions){
        super(context, -1, versions);
        versions.add(null); // Null at end of list is for "Add" item
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        BibleVersion version = getItem(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listItem;
        if (version == null){
            listItem = inflater.inflate(R.layout.add_version_list_item, parent, false);
        }
        else {
            listItem = inflater.inflate(R.layout.bible_version_list_item, parent, false);
            TextView versionName = listItem.findViewById(R.id.version_name);
            versionName.setText(version.getName());
        }
        return listItem;
    }


}
