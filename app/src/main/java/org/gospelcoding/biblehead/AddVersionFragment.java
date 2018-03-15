package org.gospelcoding.biblehead;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class AddVersionFragment extends AppCompatDialogFragment
                                implements AdapterView.OnItemSelectedListener,
                                            DialogInterface.OnClickListener{

    private AddVersionFragmentListener listener;
    private RequestQueue requestQueue;
    private BibleVersionContainer versionContainer;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        requestQueue = Volley.newRequestQueue(getContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setNegativeButton(R.string.cancel, null)
               .setPositiveButton(R.string.add_version, this);
        AlertDialog dialog = builder.create();
        dialog.setView(dialog.getLayoutInflater().inflate(R.layout.fragment_add_version, null));
        setupSpinners(dialog);
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener = (AddVersionFragmentListener) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement DeleteConfirmFramentListener");
        }
    }

    private void setupSpinners(final AlertDialog dialog){
        String url = BiblesOrgRequest.url(BiblesOrgRequest.VERSIONS);
        BiblesOrgRequest request = new BiblesOrgRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                setupLangSpinner(dialog, response);
            }
        });
        requestQueue.add(request);
    }

    private void setupLangSpinner(AlertDialog dialog, JSONObject response) {
        try{
            final Spinner langSpinner = dialog.findViewById(R.id.lang_spinner);
            versionContainer = BiblesOrgJsonParser.parseVersions(response);
            ArrayAdapter<Language> languagesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, versionContainer.getLanguages());
            languagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            langSpinner.setAdapter(languagesAdapter);
            langSpinner.setOnItemSelectedListener(this);
            ((Spinner) dialog.findViewById(R.id.version_spinner)).setOnItemSelectedListener(this);
        }
        catch (JSONException e){
            Toast.makeText(getContext(), R.string.version_download_failure, Toast.LENGTH_SHORT).show();
            Log.e("BH DBL", "You dirty rascal: " + e.getMessage());
        }

    }

    @Override
    public void onItemSelected(AdapterView spinner, View v, int i, long l) {
        switch (spinner.getId()) {
            case R.id.lang_spinner:
                languageSelected(spinner, i);
                break;
            case R.id.version_spinner:
                versionSelected(spinner, i);
                break;
        }
    }

    private void languageSelected(AdapterView langSpinner, int i) {
        Language lang = (Language) langSpinner.getItemAtPosition(i);
        List<BibleVersion> versions = versionContainer.getBibleVersions(lang);
        ArrayAdapter<BibleVersion> versionsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, versions);
        versionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner versionSpinner = getDialog().findViewById(R.id.version_spinner);
        versionSpinner.setAdapter(versionsAdapter);
    }

    private void versionSelected(AdapterView versionSpinner, int i) {
        BibleVersion bibleVersion = (BibleVersion) versionSpinner.getItemAtPosition(i);
        TextView copyrightText = getDialog().findViewById(R.id.copyright_text);
        copyrightText.setText(Html.fromHtml(bibleVersion.copyright));
    }

    @Override
    public void onNothingSelected(AdapterView adapterView) {
        // Do nothing
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        Spinner versionSpinner = getDialog().findViewById(R.id.version_spinner);
        BibleVersion bibleVersion = (BibleVersion) versionSpinner.getSelectedItem();
        listener.onAddVersion(bibleVersion);
    }

    public interface AddVersionFragmentListener {
        void onAddVersion(BibleVersion bibleVersion);
    }
}
