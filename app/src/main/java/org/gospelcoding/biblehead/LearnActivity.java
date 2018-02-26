package org.gospelcoding.biblehead;

import android.content.Intent;
import android.content.SharedPreferences;
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

    protected String verseText;

    // Word characters optionally including one apostrophe or hyphen
    protected static final Pattern wordPattern = Pattern.compile("\\w+(['’-]\\w+)?");

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.learn_activity_menu, menu);
        menu.findItem(R.id.switch_game).setTitle(switchGameMenuItem());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.mark_learned:
                markLearned();
                break;
            case R.id.switch_game:
                switchGame();
                break;
            case R.id.go_back:
                finish();
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    protected abstract int switchGameMenuItem();
    protected abstract String switchGameName();

    protected void switchGame(){
        SharedPreferences.Editor valuesEditor = getSharedPreferences(VerseListActivity.SHARED_PREFS_TAG, 0).edit();
        valuesEditor.putString(LearnActivity.LEARN_GAME, switchGameName());
        valuesEditor.commit();
        Class activity = (switchGameName()==HIDE_WORDS) ? HideWordActivity.class : VerseBuilderActivity.class;
        Intent intent = new Intent(this, activity);
        intent.putExtra(VERSE_ID, getIntent().getIntExtra(VERSE_ID, -1));
        startActivity(intent);
        finish();
    }

    protected class LoadVerseTask extends AsyncTask<Void, Void, Verse>{
        @Override
        public Verse doInBackground(Void...v){
            int verseId = getIntent().getIntExtra(VERSE_ID, 0);
            return AppDatabase.getDatabase(LearnActivity.this).verseDao().getById(verseId).get(0);
        }

        @Override
        public void onPostExecute(Verse verse){
            verseText = verse.text;
            buildGame(verse);
        }
    }

    protected abstract void buildGame(Verse verse);

    public void clickMarkLearned(View v) {
        markLearned();
    }

    protected void markLearned(){
//        Intent result = new Intent();
//        result.putExtra(VERSE_ID, getIntent().getIntExtra(VERSE_ID, -1));
//        setResult(RESULT_OK, result);
//        finish();
        SharedPreferences.Editor valuesEditor = getSharedPreferences(VerseListActivity.SHARED_PREFS_TAG, 0).edit();
        valuesEditor.putBoolean(VerseListActivity.LEARNED_A_VERSE, true);
        valuesEditor.commit();

        new MarkLearnedTask().execute(getIntent().getIntExtra(VERSE_ID, -1));
    }

    private class MarkLearnedTask extends AsyncTask<Integer, Void, Void>{
        @Override
        public Void doInBackground(Integer... verseIds){
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            Verse verse = db.verseDao().find(verseIds[0]);
            verse.toggleLearned(true);
            db.verseDao().update(verse);
            return null;
        }

        @Override
        public void onPostExecute(Void v){
            finish();
        }
    }
}
