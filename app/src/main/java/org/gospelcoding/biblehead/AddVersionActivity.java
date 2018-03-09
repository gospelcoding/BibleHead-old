package org.gospelcoding.biblehead;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

public class AddVersionActivity extends AppCompatActivity implements AddVersionFragment.AddVersionFragmentListener {

    private VersionAdapter versionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_version);
        new LoadBibleVersionsFromDBTask().execute();
        addClickListner();
    }

    private void addClickListner(){
        ((ListView) findViewById(R.id.version_list)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BibleVersion bibleVersion = versionAdapter.getItem(i);
                if (bibleVersion == null) { // Add version item
                    new AddVersionFragment().show(getSupportFragmentManager(), "add_version");
                }
                else {
                    displayVersionDialog(bibleVersion);
                }
            }
        });
    }

    @Override
    public void onAddVersion(BibleVersion bibleVersion) {
        new AddBibleVersionTask().execute(bibleVersion);
    }

    private void displayVersionDialog(final BibleVersion bibleVersion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(bibleVersion.getName());
        builder.setMessage(Html.fromHtml(bibleVersion.copyright));
        builder.setNegativeButton(R.string.go_back, null);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new DeleteBibleVersionTask().execute(bibleVersion);
            }
        });
        builder.create().show();
    }

    private void updateVersionList(List<BibleVersion> bibleVersions) {
        ListView versionsList = findViewById(R.id.version_list);
        versionAdapter = new VersionAdapter(AddVersionActivity.this, bibleVersions);
        versionsList.setAdapter(versionAdapter);

    }

    private class LoadBibleVersionsFromDBTask extends AsyncTask<Void, Void, List<BibleVersion>> {
        @Override
        public List<BibleVersion> doInBackground(Void... v) {
            return AppDatabase.getDatabase(getApplicationContext()).bibleVersionDao().getAll();
        }

        @Override
        public void onPostExecute(List<BibleVersion> bibleVersions) {
            updateVersionList(bibleVersions);
        }
    }

    private class AddBibleVersionTask extends AsyncTask<BibleVersion, Void, List<BibleVersion>> {
        @Override
        public List<BibleVersion> doInBackground(BibleVersion... versions) {
            BibleVersion bibleVersion = versions[0];
            AppDatabase database = AppDatabase.getDatabase(getApplicationContext());
            database.bibleVersionDao().insert(bibleVersion);
            return database.bibleVersionDao().getAll();
        }

        @Override
        public void onPostExecute(List<BibleVersion> bibleVersions) {
            updateVersionList(bibleVersions);
        }
    }

    private class DeleteBibleVersionTask extends AsyncTask<BibleVersion, Void, List<BibleVersion>> {
        @Override
        public List<BibleVersion> doInBackground(BibleVersion... versions) {
            BibleVersion bibleVersion = versions[0];
            AppDatabase database = AppDatabase.getDatabase(getApplicationContext());
            database.bibleVersionDao().delete(bibleVersion);
            return database.bibleVersionDao().getAll();
        }

        @Override
        public void onPostExecute(List<BibleVersion> bibleVersions) {
            updateVersionList(bibleVersions);
        }
    }
}
