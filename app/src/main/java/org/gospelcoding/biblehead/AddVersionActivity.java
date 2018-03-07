package org.gospelcoding.biblehead;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class AddVersionActivity extends AppCompatActivity {

    private VersionAdapter versionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_version);
        ListView versionsList = findViewById(R.id.version_list);
        versionAdapter = new VersionAdapter(this, new ArrayList());
        versionsList.setAdapter(versionAdapter);
    }
}
