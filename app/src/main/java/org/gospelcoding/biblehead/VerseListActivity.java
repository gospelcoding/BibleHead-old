package org.gospelcoding.biblehead;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.List;

public class VerseListActivity extends AppCompatActivity {

    public static final int ADD_VERSE_CODE = 1;

    AppDatabase db;
    VerseArrayAdapter verseArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verse_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //TODO This ain't right, reserach correct method
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "biblehead").build();

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

    private class LoadVersesTask extends AsyncTask<Void, Void, List<Verse>>{
        @Override
        public List<Verse> doInBackground(Void... v){
            return db.verseDao().getAll();
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

            return db.verseDao().getById(verse_ids);
        }

        @Override
        public void onPostExecute(List<Verse> newVerses){
            for(Verse v : newVerses)
                verseArrayAdapter.insert(v, 0);
            verseArrayAdapter.notifyDataSetChanged();
        }
    }
}
