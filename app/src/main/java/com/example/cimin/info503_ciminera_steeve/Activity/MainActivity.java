package com.example.cimin.info503_ciminera_steeve.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cimin.info503_ciminera_steeve.R;
import com.example.cimin.info503_ciminera_steeve.model.DatabaseHelper;
import com.example.cimin.info503_ciminera_steeve.model.JsonDrawerAdapter;
import com.example.cimin.info503_ciminera_steeve.model.JsonHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<JSONObject> drawerItemList;
    private DrawerLayout myDrawerLayout;
    LinearLayout myDrawerLinearLayout;
    ListView mListView;

    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        myDb = new DatabaseHelper(this);

        drawerItemList = new ArrayList<>();
        ArrayList<JSONObject> contentList = new ArrayList<>();
        ArrayList<JSONObject> categoriesJson = new ArrayList<>();
        JsonHandler jsonhandler = new JsonHandler(this);
        mListView = (ListView) findViewById(R.id.drawerLv);
        myDrawerLayout = (DrawerLayout) findViewById(R.id.myDrawerLayout);
        myDrawerLinearLayout = (LinearLayout) findViewById(R.id.drawerLinear);
        Toolbar mainToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);


        //menu hamburgeur
        ImageButton hamburger = (ImageButton) findViewById(R.id.threeDots);
        hamburger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myDrawerLayout.isDrawerOpen(Gravity.LEFT)){
                    myDrawerLayout.closeDrawer(Gravity.LEFT, true);
                }else{ //si le menu est fermé, on l'ouvre
                    myDrawerLayout.openDrawer(Gravity.LEFT, true);
                    myDrawerLinearLayout.bringToFront(); //On gere la priorité d'apparition (axe Z)
                    myDrawerLayout.requestLayout();
                }
            }
        });

        //lorsque l'on clique sur le drawer
        myDrawerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myDrawerLayout.isDrawerOpen(Gravity.LEFT)){
                    myDrawerLayout.closeDrawer(Gravity.LEFT, true);
                }else{ //si le menu est fermé, on l'ouvre
                    myDrawerLayout.openDrawer(Gravity.LEFT, true);
                    myDrawerLinearLayout.bringToFront(); //On gere la priorité d'apparition (axe Z)
                    myDrawerLayout.requestLayout();
                }
            }
        });


        //on rempli le drawer à l'aide d'un json
        try {
            JSONObject drawerItemJson = JsonHandler.retrieveJSON(R.raw.drawer_items);
            for(int i=0; i<drawerItemJson.getJSONArray("items").length(); i++){
                drawerItemList.add(drawerItemJson.getJSONArray("items").getJSONObject(i));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView parent, View view,
                                    int position, long id) {

                Intent i = new Intent(getApplicationContext(), List.class);
                String queryFromMain = "";
                switch(position){
                    case 0: //home
                        i = new Intent(getApplicationContext(), MainActivity.class);
                        break;
                    case 1: //tout voir
                        queryFromMain ="SELECT * FROM " + DatabaseHelper.CONTENT_TABLE;
                        i.putExtra("queryFromMain", queryFromMain);
                        break;
                    case 2: //favoris
                        queryFromMain = "SELECT * FROM " + DatabaseHelper.CONTENT_TABLE +" INNER JOIN "+DatabaseHelper.FAVORITES_TABLE +" ON "+
                                DatabaseHelper.CONTENT_TABLE +"."+DatabaseHelper.ID_CONT +"="+DatabaseHelper.FAVORITES_TABLE +"."+DatabaseHelper.CONT_FAV;
                        i.putExtra("queryFromMain", queryFromMain);
                        break;
                    case 3: //Mise a jour
                        i = new Intent(getApplicationContext(), UpdateData.class);
                        break;
                    default: //categories
                        TextView nameTv = (TextView) view.findViewById(R.id.drawerTv);
                        String name = (String)nameTv.getText();
                        name = name.toLowerCase().replace(' ', '_');
                        String subQuery = "SELECT ID FROM " + DatabaseHelper.CATEGORIES_TABLE +" WHERE NAME ='"+name+"'"; //nom de la categorie
                        queryFromMain = "SELECT * FROM " + DatabaseHelper.CONTENT_TABLE +" WHERE CATEGORY=("+subQuery+")";
                        i.putExtra("queryFromMain", queryFromMain);
                        break;
                }
                startActivity(i);
            }
        });

        //on récupères nos catégories et nos contenus dans une liste
        try {
            JSONObject contentJson = JsonHandler.retrieveJSON(R.raw.content);
            for(int i=0; i<contentJson.getJSONArray("categories").length(); i++){
                categoriesJson.add(contentJson.getJSONArray("categories").getJSONObject(i));
            }
            for(int i=0; i<contentJson.getJSONArray("content").length(); i++){
                contentList.add(contentJson.getJSONArray("content").getJSONObject(i));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        //on passe la liste dans notre base de données
        for(int i = 0; i< categoriesJson.size(); i++){
            try {
                myDb.insertDataTable0(categoriesJson.get(i).getString("name"));
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //permet d'aller a l'activité 'Liste' depuis les bouton de la main activity
        View.OnClickListener goToListWthParams = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(getApplicationContext(),List.class);
                i.putExtra("queryFromMain", (String)v.getTag());
                startActivity(i);
            }
        };

        //on ajoute notre contenu derriere le drawer
        //on crée nos boutons grace a notre base de données
        LinearLayout leftButtons = (LinearLayout) findViewById(R.id.leftLayoutMain); //layout pour boutons d'indices pairs
        LinearLayout rightButtons = (LinearLayout) findViewById(R.id.rightLayoutMain);   //layout pour boutons d'indices impairs
        leftButtons.setWeightSum(0);
        rightButtons.setWeightSum(0);
        LinearLayout tmpLayout;
        Button tmpbtn = new Button(this);
        Cursor cur = myDb.getAllDataFromTable(DatabaseHelper.CATEGORIES_TABLE);
        int k=0;
        while (cur.moveToNext())
        {
            tmpLayout = leftButtons; //indices pairs
            if(k%2 != 0){  //indices impairs
                tmpLayout = rightButtons;
            }
            tmpLayout.setWeightSum(tmpLayout.getWeightSum()+1); //on rajoute un bouton donc on réduit la taille des autres
            String name = cur.getString(cur.getColumnIndex("NAME"));
            int imgID = getResources().getIdentifier(name, "drawable", getPackageName());
            tmpbtn.setTag("SELECT * FROM "+DatabaseHelper.CONTENT_TABLE + " WHERE CATEGORY =" + cur.getString(cur.getColumnIndex("ID")));
            //Ce tag permet d'attribuer une requete a un bouton
            tmpbtn.setText(name.replace('_', ' '));
            tmpbtn.setTextColor(Color.parseColor("#4b4b4b"));
            tmpbtn.setBackgroundResource(R.drawable.button_bg);
            tmpbtn.setCompoundDrawablesWithIntrinsicBounds(0, imgID, 0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
            params.weight = 1;
            tmpbtn.setLayoutParams(params);
            tmpbtn.setHeight(0);
            tmpbtn.setOnClickListener(goToListWthParams);
            tmpLayout.addView(tmpbtn);
            tmpbtn = new Button(this);
            k++;
        }

        //on passe la liste des contenus dans notre base de données
        String getCatID="";
        int catID=0;
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


        //on rempli le drawer à l'aide de nos categories (BDD)
        String query = "SELECT * FROM "+ DatabaseHelper.CATEGORIES_TABLE;
        cur = myDb.executeQuery(query);
        while(cur.moveToNext()){
            JSONObject tmpObject = new JSONObject();
            try {
                tmpObject.put("name", cur.getString(cur.getColumnIndex("NAME")));
                tmpObject.put("icon", cur.getString(cur.getColumnIndex("NAME")));
                drawerItemList.add(tmpObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JsonDrawerAdapter jda = new JsonDrawerAdapter(this, drawerItemList);
        mListView.setAdapter(jda);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
}