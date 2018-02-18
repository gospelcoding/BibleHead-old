package org.gospelcoding.biblehead;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class ReviewActivity extends Activity {

    private AppDatabase db;
    private List<Verse> reviewVerses;
    private int currentVerseIndex;
    private TextView verseTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        verseTextView = (TextView) findViewById(R.id.verse_text);

        //TODO This ain't right, reserach correct method
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "biblehead").build();

        new ReviewVerseLoader().execute();
    }

    private class ReviewVerseLoader extends AsyncTask<Void, Void, List<Verse>>{
        @Override
        public List<Verse> doInBackground(Void... v){
            return db.verseDao().getVersesForReview(5);
        }

        @Override
        public void onPostExecute(List<Verse> reviewVerses){
            ReviewActivity.this.reviewVerses = reviewVerses;
            currentVerseIndex = 0;
            reviewNextVerse();
        }
    }

    private void reviewNextVerse(){
        if(currentVerseIndex >= reviewVerses.size()){
            finish();
            return;
        }

        Verse verse = reviewVerses.get(currentVerseIndex);
        setTextView(R.id.verse_reference, verse.getReference());
        setTextView(R.id.verse_text, "");
        showHideButtons(false);
    }

    private void completeReview(){
        showHideButtons(true);
    }

    private void showHideButtons(boolean reviewComplete){
        int reviewingBtnVisibility = reviewComplete ? View.GONE : View.VISIBLE;
        int reviewCompleteBtnVisibility = reviewComplete ? View.VISIBLE : View.GONE;

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
        appendToVerseText(" \t\n");
    }

    public void clickShowPhrase(View v) {
        appendToVerseText(".,;?!\n");
    }

    private void appendToVerseText(String delimiter){
        String remainder = getRemainderText();
        String append = getUpToAndIncluding(remainder, delimiter);
        verseTextView.append(append);
        if(append.length() == remainder.length())
            completeReview();
    }

    public void clickShowAll(View v){
        Verse verse = reviewVerses.get(currentVerseIndex);
        setTextView(R.id.verse_text, verse.text);
        completeReview();
    }

    private String getUpToAndIncluding(String baseText, String delimiters){
        int i=0;

        // If string starts with delimiter, pass it
        while(i<baseText.length() && delimiters.contains(baseText.substring(i, i+1)))
            ++i;

        // Get everything that's not the delimiter
        while(i<baseText.length() && !delimiters.contains(baseText.substring(i, i+1)))
            ++i;

        //Get the entire ending delimiter
        while(i<baseText.length() && delimiters.contains(baseText.substring(i, i+1)))
            ++i;

        return baseText.substring(0, i);
    }

    private String getRemainderText(){
        int startIndex = verseTextView.getText().length();
        String fullText = reviewVerses.get(currentVerseIndex).text;
        return fullText.substring(startIndex);
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
                db.verseDao().update(v);
            }
            return null;
        }
    }
}
