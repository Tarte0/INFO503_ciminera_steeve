package com.example.cimin.info503_ciminera_steeve.model;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by cimin on 20/10/2017.
 */

public class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

    public RetrieveFeedTask(){
        doInBackground();
    }

    protected void onPreExecute() {
        //avant l'exec
        //afficher ce que l'on essaie de faire ?
    }

    protected String doInBackground(Void... urls) {
        String API_URL = "193.48.123.188/json/contenu.json";
        // valider le bazar

        try {
            URL url = new URL(API_URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                return stringBuilder.toString();
            }
            finally{
                urlConnection.disconnect();
            }
        }
        catch(Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return null;
        }
    }

    protected void onPostExecute(String response) {
        if(response == null) {
            response = "THERE WAS AN ERROR";
        }
        Log.i("INFO", response);
        JsonHandler.parseJsonData(response);
    }
}
