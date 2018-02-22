package org.gospelcoding.biblehead;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

public class HideWordActivity extends LearnActivity {

    private static final int DEFAULT_STEP = 4;


    private String verseText;
    private String currentText;
    private List<int[]> wordIndices;
    private int position=0;

    @Override
    protected int switchGameTitle(){ return R.string.build_verse; }

    @Override
    protected String switchGame(){ return BUILD_VERSE; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_hideword);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setPeekButtonListener();

        new LoadVerseTask().execute(); //Loads verse and calls buildGame()
    }

    protected void buildGame(Verse verse){
        setTitle(verse.getReference());
        ((TextView) findViewById(R.id.verse_text)).setText(verse.text);

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

    private void showHideButtons(boolean showResetAndLearned){
        int visibility = showResetAndLearned ? View.VISIBLE : View.INVISIBLE;
        findViewById(R.id.reset).setVisibility(visibility);
        findViewById(R.id.big_mark_learned).setVisibility(visibility);

        visibility = showResetAndLearned ? View.INVISIBLE : View.VISIBLE;
        findViewById(R.id.hide_med).setVisibility(visibility);
        findViewById(R.id.hide_fast).setVisibility(visibility);
        findViewById(R.id.peek).setVisibility(View.INVISIBLE);
    }
}