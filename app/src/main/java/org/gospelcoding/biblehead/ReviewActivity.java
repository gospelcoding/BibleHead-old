package org.gospelcoding.biblehead;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReviewActivity extends AppCompatActivity {

    // One or more word characters
    // zero or more anything, non-greedy
    // The specified delimeter group
    // Zero or more non-word characters, greedy
    private static final Pattern NEXT_WORD = Pattern.compile("\\w+.*?[\\s]\\W*");
    private static final Pattern NEXT_PHRASE = Pattern.compile("\\w+.*?[,;.?!\n]\\W*");

    private List<Verse> reviewVerses;
    private int currentVerseIndex;
    private TextView verseTextView;
    private String verseTextRemainder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        verseTextView = (TextView) findViewById(R.id.verse_text);

        new ReviewVerseLoader().execute();
    }

    private class ReviewVerseLoader extends AsyncTask<Void, Void, List<Verse>>{
        @Override
        public List<Verse> doInBackground(Void... v){
            return AppDatabase.getDatabase(ReviewActivity.this).verseDao().getVersesForReview(5);
        }

        @Override
        public void onPostExecute(List<Verse> reviewVerses){
            if (reviewVerses.size() == 0) {
                Toast.makeText(getApplicationContext(), R.string.no_learned_verses, Toast.LENGTH_LONG).show();
                finish();
            }
            else {
                ReviewActivity.this.reviewVerses = reviewVerses;
                currentVerseIndex = 0;
                reviewNextVerse();
            }
        }
    }

    private void reviewNextVerse(){
        if(currentVerseIndex >= reviewVerses.size()){
            finish();
            return;
        }

        Verse verse = reviewVerses.get(currentVerseIndex);
        verseTextRemainder = verse.text;
        setTextView(R.id.verse_reference, verse.getReference());
        setTextView(R.id.verse_number, getString(R.string.x_of_y, currentVerseIndex+1, reviewVerses.size()));
        setTextView(R.id.verse_text, "");
        showHideButtons(false);
    }

    private void completeReview(){
        showHideButtons(true);
    }

    private void showHideButtons(boolean reviewComplete){
        int reviewingBtnVisibility = reviewComplete ? View.INVISIBLE : View.VISIBLE;
        int reviewCompleteBtnVisibility = reviewComplete ? View.VISIBLE : View.INVISIBLE;

        findViewById(R.id.review_failure).setVisibility(reviewCompleteBtnVisibility);
        findViewById(R.id.review_success).setVisibility(reviewCompleteBtnVisibility);
        findViewById(R.id.show_word).setVisibility(reviewingBtnVisibility);
        findViewById(R.id.show_phrase).setVisibility(reviewingBtnVisibility);
        findViewById(R.id.show_all).setVisibility(reviewingBtnVisibility);
    }

    private void setTextView(int textViewId, String text){
        TextView tv = (TextView) findViewById(textViewId);
        tv.setText(text);
    }

    public void clickShowWord(View v){
        appendToVerseText(NEXT_WORD);
    }

    public void clickShowPhrase(View v) {
        appendToVerseText(NEXT_PHRASE);
    }

    private void appendToVerseText(Pattern pattern){
        Matcher matcher = pattern.matcher(verseTextRemainder);
        String append;
        if(matcher.find()){
            append = verseTextRemainder.substring(0, matcher.end());
            verseTextRemainder = verseTextRemainder.substring(matcher.end());
        }
        else {
          append = verseTextRemainder;
          verseTextRemainder = "";
        }

        verseTextView.append(append);
        scrollToBottom();
        if(verseTextRemainder.length() == 0)
            completeReview();
    }

    private void scrollToBottom(){
        final ScrollView scrollView = (ScrollView) findViewById(R.id.verse_text_scroll);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    public void clickShowAll(View v){
        verseTextView.append(verseTextRemainder);
        scrollToBottom();
        completeReview();
    }

    public void clickReviewFailure(View v){
        updateVerseAsReviewed(false);
        ++currentVerseIndex;
        reviewNextVerse();
    }

    public void clickReviewSuccess(View v){
        updateVerseAsReviewed(true);
        ++currentVerseIndex;
        reviewNextVerse();
    }

    private void updateVerseAsReviewed(boolean success){
        Verse verse = reviewVerses.get(currentVerseIndex);
        verse.markReviewed(success);
        new VerseUpdater().execute(verse);
    }


    private class VerseUpdater extends AsyncTask<Verse, Void, Void>{
        @Override
        public Void doInBackground(Verse... verses){
            for(Verse v : verses){
                AppDatabase.getDatabase(ReviewActivity.this).verseDao().update(v);
            }
            return null;
        }
    }
}
