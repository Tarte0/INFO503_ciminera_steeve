package com.example.cimin.info503_ciminera_steeve.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.cimin.info503_ciminera_steeve.R;
import com.example.cimin.info503_ciminera_steeve.model.ContentText;
import com.example.cimin.info503_ciminera_steeve.model.ContentTextAdapter;
import com.example.cimin.info503_ciminera_steeve.model.DatabaseHelper;
import com.example.cimin.info503_ciminera_steeve.model.JsonDrawerAdapter;
import com.example.cimin.info503_ciminera_steeve.model.JsonHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class Favorites extends AppCompatActivity {
    private ArrayList<JSONObject> drawerItemList;
    private DrawerLayout myDrawerLayout;
    DatabaseHelper myDb;
    String bTag;
    private ArrayList<ContentText> contentList = new ArrayList<>();
    private ListView myListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        myListView = (ListView) findViewById(R.id.lvListAct);

        Intent it = getIntent();
        if(it != null){
            Bundle extras = it.getExtras();
            if(extras != null){
                bTag = extras.getString("buttonTag");
            }
        }

        myDrawerLayout = (DrawerLayout) findViewById(R.id.myDrawerLayout);
        drawerItemList = new ArrayList<>();
        ListView mListView = (ListView) findViewById(R.id.drawerLv);
        //on rempli le drawer à l'aide d'un json
        try {
            JSONObject drawerItemJson = JsonHandler.retrieveJSON(R.raw.drawer_items);
            for(int i=0; i<drawerItemJson.getJSONArray("items").length(); i++){
                drawerItemList.add(drawerItemJson.getJSONArray("items").getJSONObject(i));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        JsonDrawerAdapter jda = new JsonDrawerAdapter(this, drawerItemList);
        mListView.setAdapter(jda);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView parent, View view,
                                    int position, long id) {
                Intent i = new Intent(getApplicationContext(),List.class);
                //i.putExtra("DB", myDb);
                i.putExtra("LIST", drawerItemList);
                startActivity(i);
            }
        });

        //menu hamburger
        ImageButton hamburger = (ImageButton) findViewById(R.id.threeDots);
        hamburger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myDrawerLayout.isDrawerOpen(Gravity.LEFT)){
                    myDrawerLayout.closeDrawer(Gravity.LEFT, true);
                }else{ //si le menu est fermé, on l'ouvre
                    myDrawerLayout.openDrawer(Gravity.LEFT, true);
                }
            }
        });


        //on recupère les éléments joints entre les favoris et le contenu
        myDb = new DatabaseHelper(this);
        String query = "SELECT * FROM " + DatabaseHelper.CONTENT_TABLE +" INNER JOIN "+DatabaseHelper.FAVORITES_TABLE +" ON "+
                DatabaseHelper.CONTENT_TABLE +"."+DatabaseHelper.ID_CONT +"="+DatabaseHelper.FAVORITES_TABLE +"."+DatabaseHelper.CONT_FAV;
        Cursor cur = myDb.executeQuery(query);
        ContentText tmpContent;
        while (cur.moveToNext())
        {
            tmpContent = new ContentText(cur.getInt(cur.getColumnIndex(DatabaseHelper.ID_CONT)),
                    cur.getInt(cur.getColumnIndex(DatabaseHelper.CAT_CONT)),
                    cur.getString(cur.getColumnIndex(DatabaseHelper.TITLE_CONT)),
                    cur.getString(cur.getColumnIndex(DatabaseHelper.TEXT_CONT)),
                    cur.getString(cur.getColumnIndex(DatabaseHelper.ICON_CONT)));
            contentList.add(tmpContent);

        }

        ContentTextAdapter cta = new ContentTextAdapter(this, contentList);
        myListView.setAdapter(cta);

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView parent, View view,
                                    int position, long id) {
                Intent i = new Intent(getApplicationContext(),DetailedItem.class);
                i.putExtra("title", contentList.get(position).getTitle());
                i.putExtra("text", contentList.get(position).getLongText());
                startActivity(i);
            }
        });
    }
}
