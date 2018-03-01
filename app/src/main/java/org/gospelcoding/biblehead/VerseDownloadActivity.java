package org.gospelcoding.biblehead;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.faithcomesbyhearing.dbt.Dbt;
import com.faithcomesbyhearing.dbt.callback.VolumeCallback;
import com.faithcomesbyhearing.dbt.model.Volume;

import java.util.List;

public class VerseDownloadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verse_download);
        Dbt.setApiKey(APIKey.get());
        fillSpinner();
    }

    private void fillSpinner(){
        Dbt.getLibraryVolume(null, "text", null, "Eng", new VolumeCallback() {
            @Override
            public void success(List<Volume> volumes) {
                ArrayAdapter<Volume> volumeAdapter = new ArrayAdapter(VerseDownloadActivity.this, android.R.layout.simple_spinner_item, volumes);
                volumeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ((Spinner) findViewById(R.id.language)).setAdapter(volumeAdapter);
            }

            @Override
            public void failure(Exception e) {
                Toast.makeText(VerseDownloadActivity.this, "Download Failure", Toast.LENGTH_SHORT).show();
                Log.e("BH DBL", e.getMessage());
            }
        });
    }
}
