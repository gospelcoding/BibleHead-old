package org.gospelcoding.biblehead;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LearnActivity extends Activity {

    public static final String VERSE_ID = "verse_id";

    private Verse verse;

    private String verseText;
    private String currentText;
    private List<int[]> wordIndices;
    private int position=0;

    // Word characters optionally including one apostrophe or hyphen
    private static final Pattern wordPattern = Pattern.compile("\\w+(['-]\\w+)?");
    private static final int DEFAULT_STEP = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_hideword);

        new LoadVerseTask().execute();
    }

    private class LoadVerseTask extends AsyncTask<Void, Void, Verse>{
        @Override
        public Verse doInBackground(Void...v){
            int verseId = getIntent().getIntExtra(VERSE_ID, 0);
            return AppDatabase.getDatabase(LearnActivity.this).verseDao().getById(verseId).get(0);
        }

        @Override
        public void onPostExecute(Verse v){
            verse = v;
            buildHideWordGame(verse);
            ((TextView) findViewById(R.id.verse_reference)).setText(verse.getReference());
            ((TextView) findViewById(R.id.verse_text)).setText(verseText);
        }
    }

    private void buildHideWordGame(Verse verse){
        currentText = verseText = verse.text;
        wordIndices = new ArrayList();
        Matcher matcher = wordPattern.matcher(verseText);
        while(matcher.find()){
            int[] find = {matcher.start(), matcher.end()};
            wordIndices.add(find);
        }
        Collections.shuffle(wordIndices);
    }

    private void takeStep(int step){
        int stopPosition = Math.min(position + step, wordIndices.size());
        int newPosition;
        for(newPosition=position; newPosition<stopPosition; ++newPosition){
            int[] indices = wordIndices.get(newPosition);
            String sub = dashes(indices[1] - indices[0]);
            currentText = currentText.substring(0, indices[0]) + sub + currentText.substring(indices[1]);
        }
        position = newPosition;
        ((TextView) findViewById(R.id.verse_text)).setText(currentText);
    }

    private void rewindStep(int step){
        int stopPosition = Math.max(position - step, 0);
        int newPosition;
        for(newPosition=position; newPosition>stopPosition; --newPosition){
            int[] indices = wordIndices.get(newPosition - 1);
            String sub = verseText.substring(indices[0], indices[1]);
            currentText = currentText.substring(0, indices[0]) + sub + currentText.substring(indices[1]);
        }
        position = newPosition;
        ((TextView) findViewById(R.id.verse_text)).setText(currentText);
    }

    private String dashes(int count){
        char[] dashes = new char[count];
        Arrays.fill(dashes, '-');
        return new String(dashes);
    }

    public void clickHideRewind(View v){
        rewindStep(DEFAULT_STEP);
    }

    public void clickHideMed(View v){
        takeStep(DEFAULT_STEP);
    }

    public void clickHideFast(View v){
        int step = DEFAULT_STEP * 3;
        takeStep(step);
    }

    public void clickMarkLearned(View v){
        verse.learned = true;
        new VerseUpdater().execute(verse);

        Intent result = new Intent();
        result.putExtra(VERSE_ID, verse.id);
        setResult(RESULT_OK, result);
        finish();
    }

    private class VerseUpdater extends AsyncTask<Verse, Void, Void>{
        @Override
        public Void doInBackground(Verse... verses){
            for(Verse v : verses){
                AppDatabase.getDatabase(LearnActivity.this).verseDao().update(v);
            }
            return null;
        }
    }
}
