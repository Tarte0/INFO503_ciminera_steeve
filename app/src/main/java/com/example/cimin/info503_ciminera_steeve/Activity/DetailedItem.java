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

public class DetailedItem extends AppCompatActivity {
    private ArrayList<JSONObject> drawerItemList;
    private DrawerLayout myDrawerLayout;
    DatabaseHelper myDb;
    private ListView drawerListView;
    private LinearLayout myDrawerLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_item);

        TextView titleTv = (TextView) findViewById(R.id.titleTv);
        TextView longTextTv = (TextView) findViewById(R.id.longTextTv);

        Intent it = getIntent();
        if(it != null){
            Bundle extras = it.getExtras();
            if(extras != null){
                titleTv.setText(extras.getString("title"));
                longTextTv.setText(extras.getString("text"));
            }
        }

        myDb = new DatabaseHelper(this);
        JsonHandler jsonhandler = new JsonHandler(this);
        myDrawerLinearLayout = (LinearLayout) findViewById(R.id.drawerLinear);
        myDrawerLayout = (DrawerLayout) findViewById(R.id.myDrawerLayout);
        drawerItemList = new ArrayList<>();
        drawerListView = (ListView) findViewById(R.id.drawerLv);

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

        //on rempli le drawer à l'aide de nos categories (BDD)
        Cursor cur;
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

        drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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

        JsonDrawerAdapter jda = new JsonDrawerAdapter(this, drawerItemList);
        drawerListView.setAdapter(jda);
    }
}
