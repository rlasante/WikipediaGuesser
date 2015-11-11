package com.lasante.ryan.wikipediaguesser;

import android.content.BroadcastReceiver;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import de.greenrobot.event.EventBus;

/**
 * Created by ryanlasante on 11/6/15.
 */


public class DownloadJSONTask extends AsyncTask<String, Void, JSONObject> {
    private static int READ_TIMEOUT = 10 * 1000;
    private static int CONNECTION_TIMEOUT = 15 * 1000;

    @Override
    protected JSONObject doInBackground(String... urls) {
        String urlData = downloadLoadURL(urls[0]);
        try {
            JSONObject jsonData = new JSONObject(urlData);
            return jsonData;
        } catch (JSONException ex) {
            Log.e("DownloadJSONTask", "Data downloaded isn't a json object", ex);
            return new JSONObject();
        }
    }

    protected static String downloadLoadURL(String urlString) {
        try {
            InputStream is = null;
            URL url = new URL(urlString);
            HttpURLConnection urlConnection =  (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);

            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();

            Log.d("DownloadJSONTask", "Response code is: " + responseCode);

            is = urlConnection.getInputStream();

            Log.d("DownloadJSONTask", "Content Type: " + urlConnection.getContentType());
            Log.d("DownloadJSONTask", "Content: " + urlConnection.getContent());
            String content = convertStreamToString(is);
            return content != null ? content : "";
        } catch (Exception ex) {
            Log.e("DownloadJSONTask", "Failed to download from url", ex);
            return "";
        }
    }

    protected static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        EventBus.getDefault().post(jsonObject);
    }
}
