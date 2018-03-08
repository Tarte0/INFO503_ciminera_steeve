package com.example.cimin.info503_ciminera_steeve.model;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by cimin on 18/10/2017.
 */

public class JsonHandler {
    private static Context context;

    public JsonHandler(Context context){
        this.context = context;
    }

    public static JSONObject retrieveJSON(int id) throws IOException, JSONException {
        InputStream is = context.getResources().openRawResource(id);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            is.close();
        }

        return new JSONObject(writer.toString());
    }

    public static int GetJSONid(String name){
        return context.getResources().getIdentifier("test", "raw", context.getPackageName());
    }

    public static void parseJsonData(String jsonResponse){
        try
        {
            JSONArray jsonArray = new JSONArray(jsonResponse);

            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                //String UserRole = jsonObject1.optString("blabla");
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}
