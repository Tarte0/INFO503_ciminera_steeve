package com.example.cimin.info503_ciminera_steeve.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.cimin.info503_ciminera_steeve.R;
import com.example.cimin.info503_ciminera_steeve.model.DatabaseHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class UpdateData extends AppCompatActivity {
    Button btnMaj, btnClean;
    TextView txtJson;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_data);

        btnMaj = (Button) findViewById(R.id.updateb);

        btnMaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new JsonTask(v.getContext()).execute("http://www.remporte-le-club.com/stev/jsons/update.json");
            }
        });

        btnClean = (Button) findViewById(R.id.cleanb);
        btnClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper myDb = new DatabaseHelper(v.getContext());
                myDb.clearTable(DatabaseHelper.CATEGORIES_TABLE); //nettoie la table 0, pour les tests
                myDb.clearTable(DatabaseHelper.CONTENT_TABLE); //nettoie la table 1, pour les tests
                myDb.clearTable(DatabaseHelper.FAVORITES_TABLE); //nettoie la table 2, pour les tests
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }

        });


    }

    public class JsonTask extends AsyncTask<String, String, String> {
        private Context mContext;

        public JsonTask (Context context){
            mContext = context;
        }

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(UpdateData.this);
            pd.setMessage("Mise à jour de la base de donnée en cours...");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream, "UTF8"));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    buffer.append("\n");
                    Log.d("Reponse: ", "> " + line);

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()){
                pd.dismiss();
            }
            if(result != null){
                ArrayList<JSONObject> categoriesJson = new ArrayList<>();
                ArrayList<JSONObject> contentList = new ArrayList<>();
                DatabaseHelper myDb = new DatabaseHelper(mContext);
                try {
                    JSONObject resultJson = new JSONObject(result); //on parse le resultat
                    //on récupères nos catégories et nos contenus dans une liste
                    for(int i=0; i<resultJson.getJSONArray("categories").length(); i++){
                        categoriesJson.add(resultJson.getJSONArray("categories").getJSONObject(i));
                    }
                    for(int i=0; i<resultJson.getJSONArray("content").length(); i++){
                        contentList.add(resultJson.getJSONArray("content").getJSONObject(i));
                    }

                    //on passe la liste dans notre base de données
                    for(int i = 0; i< categoriesJson.size(); i++){
                        try {
                            myDb.insertDataTable0(categoriesJson.get(i).getString("name"));
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    //on passe la liste des contenus dans notre base de données
                    String getCatID="";
                    int catID=0;
                    Cursor cur;
                    for(int i = 0; i< contentList.size(); i++){
                        try {
                            //on recupere l'id de la categorie
                            getCatID = "SELECT * FROM "+ DatabaseHelper.CATEGORIES_TABLE + " WHERE NAME = '" + contentList.get(i).getString("category") +"'";
                            cur = myDb.executeQuery(getCatID);
                            if(cur.moveToFirst()){
                                catID = cur.getInt(cur.getColumnIndex("ID"));
                            }

                            myDb.insertDataTable1(catID,
                                    contentList.get(i).getString("title"),
                                    contentList.get(i).getString("longText"),
                                    contentList.get(i).getString("icon"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        }
    }
}
