package org.gospelcoding.biblehead;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class VerseListActivity extends AppCompatActivity {

    public static final int ADD_VERSE_CODE = 1;
    public static final String SHARED_PREFS_TAG = "org.gospelcoding.biblehead.shared_prefs";
    public static final String VERSION = "version";

    VerseArrayAdapter verseArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verse_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        checkIfUpdateAndProcess();

        new LoadVersesTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){

        switch (menuItem.getItemId()){
            case R.id.review_verses:
                Intent intent = new Intent(this, ReviewActivity.class);
                startActivity(intent);
            default:
                Log.e("BHUI", "Unexpected menuItem in VerseListActivity.onOptionsItemSelected");

        }

        return super.onOptionsItemSelected(menuItem);
    }

    private void checkIfUpdateAndProcess(){
        SharedPreferences values = getSharedPreferences(SHARED_PREFS_TAG, 0);
        int lastVersion = values.getInt(VERSION, 0);

        int currentVersion = BuildConfig.VERSION_CODE;
        if (lastVersion != currentVersion) {
            SharedPreferences.Editor valuesEditor = values.edit();
            valuesEditor.putInt(VERSION, currentVersion);
            valuesEditor.commit();
        }
    }

//    private class SetBibleBookNumbersTask extends AsyncTask<Void, Void, Void>{
//        @Override
//        public Void doInBackground(Void... v){
//            AppDatabase db = AppDatabase.getDatabase(VerseListActivity.this);
//            List<BibleBook> bibleBooks = BibleBook.getBibleBooks(VerseListActivity.this);
//            List<Verse> verses = AppDatabase.getDatabase(VerseListActivity.this).verseDao().getAll();
//            for( Verse verse : verses){
//                int i = 0;
//                while(i< bibleBooks.size() && !bibleBooks.get(i).getName().equals(verse.bibleBook))
//                    ++i;
//                if (i < bibleBooks.size()) {
//                    verse.bibleBookNumber = bibleBooks.get(i).getUsfmNumber();
//                    db.verseDao().update(verse);
//                }
//                else {
//                    Log.e("BHDB", "Could not find Bible book for " + verse.bibleBook);
//                }
//            }
//            return null;
//        }
//
//        @Override
//        public void onPostExecute(Void v){
//            Toast.makeText(VerseListActivity.this, "Bible Book Ids Loaded", Toast.LENGTH_SHORT).show();
//            Log.d("BHDB", "Set Bible Book Numbers");
//        }
//    }


    private class LoadVersesTask extends AsyncTask<Void, Void, List<Verse>>{
        @Override
        public List<Verse> doInBackground(Void... v){
            return AppDatabase.getDatabase(VerseListActivity.this).verseDao().getAll();
        }

        @Override
        public void onPostExecute(List<Verse> verses){
            verseArrayAdapter = new VerseArrayAdapter(VerseListActivity.this, verses);
            ListView verseListView = (ListView) findViewById(R.id.verse_list_view);
            verseListView.setAdapter(verseArrayAdapter);
        }
    }

    public void clickAddVerse(View v){
        Intent intent = new Intent(this, AddVerseActivity.class);
        startActivityForResult(intent, ADD_VERSE_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK) {
            int newVerseId = data.getIntExtra(AddVerseActivity.VERSE_ID_CODE, -1);
            new AddVerseTask().execute(newVerseId);
        }
    }

    private class AddVerseTask extends AsyncTask<Integer, Void, List<Verse>>{
        @Override
        public List<Verse> doInBackground(Integer... ids){
            int[] verse_ids = new int[ids.length];
            for(int i=0; i<ids.length; ++i)
                verse_ids[i] = ids[i];

            return AppDatabase.getDatabase(VerseListActivity.this).verseDao().getById(verse_ids);
        }

        @Override
        public void onPostExecute(List<Verse> newVerses){
            for(Verse v : newVerses) {
                verseArrayAdapter.insert(v);
                Toast.makeText(VerseListActivity.this, getString(R.string.verse_added, v.getReference()), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
