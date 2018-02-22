package org.gospelcoding.biblehead;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

public class VerseBuilderActivity extends LearnActivity {

    private List<VerseWord> verseWords;
    private List<Integer> indices;
    private int position;

    @Override
    protected int switchGameTitle(){ return R.string.hide_words; }

    @Override
    protected String switchGame(){ return HIDE_WORDS; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_builder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new LoadVerseTask().execute();
    }

    protected void buildGame(Verse verse){
        setTitle(verse.getReference());
        ((TextView) findViewById(R.id.big_verse_text)).setText(verse.text);

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
}
