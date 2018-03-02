package org.gospelcoding.biblehead;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.faithcomesbyhearing.dbt.Dbt;
import com.faithcomesbyhearing.dbt.callback.VolumeCallback;
import com.faithcomesbyhearing.dbt.callback.VolumeLanguageCallback;
import com.faithcomesbyhearing.dbt.model.Volume;
import com.faithcomesbyhearing.dbt.model.VolumeLanguage;

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
        final Spinner languageSpinner = findViewById(R.id.language);
        Dbt.getLibraryVolumeLanguage("text", null, new VolumeLanguageCallback() {
            @Override
            public void success(List<VolumeLanguage> volumeLanguages) {
                VolumeLanguageAdapter langAdapter = new VolumeLanguageAdapter(VerseDownloadActivity.this, volumeLanguages);
                languageSpinner.setAdapter(langAdapter);
            }

            @Override
            public void failure(Exception e) {
                Log.e("BH DBL", "Failed to download Language List");
            }
        });

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                VolumeLanguage language = (VolumeLanguage) adapterView.getItemAtPosition(i);
                fillVolumeSpinner(language.getLanguageCode());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void fillVolumeSpinner(String languageCode){
        Dbt.getLibraryVolume(null, "text", null, languageCode, new VolumeCallback() {
            @Override
            public void success(List<Volume> volumes) {
                VolumeAdapter volumeAdapter = new VolumeAdapter(VerseDownloadActivity.this, volumes);
                volumeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ((Spinner) findViewById(R.id.volumes)).setAdapter(volumeAdapter);
            }

            @Override
            public void failure(Exception e) {
                Toast.makeText(VerseDownloadActivity.this, "Download Failure", Toast.LENGTH_SHORT).show();
                Log.e("BH DBL", e.getMessage());
            }
        });

    }

    private class VolumeLanguageAdapter extends ArrayAdapter<VolumeLanguage> implements SpinnerAdapter{
        public VolumeLanguageAdapter(Context context, List<VolumeLanguage> languages){
            super(context, -1, languages);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            return getView(position, parent, android.R.layout.simple_spinner_item);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent){
            return getView(position, parent, android.R.layout.simple_spinner_dropdown_item);
        }

        private View getView(int position, ViewGroup parent, int resource){
            VolumeLanguage language = getItem(position);
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(resource, parent, false);
            ((TextView) view.findViewById(android.R.id.text1)).setText(language.getLanguageName());
            return view;
        }
    }

    private class VolumeAdapter extends ArrayAdapter<Volume> implements SpinnerAdapter{
        public VolumeAdapter(Context context, List<Volume> volumes){
            super(context, -1, volumes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            return getView(position, parent, android.R.layout.simple_spinner_item);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent){
            return getView(position, parent, android.R.layout.simple_spinner_dropdown_item);
        }

        private View getView(int position, ViewGroup parent, int resource){
            Volume volume = getItem(position);
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(resource, parent, false);
            ((TextView) view.findViewById(android.R.id.text1)).setText(volume.getVolumeName());
            return view;
        }
    }
}
