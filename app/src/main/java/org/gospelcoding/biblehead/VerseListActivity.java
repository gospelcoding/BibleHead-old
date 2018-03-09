package org.gospelcoding.biblehead;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class VerseListActivity extends AppCompatActivity
                               implements PopupMenu.OnMenuItemClickListener,
                                          DeleteConfirmFragment.DeleteConfirmFragmentListener {

    public static final int ADD_VERSE_CODE = 1;
    public static final int LEARN_VERSE_CODE = 2;
    public static final int EDIT_VERSE_CODE = 3;

    public static final String SHARED_PREFS_TAG = "org.gospelcoding.biblehead.shared_prefs";
    public static final String VERSION = "version";
    public static final String LEARNED_A_VERSE = "learned_a_verse";

    public static final String NOTIFICATION_CHANNEL = "daily_review_reminder";

    VerseArrayAdapter verseArrayAdapter;
    int popupMenuVerseId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUI();

        checkIfUpdateAndProcess();

        setupNotificationChannel();
    }

    private void setupUI(){
        setContentView(R.layout.activity_verse_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void checkIfUpdateAndProcess(){
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        SharedPreferences values = getSharedPreferences(SHARED_PREFS_TAG, 0);
        int lastVersion = values.getInt(VERSION, 0);

        int currentVersion = BuildConfig.VERSION_CODE;
        if (lastVersion != currentVersion) {
            SharedPreferences.Editor valuesEditor = values.edit();
            valuesEditor.putInt(VERSION, currentVersion);
            valuesEditor.commit();
        }
    }

    private void setupNotificationChannel(){
        if (Build.VERSION.SDK_INT < 26)
            return;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String id = NOTIFICATION_CHANNEL;
        CharSequence name = getString(R.string.app_name);
        String description = getString(R.string.channel_description);
        NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
        mChannel.setDescription(description);
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.WHITE);
        mNotificationManager.createNotificationChannel(mChannel);
    }

    private void setupAlarm(){
        AlarmManager.setAlarmIfNecessary(getApplicationContext());
    }

    @Override
    protected void onStart(){
        super.onStart();

        new LoadVersesTask().execute();
        setupAlarm();
    }

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
            if (verses.size() == 0)
                launchAddVerseActivity();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        boolean learnedAVerse = getSharedPreferences(SHARED_PREFS_TAG, 0).getBoolean(LEARNED_A_VERSE, false);
        if (!learnedAVerse)
            menu.findItem(R.id.review_verses).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){

        switch (menuItem.getItemId()){
            case R.id.review_verses:
                Intent intent = new Intent(this, ReviewActivity.class);
                startActivity(intent);
                break;
            case R.id.add_verse:
                launchAddVerseActivity();
                break;
            case R.id.settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            default:
                Log.e("BHUI", "Unexpected menuItem in VerseListActivity.onOptionsItemSelected");

        }

        return super.onOptionsItemSelected(menuItem);
    }

    private void launchAddVerseActivity(){
//        Intent intent = new Intent(this, AddVerseActivity.class);
//        startActivityForResult(intent, ADD_VERSE_CODE);
        Intent intent = new Intent(this, AddVerseActivity.class);
        startActivity(intent);
    }

    public void clickLearn(View button) {
        int verseId = (Integer) ((View) button.getParent()).getTag();
        learn(verseId);
    }

    public void learn(int verseId){
        String game = getSharedPreferences(SHARED_PREFS_TAG, 0).getString(LearnActivity.LEARN_GAME, LearnActivity.HIDE_WORDS);
        Class activity = (game.equals(LearnActivity.HIDE_WORDS)) ? HideWordActivity.class : VerseBuilderActivity.class;
        Intent intent = new Intent(this, activity);
        intent.putExtra(LearnActivity.VERSE_ID, verseId);
        startActivity(intent);
    }

    public void clickShowVerseMenu(View button){
        popupMenuVerseId = (Integer) ((View) button.getParent()).getTag();
        boolean learned = verseArrayAdapter.find(popupMenuVerseId).learned;
        PopupMenu popup = new PopupMenu(this, button);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.verse_menu);
        int hideMeView = learned ? R.id.mark_learned : R.id.mark_unlearned;
        popup.getMenu().removeItem(hideMeView);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item){
        int verseId = popupMenuVerseId;
        popupMenuVerseId = -1;

        switch(item.getItemId()){
            case R.id.mark_learned:
                markLearned(verseId);
                return true;

            case R.id.mark_unlearned:
                markUnlearned(verseId);
                return true;

            case R.id.edit_verse:
                editVerse(verseId);
                return true;

            case R.id.delete_verse:
                deleteVerse(verseId);
                return true;

            default:
                return false;
        }
    }

    private void markLearned(int verseId){
        verseArrayAdapter.markLearned(verseId, true);
        SharedPreferences values = getSharedPreferences(SHARED_PREFS_TAG, 0);
        if (!values.getBoolean(LEARNED_A_VERSE, false)){
            SharedPreferences.Editor valuesEditor = values.edit();
            valuesEditor.putBoolean(LEARNED_A_VERSE, true);
            valuesEditor.commit();
            Toolbar toolbar = ((Toolbar) findViewById(R.id.toolbar));
            toolbar.getMenu().findItem(R.id.review_verses).setVisible(true);
            setupAlarm();
        }
        new MarkVerseLearnedUnlearnedTask(true).execute(verseId);
    }

    private void markUnlearned(int verseId){
        verseArrayAdapter.markLearned(verseId, false);
        new MarkVerseLearnedUnlearnedTask(false).execute(verseId);
    }

    private void editVerse(int verseId) {
        Intent intent = new Intent(this, AddVerseActivity.class);
        intent.putExtra(AddVerseActivity.EDIT_MODE, true);
        intent.putExtra(AddVerseActivity.VERSE_ID, verseId);
        startActivityForResult(intent, EDIT_VERSE_CODE);
    }

    private void deleteVerse(int verseId) {
        Verse verse = verseArrayAdapter.find(verseId);
        DeleteConfirmFragment deleteConfirm = new DeleteConfirmFragment();
        deleteConfirm.setVerse(verse);
        deleteConfirm.show(getFragmentManager(), "delete_verse");
    }

    @Override
    public void onDeleteConfirm(DialogFragment dialog, Verse verse){
        verseArrayAdapter.remove(verse);
        new DeleteVerseTask().execute(verse);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data){
//        else if(resultCode == LearnActivity.RESULT_REDIRECT){
//            int verseId = data.getIntExtra(LearnActivity.VERSE_ID, -1);
//            String game = data.getStringExtra(LearnActivity.LEARN_GAME);
//            SharedPreferences.Editor valuesEditor = getSharedPreferences(SHARED_PREFS_TAG, 0).edit();
//            valuesEditor.putString(LearnActivity.LEARN_GAME, game);
//            valuesEditor.commit();
//            learn(verseId);
//        }
//    }

    public void clickShowText(View view){
        int verseId = (Integer) ((View) view.getParent()).getTag();
        String text = verseArrayAdapter.find(verseId).text;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(text)
                .setPositiveButton(R.string.ok, null);
        builder.create().show();
    }

    @Override
    public void onBackPressed(){
        View verseText = findViewById(R.id.verse_text);
        if (verseText.getVisibility() == View.VISIBLE)
            verseText.setVisibility(View.GONE);
        else
            super.onBackPressed();
    }

//    private class AddVerseTask extends AsyncTask<Integer, Void, List<Verse>>{
//        @Override
//        public List<Verse> doInBackground(Integer... ids){
//            int[] verse_ids = new int[ids.length];
//            for(int i=0; i<ids.length; ++i)
//                verse_ids[i] = ids[i];
//
//            return AppDatabase.getDatabase(VerseListActivity.this).verseDao().getById(verse_ids);
//        }
//
//        @Override
//        public void onPostExecute(List<Verse> newVerses){
//            for(Verse v : newVerses) {
//                verseArrayAdapter.insert(v);
//                Toast.makeText(VerseListActivity.this, getString(R.string.verse_added, v.getReference()), Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    private class MarkVerseLearnedUnlearnedTask extends AsyncTask<Integer, Void, Void>{

        private boolean learned;

        public MarkVerseLearnedUnlearnedTask(boolean learned){
            this.learned = learned;
        }

        @Override
        public Void doInBackground(Integer... verseIds){
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            Verse verse = db.verseDao().find(verseIds[0]);
            verse.toggleLearned(learned);
            db.verseDao().update(verse);
            return null;
        }
    }

//    private class EditVerseTask extends AsyncTask<Integer, Void, Verse>{
//        @Override
//        public Verse doInBackground(Integer... ids){
//            return AppDatabase.getDatabase(VerseListActivity.this).verseDao().find(ids[0]);
//        }
//
//        @Override
//        public void onPostExecute(Verse verse){
//            verseArrayAdapter.update(verse);
//            Toast.makeText(VerseListActivity.this, getString(R.string.verse_updated, verse.getReference()), Toast.LENGTH_SHORT).show();
//        }
//    }

    private class DeleteVerseTask extends AsyncTask<Verse, Void, Verse>{
        @Override
        public Verse doInBackground(Verse... verses){
            AppDatabase.getDatabase(getApplicationContext()).verseDao().deleteVerses(verses);
            return verses[0];
        }

        @Override
        public void onPostExecute(Verse verse){
            Toast.makeText(VerseListActivity.this, getString(R.string.verse_deleted, verse.getReference()), Toast.LENGTH_SHORT).show();
        }
    }
}
