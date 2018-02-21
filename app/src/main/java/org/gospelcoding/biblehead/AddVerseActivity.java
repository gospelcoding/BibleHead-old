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

    public static final String EDIT_MODE = "edit_mode";
    public static final String VERSE_ID = "verse_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupViews();

        if (editMode())
            new LoadVerseTask().execute();
    }

    private boolean editMode(){
        return getIntent().getBooleanExtra(EDIT_MODE, false);
    }

    private void setupViews(){
        setContentView(R.layout.activity_add_verse);
        Spinner bookSpinner = findViewById(R.id.bible_book);
        List<BibleBook> bibleBooks = BibleBook.getBibleBooks(this);
        ArrayAdapter<BibleBook> bookAdapter = new ArrayAdapter<BibleBook>(this, R.layout.book_spinner_item, bibleBooks);
        bookAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bookSpinner.setAdapter(bookAdapter);
    }

    private void loadVerseForEdit(Verse verse){
        ((EditText) findViewById(R.id.verse_text)).setText(verse.text);
        ((EditText) findViewById(R.id.chapter_start)).setText(String.valueOf(verse.chapterStart));
        ((EditText) findViewById(R.id.verse_start)).setText(String.valueOf(verse.verseStart));
        setBookSpinnerValue(verse.bibleBookNumber);

        if (verse.chapterStart==verse.chapterEnd && verse.verseStart==verse.verseEnd) {  //One verse only
            ((CheckBox) findViewById(R.id.multiverse_check)).setChecked(false);
        }
        else {
            CheckBox multiverseCheck = ((CheckBox) findViewById(R.id.multiverse_check));
            clickSetMultiverse(multiverseCheck);
            ((EditText) findViewById(R.id.chapter_end)).setText(String.valueOf(verse.chapterEnd));
            ((EditText) findViewById(R.id.verse_end)).setText(String.valueOf(verse.verseEnd));
        }
    }

    private void setBookSpinnerValue(int bibleBookNumber){
        Spinner bookSpinner = ((Spinner) findViewById(R.id.bible_book));
        ArrayAdapter<BibleBook> bookAdapter = (ArrayAdapter) bookSpinner.getAdapter();
        int i = 0;
        while(i<bookAdapter.getCount() && bookAdapter.getItem(i).getUsfmNumber() != bibleBookNumber)
            ++i;
        if (i < bookAdapter.getCount())
            bookSpinner.setSelection(i);
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
            if (editMode()) {
                verse.id = getIntent().getIntExtra(VERSE_ID, -1);
                new UpdateVerseTask().execute(verse);
            }
            else
                new AddVerseTask().execute(verse);
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

    private class LoadVerseTask extends AsyncTask<Void, Void, Verse> {
        @Override
        protected Verse doInBackground(Void... v){
            int verseId = getIntent().getIntExtra(VERSE_ID, -1);
            return AppDatabase.getDatabase(AddVerseActivity.this).verseDao().find(verseId);
        }

        @Override
        protected void onPostExecute(Verse verse){
            loadVerseForEdit(verse);
        }
    }

    private class AddVerseTask extends AsyncTask<Verse, Void, Integer> {
        @Override
        protected Integer doInBackground(Verse... verses){
            Verse verse = verses[0];
            return (int) AppDatabase.getDatabase(AddVerseActivity.this).verseDao().insert(verse);
        }

        @Override
        protected void onPostExecute(Integer verseId){
            Intent result = new Intent();
            result.putExtra(VERSE_ID, verseId);
            setResult(RESULT_OK, result);
            finish();
        }
    }

    private class UpdateVerseTask extends AsyncTask<Verse, Void, Void> {
        @Override
        protected Void doInBackground(Verse... verses){
            Verse verse = verses[0];
            AppDatabase.getDatabase(AddVerseActivity.this).verseDao().update(verse);
            return null;
        }

        @Override
        protected void onPostExecute(Void v){
            Intent result = new Intent();
            result.putExtra(VERSE_ID, getIntent().getIntExtra(VERSE_ID, -1));
            setResult(RESULT_OK, result);
            finish();
        }
    }
}
