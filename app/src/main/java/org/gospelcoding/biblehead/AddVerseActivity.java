package org.gospelcoding.biblehead;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

public class AddVerseActivity extends Activity {

    public static final String VERSE_ID_CODE = "org.gospelcoding.biblehead.verse_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupViews();
    }

    private void setupViews(){
        setContentView(R.layout.activity_add_verse);
        Spinner bookSpinner = findViewById(R.id.bible_book);
        List<BibleBook> bibleBooks = BibleBook.getBibleBooks(this);
        ArrayAdapter<BibleBook> bookAdapter = new ArrayAdapter<BibleBook>(this, R.layout.book_spinner_item, bibleBooks);
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
        BibleBook book = (BibleBook) ((Spinner) findViewById(R.id.bible_book)).getSelectedItem();
        String bibleBook = book.getName();
        int bibleBookNumber = book.getUsfmNumber();
        int chapterStart = numberFromEditText(R.id.chapter_start);
        int verseStart = numberFromEditText(R.id.verse_start);
        if (((CheckBox) findViewById(R.id.multiverse_check)).isChecked()) {
            int chapterEnd = numberFromEditText(R.id.chapter_end);
            int verseEnd = numberFromEditText(R.id.verse_end);
            return new Verse(text, bibleBook, bibleBookNumber, chapterStart, chapterEnd, verseStart, verseEnd);
        }
        else {
            return new Verse(text, bibleBook, bibleBookNumber, chapterStart, verseStart);
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

    private class SaveVerseTask extends AsyncTask<Verse, Void, Integer> {
        protected Integer doInBackground(Verse... verses){
            Verse verse = verses[0];
            return (int) AppDatabase.getDatabase(AddVerseActivity.this).verseDao().insert(verse);
        }

        protected void onPostExecute(Integer verseId){
            Intent result = new Intent();
            result.putExtra(VERSE_ID_CODE, verseId);
            setResult(RESULT_OK, result);
            finish();
        }
    }
}
