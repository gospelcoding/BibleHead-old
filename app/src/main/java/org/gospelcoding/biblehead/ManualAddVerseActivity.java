package org.gospelcoding.biblehead;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

public class ManualAddVerseActivity extends AddVerseActivity {

    public static final String EDIT_MODE = "edit_mode";
    public static final String VERSE_ID = "verse_id";

    private Verse verse;

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
            multiverseCheck.setChecked(true);
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

    @Override
    public void clickSaveVerse(View v) {
        Verse verse = editMode() ? this.verse : new Verse();
        AsyncTask saveTask = editMode() ? new UpdateVerseTask() : new AddVerseTask();
        saveVerse(verse, saveTask);
    }

    private class LoadVerseTask extends AsyncTask<Void, Void, Verse> {
        @Override
        protected Verse doInBackground(Void... v){
            int verseId = getIntent().getIntExtra(VERSE_ID, -1);
            return AppDatabase.getDatabase(ManualAddVerseActivity.this).verseDao().find(verseId);
        }

        @Override
        protected void onPostExecute(Verse verse){
            ManualAddVerseActivity.this.verse = verse;
            loadVerseForEdit(verse);
        }
    }

    private class UpdateVerseTask extends AsyncTask<Verse, Void, Void> {
        @Override
        protected Void doInBackground(Verse... verses){
            Verse verse = verses[0];
            AppDatabase.getDatabase(ManualAddVerseActivity.this).verseDao().update(verse);
            return null;
        }

        @Override
        protected void onPostExecute(Void v){
            Toast.makeText(getApplicationContext(), getString(R.string.verse_updated, verse.getReference()), Toast.LENGTH_SHORT).show();
            Intent result = new Intent();
            result.putExtra(VERSE_ID, getIntent().getIntExtra(VERSE_ID, -1));
            setResult(RESULT_OK, result);
            finish();
        }
    }
}
