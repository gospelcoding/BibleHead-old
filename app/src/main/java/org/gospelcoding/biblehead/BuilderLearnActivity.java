package org.gospelcoding.biblehead;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuilderLearnActivity extends AppCompatActivity {

    private Verse verse;
    private List<VerseWord> verseWords;
    private List<Integer> indices;
    private int position;

    // Word characters optionally including one apostrophe or hyphen
    private static final Pattern wordPattern = Pattern.compile("\\w+(['-]\\w+)?");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_builder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new LoadVerseTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.learn_activity_menu, menu);
        menu.findItem(R.id.switch_game).setTitle(R.string.hide_words);
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
                resultIntent.putExtra(LearnActivity.VERSE_ID, verse.id);
                resultIntent.putExtra(VerseListActivity.LEARN_GAME, VerseListActivity.HIDE_WORDS);
                setResult(LearnActivity.RESULT_REDIRECT, resultIntent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private class LoadVerseTask extends AsyncTask<Void, Void, Verse> {
        @Override
        public Verse doInBackground(Void...v){
            int verseId = getIntent().getIntExtra(LearnActivity.VERSE_ID, 0);
            return AppDatabase.getDatabase(BuilderLearnActivity.this).verseDao().getById(verseId).get(0);
        }

        @Override
        public void onPostExecute(Verse v){
            verse = v;
            //((TextView) findViewById(R.id.verse_reference)).setText(verse.getReference());
            setTitle(verse.getReference());
            ((TextView) findViewById(R.id.big_verse_text)).setText(verse.text);
            makeBuilderGame(verse);
        }
    }

    private void makeBuilderGame(Verse verse){
        verseWords = new ArrayList();
        List<VerseWord> scrambleWords = new ArrayList();
        indices = new ArrayList();
        Matcher matcher = wordPattern.matcher(verse.text);
        while(matcher.find()){
            String word = matcher.group().toLowerCase();
            VerseWord vWord = new VerseWord(word, verseWords.size());
            verseWords.add(vWord);
            scrambleWords.add(vWord);
            indices.add(matcher.end());
        }

        position = 0;
        Collections.shuffle(scrambleWords);
        makeWordButtons(scrambleWords);
    }

    private void makeWordButtons(List<VerseWord> scrambleWords){
        FlexboxLayout wordContainer = (FlexboxLayout) findViewById(R.id.word_container);
        ContextThemeWrapper newContext = new ContextThemeWrapper(this, R.style.AppTheme_BlueButton);
        for(VerseWord vWord : scrambleWords){
            Button wordButton = new Button(newContext);
            wordButton.setText(vWord.word);
            wordButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View button) {
                    String guess = (String) ((Button) button).getText();
                    checkGuess(guess, button);
                }
            });
            wordContainer.addView(wordButton);
        }
    }

    private void checkGuess(String guess, View button){
        if (verseWords.get(position).word.equals(guess)){
            String text = verse.text.substring(0, indices.get(position));
            ((TextView) findViewById(R.id.verse_text)).setText(text);

            position++;
            if (position == verseWords.size()){
                findViewById(R.id.reset).setVisibility(View.VISIBLE);
                findViewById(R.id.big_mark_learned).setVisibility(View.VISIBLE);
                findViewById(R.id.big_verse_text).setVisibility(View.VISIBLE);
                //findViewById(R.id.scroll_verse_text).setVisibility(View.INVISIBLE);
                //findViewById(R.id.word_container).setVisibility(View.INVISIBLE);
            }

            button.setVisibility(View.INVISIBLE);
            reBluifyButtons();
            final HorizontalScrollView scroll = (HorizontalScrollView) findViewById(R.id.scroll_verse_text);
            scroll.post(new Runnable() {
                @Override
                public void run() {
                    scroll.fullScroll(View.FOCUS_RIGHT);
                }
            });
        }
        else {
            //button.setBackgroundColor(getResources().getColor(R.color.red));
            ViewCompat.setBackgroundTintList(button, ContextCompat.getColorStateList(button.getContext(), R.color.red));
        }
    }

    private void reBluifyButtons(){
        FlexboxLayout wordContainer = findViewById(R.id.word_container);
        for(int i=0; i<wordContainer.getChildCount(); ++i){
            View button = wordContainer.getChildAt(i);
            ViewCompat.setBackgroundTintList(button, ContextCompat.getColorStateList(button.getContext(), R.color.blue));
        }
    }

    public void clickReset(View v){
        recreate();
        // All this is shot because I can't seem to reset the scrolling verse text view
//        v.setVisibility(View.INVISIBLE);
//        findViewById(R.id.big_mark_learned).setVisibility(View.INVISIBLE);
//        findViewById(R.id.big_verse_text).setVisibility(View.INVISIBLE);
//        ((TextView) findViewById(R.id.verse_text)).setText("");
//        ((HorizontalScrollView) findViewById(R.id.scroll_verse_text)).scrollTo(0, 0);
//        position = 0;
//        FlexboxLayout wordContainer = findViewById(R.id.word_container);
//        for(int i=0; i<wordContainer.getChildCount(); ++i){
//            wordContainer.getChildAt(i).setVisibility(View.VISIBLE);
//        }
    }

    public void clickMarkLearned(View v){
        markLearned();
    }

    public void markLearned(){
        verse.learned = true;
        new VerseUpdater().execute(verse);

        Intent result = new Intent();
        result.putExtra(LearnActivity.VERSE_ID, verse.id);
        setResult(RESULT_OK, result);
        finish();
    }

    private class VerseUpdater extends AsyncTask<Verse, Void, Void>{
        @Override
        public Void doInBackground(Verse... verses){
            for(Verse v : verses){
                AppDatabase.getDatabase(BuilderLearnActivity.this).verseDao().update(v);
            }
            return null;
        }
    }
}
