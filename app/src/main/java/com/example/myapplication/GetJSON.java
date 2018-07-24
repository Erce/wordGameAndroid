package com.example.myapplication;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Erce on 7/12/2018.
 */

public class GetJSON extends AsyncTask<String, Void, ArrayList> {
    private Exception exception;

    protected ArrayList doInBackground(String... urls) {
        String parsedString = "";
        InputStream is = null;
        try {
            URL url = new URL(urls[0]);
            URLConnection conn = url.openConnection();

            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            is = httpConn.getInputStream();
            parsedString = convertinputStreamToString(is);
            JSONObject json = new JSONObject(parsedString);
            JSONArray nameArray = json.names();
            JSONArray valArray = json.toJSONArray(nameArray);

            JSONArray valArray1 = valArray.getJSONArray(0);

            valArray1.toString().replace("[", "");
            valArray1.toString().replace("]", "");

            int len = valArray1.length();
            ArrayList<HashMap<String,String>> arrList = new ArrayList<HashMap<String,String>>();

            for (int i=0; i<len; i++) {
                HashMap<String, String> map = new HashMap<String, String>();
                JSONObject e = valArray1.getJSONObject(i);
                map.put("id", e.getString("id"));
                map.put("German", e.getString("German"));
                map.put("English", e.getString("English"));
                map.put("Turkish", e.getString("Turkish"));

                arrList.add(map);
            }
            is.close();
            return arrList;
        } catch (Exception e) {
            this.exception = e;

            return null;
        }
    }

    protected void onPostExecute(ArrayList feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }

    protected static String convertinputStreamToString(InputStream ists)
            throws IOException {
        if (ists != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader r1 = new BufferedReader(new InputStreamReader(
                        ists, "UTF-8"));
                while ((line = r1.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                ists.close();
            }
            return sb.toString();
        } else {
            return "";
        }
    }
}
