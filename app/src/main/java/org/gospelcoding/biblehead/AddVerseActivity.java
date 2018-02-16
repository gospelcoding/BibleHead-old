package org.gospelcoding.biblehead;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class AddVerseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_verse);
    }

    public void clickSaveVerse(View v){
        Toast.makeText(this, R.string.verse_added, Toast.LENGTH_SHORT).show();
    }
}
