package org.gospelcoding.biblehead;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddVerseActivity extends Activity {

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupViews();

        //TODO This ain't right, reserach correct method
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "biblehead").build();
    }

    private void setupViews(){
        setContentView(R.layout.activity_add_verse);
        Spinner bookSpinner = findViewById(R.id.bible_book);
        ArrayAdapter<CharSequence> bookAdapter = ArrayAdapter.createFromResource(this, R.array.bible_books, R.layout.book_spinner_item);
        bookAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bookSpinner.setAdapter(bookAdapter);
    }

    public void clickSaveVerse(View v){
        //TODO Reject if blank
        String text = ((EditText) findViewById(R.id.verse_text)).getText().toString();
        String bibleBook = ((Spinner) findViewById(R.id.bible_book)).getSelectedItem().toString();
        // TODO Check if blank and assign the hint value if so.
        // TODO Validate?
        int chapterStart = Integer.parseInt(((EditText) findViewById(R.id.chapter_start)).getText().toString());
        int chapterEnd = Integer.parseInt(((EditText) findViewById(R.id.chapter_end)).getText().toString());
        int verseStart = Integer.parseInt(((EditText) findViewById(R.id.verse_start)).getText().toString());
        int verseEnd = Integer.parseInt(((EditText) findViewById(R.id.verse_end)).getText().toString());
        new SaveVerseTask().execute(new Verse(text, bibleBook, chapterStart, chapterEnd, verseStart, verseEnd));
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
