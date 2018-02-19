package org.gospelcoding.biblehead;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
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

    public void clickSetMultiverse(View multiverseCheck){
        copyEditTextAndIncrement(R.id.chapter_start, R.id.chapter_end, 0);
        copyEditTextAndIncrement(R.id.verse_start, R.id.verse_end, 1);
        multiverseCheck.setVisibility(View.GONE);
        findViewById(R.id.dash).setVisibility(View.VISIBLE);
        findViewById(R.id.chapter_end).setVisibility(View.VISIBLE);
        findViewById(R.id.colon2).setVisibility(View.VISIBLE);
        findViewById(R.id.verse_end).setVisibility(View.VISIBLE);
    }

    private void copyEditTextAndIncrement(int fromEditText, int toEditText, int increment){
        String s = ((EditText) findViewById(fromEditText)).getText().toString();
        try {
            int i = Integer.parseInt(s) + increment;
            s = String.valueOf(i);
            ((EditText) findViewById(toEditText)).setText(s);
        }
        catch (NumberFormatException e){
            // No worries ;)
        }
    }

    public void clickSaveVerse(View v) {
        Verse verse = buildVerse();
        if (verse.isValid()) {
            new SaveVerseTask().execute(verse);
            finish();
        }
        else {
            String message = "";
            for(int i=0; i<verse.validationErrors.size(); ++i){
                message += getString(verse.validationErrors.get(i));
                if (i+1 < verse.validationErrors.size())
                    message += '\n';
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    private Verse buildVerse(){
        String text = ((EditText) findViewById(R.id.verse_text)).getText().toString();
        String bibleBook = ((Spinner) findViewById(R.id.bible_book)).getSelectedItem().toString();
        int chapterStart = numberFromEditText(R.id.chapter_start);
        int verseStart = numberFromEditText(R.id.verse_start);
        if (((CheckBox) findViewById(R.id.multiverse_check)).isChecked()) {
            int chapterEnd = numberFromEditText(R.id.chapter_end);
            int verseEnd = numberFromEditText(R.id.verse_end);
            return new Verse(text, bibleBook, chapterStart, chapterEnd, verseStart, verseEnd);
        }
        else {
            return new Verse(text, bibleBook, chapterStart, verseStart);
        }
    }

    private int numberFromEditText(int editTextId){
        EditText editText = ((EditText) findViewById(editTextId));
        String s = editText.getText().toString();
        if (s == null || s.length() == 0) {
            s = editText.getHint().toString();
            editText.setText(s);
        }
        try {
            int i = Integer.parseInt(s);
            return i;
        }
        catch (NumberFormatException e){
            editText.setText("0");
            return 0; //Which will be invalid
        }
    }

    private class SaveVerseTask extends AsyncTask<Verse, Void, Verse> {
        protected Verse doInBackground(Verse... verses){
            Verse verse = verses[0];
            db.verseDao().insert(verse);
            return verse;
        }

        protected void onPostExecute(Verse verse){
            Toast.makeText(getApplicationContext(), getString(R.string.verse_added, verse.getReference()), Toast.LENGTH_SHORT).show();
        }
    }
}
