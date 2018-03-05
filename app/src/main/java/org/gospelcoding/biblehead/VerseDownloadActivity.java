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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VerseDownloadActivity extends AppCompatActivity {

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verse_download);
        requestQueue = Volley.newRequestQueue(this);
        fillLanguageSpinner();
    }
    private void fillLanguageSpinner(){
        String url = "http://dbt.io/library/volumelanguagefamily?media=text&v=2&key=" + APIKey.get();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    ArrayList<DBLLanguage> languages = new ArrayList(response.length());
                    for(int i=0; i<response.length(); ++i)
                        languages.add(new DBLLanguage(response.getJSONObject(i)));
                    setLangSpinnerAdapter(languages);
                }
                catch (JSONException e){
                    Log.e("BH DBL", "You dirty rascal: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Log.e("BH DBL", error.getMessage());
            }
        });
        requestQueue.add(request);
        ((Spinner) findViewById(R.id.language)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                DBLLanguage language = (DBLLanguage) adapterView.getItemAtPosition(i);
                fillVersionSpinner(language);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setLangSpinnerAdapter(ArrayList<DBLLanguage> languages){
        Spinner langSpinner = findViewById(R.id.language);
        ArrayAdapter<DBLLanguage> adapter = new ArrayAdapter<DBLLanguage>(this, android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        langSpinner.setAdapter(adapter);
    }

    private void fillVersionSpinner(DBLLanguage language){
        String url = "http://dbt.io/library/volume?media=text&v=2&key=" + APIKey.get() + "&language_family_code=" + language.getCode();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    ArrayList<DBLBible> bibles = new ArrayList(response.length());
                    for(int i=0; i<response.length(); ++i) {
                        DBLBible bible = new DBLBible(response.getJSONObject(i));
                        if (!bible.alreadyIn(bibles))
                            bibles.add(bible);
                    }
                    setVersionSpinnerAdapter(bibles);
                }
                catch (JSONException e){
                    Log.e("BH DBL", "You dirty rascal: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Log.e("BH DBL", error.getMessage());
            }
        });
        requestQueue.add(request);
        ((Spinner) findViewById(R.id.volumes)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                DBLBible bible = (DBLBible) adapterView.getItemAtPosition(i);
                fillBookSpinner(bible);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setVersionSpinnerAdapter(ArrayList<DBLBible> bibles){
        Spinner langSpinner = findViewById(R.id.volumes);
        ArrayAdapter<DBLBible> adapter = new ArrayAdapter<DBLBible>(this, android.R.layout.simple_spinner_item, bibles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        langSpinner.setAdapter(adapter);
    }

    private void fillBookSpinner(DBLBible bible){
        String url = "http://dbt.io/library/book?v=2&key=" + APIKey.get() + "&dam_id=" + bible.getDam();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    ArrayList<DBLBibleBook> books = new ArrayList(response.length());
                    for(int i=0; i<response.length(); ++i)
                        books.add(new DBLBibleBook(response.getJSONObject(i)));

                    setBookSpinnerAdapter(books);
                }
                catch (JSONException e){
                    Log.e("BH DBL", "You dirty rascal: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Log.e("BH DBL", error.getMessage());
            }
        });
        requestQueue.add(request);
    }

    private void setBookSpinnerAdapter(ArrayList<DBLBibleBook> books){
        Spinner spinner = findViewById(R.id.bible_book);
        ArrayAdapter<DBLBibleBook> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, books);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void clickGetChapter(View v){
        String damId = ((DBLBibleBook) ((Spinner) findViewById(R.id.bible_book)).getSelectedItem()).getDam() + "1ET";
        String bookId = ((DBLBibleBook) ((Spinner) findViewById(R.id.bible_book)).getSelectedItem()).getBookId();
        String chapterNum = ((EditText) findViewById(R.id.chapter)).getText().toString();
        String url = "http://dbt.io/text/verse?v=2&key=" + APIKey.get() + "&dam_id=" + damId + "&book_id=" + bookId + "&chapter_id=" + chapterNum;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("BH DBL", response.toString());1
                TextView tv = findViewById(R.id.chapter_text);
                tv.setText("");
                try{
                    for(int i=0; i<response.length(); ++i)
                        tv.append(response.getJSONObject(i).getString("verse_text"));
                }
                catch (JSONException e) {
                    Log.e("BH DBL", "You dirty rascal: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("BH DBL", "You dirty rascal: " + error.getMessage());

            }
        });
        requestQueue.add(request);
    }
}
