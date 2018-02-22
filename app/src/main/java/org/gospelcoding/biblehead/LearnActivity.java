package org.gospelcoding.biblehead;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LearnActivity extends AppCompatActivity {

    public static final String VERSE_ID = "verse_id";
    public static int RESULT_REDIRECT = 2;

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setPeekButtonListener();

        new LoadVerseTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.learn_activity_menu, menu);
        menu.findItem(R.id.switch_game).setTitle(R.string.build_verse);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.mark_learned:
                markLearned();
                break;
            case R.id.switch_game:
                Intent resultIntent = new Intent();
                resultIntent.putExtra(VERSE_ID, verse.id);
                resultIntent.putExtra(VerseListActivity.LEARN_GAME, VerseListActivity.BUILD_VERSE);
                setResult(RESULT_REDIRECT, resultIntent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(menuItem);
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
            //((TextView) findViewById(R.id.verse_reference)).setText(verse.getReference());
            setTitle(verse.getReference());
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
        findViewById(R.id.peek).setVisibility(View.VISIBLE);

        if (position == wordIndices.size())
            showHideButtons(true);
    }

//    private void rewindStep(int step){
//        int stopPosition = Math.max(position - step, 0);
//        int newPosition;
//        for(newPosition=position; newPosition>stopPosition; --newPosition){
//            int[] indices = wordIndices.get(newPosition - 1);
//            String sub = verseText.substring(indices[0], indices[1]);
//            currentText = currentText.substring(0, indices[0]) + sub + currentText.substring(indices[1]);
//        }
//        position = newPosition;
//        ((TextView) findViewById(R.id.verse_text)).setText(currentText);
//    }

    private String dashes(int count){
        char[] dashes = new char[count];
        Arrays.fill(dashes, '-');
        return new String(dashes);
    }

    private void setPeekButtonListener(){
        ((ImageButton) findViewById(R.id.peek)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                TextView textView = (TextView) findViewById(R.id.verse_text);
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        textView.setText(verseText);
                        return true;
                    case MotionEvent.ACTION_UP:
                        textView.setText(currentText);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

//    public void clickHideRewind(View v){
//        rewindStep(DEFAULT_STEP);
//    }

    public void clickHideMed(View v){
        takeStep(DEFAULT_STEP);
    }

    public void clickHideFast(View v){
        int step = DEFAULT_STEP * 3;
        takeStep(step);
    }

    public void clickReset(View v){
        position = 0;
        currentText = verseText;
        ((TextView) findViewById(R.id.verse_text)).setText(currentText);
        showHideButtons(false);
    }

    public void clickMarkLearned(View v) {
        markLearned();
    }

    public void markLearned(){
        verse.learned = true;
        new VerseUpdater().execute(verse);

        Intent result = new Intent();
        result.putExtra(VERSE_ID, verse.id);
        setResult(RESULT_OK, result);
        finish();
    }

    private void showHideButtons(boolean showResetAndLearned){
        int visibility = showResetAndLearned ? View.VISIBLE : View.INVISIBLE;
        findViewById(R.id.reset).setVisibility(visibility);
        findViewById(R.id.big_mark_learned).setVisibility(visibility);

        visibility = showResetAndLearned ? View.INVISIBLE : View.VISIBLE;
        findViewById(R.id.hide_med).setVisibility(visibility);
        findViewById(R.id.hide_fast).setVisibility(visibility);
        findViewById(R.id.peek).setVisibility(View.INVISIBLE);
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
