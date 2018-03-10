package org.gospelcoding.biblehead;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public abstract class AddVerseActivity extends AppCompatActivity {

    public void clickSetMultiverse(View multiverseCheck){
        copyEditTextAndIncrement(R.id.chapter_start, R.id.chapter_end, 0);
        copyEditTextAndIncrement(R.id.verse_start, R.id.verse_end, 1);
        multiverseCheck.setVisibility(View.GONE);
        findViewById(R.id.dash).setVisibility(View.VISIBLE);
        findViewById(R.id.chapter_end).setVisibility(View.VISIBLE);
        findViewById(R.id.colon2).setVisibility(View.VISIBLE);
        findViewById(R.id.verse_end).setVisibility(View.VISIBLE);
    }

    protected void copyEditTextAndIncrement(int fromEditText, int toEditText, int increment){
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

    protected boolean validateVerse(Verse verse) {
        boolean valid = verse.isValid();
        if (!valid) {
            String message = "";
            for(int i=0; i<verse.validationErrors.size(); ++i){
                message += getString(verse.validationErrors.get(i));
                if (i+1 < verse.validationErrors.size())
                    message += '\n';
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
        return valid;
    }

    protected void saveVerse(Verse verse, AsyncTask saveTask) {
        setVerseValues(verse);
        if (validateVerse(verse))
            saveTask.execute(verse);

    }

    public void clickSaveVerse(View v) {
        Verse verse = new Verse();
        saveVerse(verse, new AddVerseTask());
    }

    protected void setVerseValues(Verse verse){
        String text = ((EditText) findViewById(R.id.verse_text)).getText().toString();
        BibleBook book = (BibleBook) ((Spinner) findViewById(R.id.bible_book)).getSelectedItem();
        String bibleBook = book.getName();
        int bibleBookNumber = book.getUsfmNumber();
        int chapterStart = numberFromEditText(R.id.chapter_start);
        int verseStart = numberFromEditText(R.id.verse_start);
        if (((CheckBox) findViewById(R.id.multiverse_check)).isChecked()) {
            int chapterEnd = numberFromEditText(R.id.chapter_end);
            int verseEnd = numberFromEditText(R.id.verse_end);
            verse.update(text, bibleBook, bibleBookNumber, chapterStart, chapterEnd, verseStart, verseEnd);
        }
        else {
            verse.update(text, bibleBook, bibleBookNumber, chapterStart, verseStart);
        }
    }

    protected int numberFromEditText(int editTextId){
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

    protected class AddVerseTask extends AsyncTask<Verse, Void, Integer> {
        String verseReference;

        @Override
        protected Integer doInBackground(Verse... verses){
            Verse verse = verses[0];
            verseReference = verse.getReference();
            return (int) AppDatabase.getDatabase(getApplicationContext()).verseDao().insert(verse);
        }

        @Override
        protected void onPostExecute(Integer verseId){
            Toast.makeText(getApplicationContext(), getString(R.string.verse_added, verseReference), Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
