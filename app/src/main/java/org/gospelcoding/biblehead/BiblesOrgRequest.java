package org.gospelcoding.biblehead;

import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import java.util.HashMap;
import java.util.Map;

public class BiblesOrgRequest extends JsonObjectRequest {

    public static final String BASE_URL = "https://bibles.org/v2/";
    public static final String URL_SUFFIX = ".js";
    public static final String VERSIONS = "versions";

    public BiblesOrgRequest(String url, Response.Listener listener){
        super(Request.Method.GET, url, null, listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("BH VerseDL", error.getMessage());
            }
        });
    }

    public static String url(String request){
        return BASE_URL + request + URL_SUFFIX;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> params = new HashMap();
        String creds = String.format("%s:%s",APIKey.getABSKey(),"X");
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
        params.put("Authorization", auth);
        return params;
    }

}
