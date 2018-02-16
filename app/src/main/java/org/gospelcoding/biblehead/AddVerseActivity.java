package org.gospelcoding.biblehead;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddVerseActivity extends Activity {

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_verse);
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "biblehead").build();
    }

    public void clickSaveVerse(View v){
        String text = ((EditText) findViewById(R.id.verse_text)).getText().toString();
        new SaveVerseTask().execute(new Verse(text));
        finish();
    }

    private class SaveVerseTask extends AsyncTask<Verse, Void, Void> {
        protected Void doInBackground(Verse... verses){
            for(Verse v : verses){
                db.verseDao().insert(v);
            }
            return null;
        }

        protected void onPostExecute(Void v){
            Toast.makeText(getApplicationContext(), R.string.verse_added, Toast.LENGTH_SHORT).show();
        }
    }
}
