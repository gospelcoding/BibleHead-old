package org.gospelcoding.biblehead;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.regex.Pattern;

public abstract class LearnActivity extends AppCompatActivity {

    public static final String VERSE_ID = "verse_id";
    public static int RESULT_REDIRECT = 2;
    public static final String LEARN_GAME = "learn_game";
    public static final String HIDE_WORDS = "hide_words";
    public static final String BUILD_VERSE = "build_verse";

    protected Verse verse;

    // Word characters optionally including one apostrophe or hyphen
    protected static final Pattern wordPattern = Pattern.compile("\\w+(['’-]\\w+)?");

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.learn_activity_menu, menu);
        menu.findItem(R.id.switch_game).setTitle(switchGameTitle());
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
                resultIntent.putExtra(LEARN_GAME, switchGame());
                setResult(RESULT_REDIRECT, resultIntent);
                finish();
                break;
            case R.id.go_back:
                finish();
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    protected abstract int switchGameTitle();
    protected abstract String switchGame();

    protected class LoadVerseTask extends AsyncTask<Void, Void, Verse>{
        @Override
        public Verse doInBackground(Void...v){
            int verseId = getIntent().getIntExtra(VERSE_ID, 0);
            return AppDatabase.getDatabase(LearnActivity.this).verseDao().getById(verseId).get(0);
        }

        @Override
        public void onPostExecute(Verse v){
            verse = v;
            buildGame(verse);
        }
    }

    protected abstract void buildGame(Verse verse);

    public void clickMarkLearned(View v) {
        markLearned();
    }

    protected void markLearned(){
        Intent result = new Intent();
        result.putExtra(VERSE_ID, verse.id);
        setResult(RESULT_OK, result);
        finish();
    }
}
