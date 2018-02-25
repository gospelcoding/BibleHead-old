package org.gospelcoding.biblehead;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class VerseBuilderActivity extends LearnActivity {

    private static int DISPLAYED_WORDS = 12;

    private static final String VERSE_WORDS = "verseWords";
    private static final String SHUFFLED_WORDS = "shuffledWords";
    private static final String INDICES = "indices";
    private static final String POSITION = "position";
    private static final String RESET = "reset";
    private static final String BUTTON_WORDS = "buttonWords";
    private static final String BUTTON_VISIBILITIES = "buttonVisibilities";

    ContextThemeWrapper buttonContext = new ContextThemeWrapper(this, R.style.AppTheme_BlueButton);

    private ArrayList<String> verseWords;
    private ArrayList<String> shuffledWords;
    private ArrayList<Integer> indices;
    private int position;
    private boolean reset = false;

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
        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickReset(view);
            }
        });

        if (savedInstanceState == null || savedInstanceState.getBoolean(RESET))
            new LoadVerseTask().execute();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        if (reset)
            savedInstanceState.putBoolean(RESET, true);
        else {
            savedInstanceState.putStringArrayList(VERSE_WORDS, verseWords);
            savedInstanceState.putStringArrayList(SHUFFLED_WORDS, shuffledWords);
            savedInstanceState.putIntegerArrayList(INDICES, indices);
            savedInstanceState.putInt(POSITION, position);
            ArrayList<String> buttonWords = new ArrayList(DISPLAYED_WORDS);
            ArrayList<Integer> buttonVisibilities = new ArrayList(DISPLAYED_WORDS);
            FlexboxLayout wordContainer = findViewById(R.id.word_container);
            for (int i = 0; i < wordContainer.getChildCount(); ++i) {
                Button button = (Button) wordContainer.getChildAt(i);
                buttonWords.add((String) button.getText());
                buttonVisibilities.add(button.getVisibility());
            }
            savedInstanceState.putStringArrayList(BUTTON_WORDS, buttonWords);
            savedInstanceState.putIntegerArrayList(BUTTON_VISIBILITIES, buttonVisibilities);
        }

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        if( !savedInstanceState.getBoolean(RESET)) {
            verseWords = savedInstanceState.getStringArrayList(VERSE_WORDS);
            shuffledWords = savedInstanceState.getStringArrayList(SHUFFLED_WORDS);
            indices = savedInstanceState.getIntegerArrayList(INDICES);
            position = savedInstanceState.getInt(POSITION);

            ((TextView) findViewById(R.id.big_verse_text)).setText(verseText);
            if (position == verseWords.size())
                finishGame();
            else {
                if (position > 0)
                    ((TextView) findViewById(R.id.verse_text)).setText(verseText.substring(0, indices.get(position - 1)));
                ArrayList<String> buttonWords = savedInstanceState.getStringArrayList(BUTTON_WORDS);
                ArrayList<Integer> buttonVisibilities = savedInstanceState.getIntegerArrayList(BUTTON_VISIBILITIES);
                FlexboxLayout wordContainer = findViewById(R.id.word_container);
                for (int i = 0; i < buttonWords.size(); ++i)
                    addButton(wordContainer, i, buttonWords.get(i), buttonVisibilities.get(i));

            }
        }
    }

    protected void buildGame(Verse verse){
        setTitle(verse.getReference());
        ((TextView) findViewById(R.id.big_verse_text)).setText(verse.text);

        verseWords = new ArrayList();
        indices = new ArrayList();
        Matcher matcher = wordPattern.matcher(verse.text);
        while(matcher.find()){
            String word = matcher.group().toLowerCase();
            verseWords.add(word);
            indices.add(matcher.end());
        }

        position = 0;
        makeWordButtons(shuffle(verseWords));
    }

    // A riff on the alorithm used by Collections.shuffle
    // Scrambles words while guaranteeing that no word moves
    // farther back than DISPLAYED_WORD places which would
    // break the game.
    private List<String> shuffle(ArrayList<String> verseWords){
        ArrayList<String> shuffledWords = (ArrayList) verseWords.clone();
        for(int i=shuffledWords.size()-1; i>0; --i){
            String swapMe = shuffledWords.get(i);
            int swapIndex = i - randomInt(DISPLAYED_WORDS-1, i);
            shuffledWords.set(i, shuffledWords.get(swapIndex));
            shuffledWords.set(swapIndex, swapMe);
        }
        this.shuffledWords = shuffledWords;
        return shuffledWords;
    }

    private int randomInt(int limit1, int limit2){
        int limit = Math.min(limit1, limit2);
        return (int) (limit * Math.random());
    }

    private void makeWordButtons(List<String> scrambleWords){
        FlexboxLayout wordContainer = (FlexboxLayout) findViewById(R.id.word_container);
        int numberOfButtons = Math.min(DISPLAYED_WORDS, scrambleWords.size());
        for(int i=0; i<numberOfButtons; ++i){
            addButton(wordContainer, i, scrambleWords.get(i));
        }
    }

    private void addButton(FlexboxLayout wordContainer, int index, String word){
        addButton(wordContainer, index, word, View.VISIBLE);
    }

    private void addButton(FlexboxLayout wordContainer, int index, String word, int visibility){
        Button wordButton = new Button(buttonContext);
        wordButton.setText(word);
        wordButton.setVisibility(visibility);
        wordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                String guess = (String) ((Button) button).getText();
                checkGuess(guess, button);
            }
        });
        wordContainer.addView(wordButton, index);
    }

    private void checkGuess(String guess, View button){
        if (verseWords.get(position).equals(guess)){
            String text = verseText.substring(0, indices.get(position));
            ((TextView) findViewById(R.id.verse_text)).setText(text);
            final ScrollView scroll = findViewById(R.id.scroll_verse_text);
            scroll.post(new Runnable() {
                @Override
                public void run() {
                    scroll.fullScroll(View.FOCUS_DOWN);
                }
            });

            position++;
            if (position == verseWords.size())
                finishGame();
            else{
                FlexboxLayout wordContainer = (FlexboxLayout) button.getParent();
                reBluifyButtons(wordContainer);
                int addWordIndex = position + DISPLAYED_WORDS - 1;
                if (addWordIndex < shuffledWords.size()){
                    int viewIndex = wordContainer.indexOfChild(button);
                    wordContainer.removeView(button);
                    addButton(wordContainer, viewIndex, shuffledWords.get(addWordIndex));
                }
                else
                    button.setVisibility(View.INVISIBLE);
            }
        }
        else {
            ViewCompat.setBackgroundTintList(button, ContextCompat.getColorStateList(button.getContext(), R.color.red));
        }
    }

    private void reBluifyButtons(FlexboxLayout wordContainer){
        for(int i=0; i<wordContainer.getChildCount(); ++i){
            View button = wordContainer.getChildAt(i);
            ViewCompat.setBackgroundTintList(button, ContextCompat.getColorStateList(button.getContext(), R.color.blue));
        }
    }

    private void finishGame(){
        findViewById(R.id.reset).setVisibility(View.VISIBLE);
        findViewById(R.id.big_mark_learned).setVisibility(View.VISIBLE);
        findViewById(R.id.big_verse_text).setVisibility(View.VISIBLE);
        findViewById(R.id.word_container).setVisibility(View.INVISIBLE);
    }

    public void clickReset(View v){
        reset = true;
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
