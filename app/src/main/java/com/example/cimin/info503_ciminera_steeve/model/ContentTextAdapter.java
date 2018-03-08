package com.example.cimin.info503_ciminera_steeve.model;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cimin.info503_ciminera_steeve.R;


import java.util.ArrayList;

/**
 * Created by cimin on 19/10/2017.
 */

public class ContentTextAdapter extends BaseAdapter {
    private ArrayList<ContentText> ct = new ArrayList<>();
    private static LayoutInflater inflater = null;
    private Activity activity;
    private DatabaseHelper myDb;

    public ContentTextAdapter(Activity a, ArrayList<ContentText> contentList) {
        this.ct = contentList;
        activity = a;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return ct.size();
    }

    @Override
    public Object getItem(int position) {
        return ct.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi=convertView;

        if(convertView==null) {
            vi = inflater.inflate(R.layout.list_row, null);
        }
        ImageView icon=(ImageView)vi.findViewById(R.id.drawerIv);
        TextView text=(TextView)vi.findViewById(R.id.drawerTv);

        ImageButton favB = (ImageButton) vi.findViewById(R.id.favButton);
        //on change l'image de favoris selon si l'item l'est ou non


        //on regarde si l'item est favori ou non
        myDb = new DatabaseHelper(activity);
        Cursor cur = myDb.executeQuery("SELECT CASE WHEN EXISTS(SELECT 1 FROM " + DatabaseHelper.FAVORITES_TABLE +
                " WHERE "+DatabaseHelper.CONT_FAV +"='" + ct.get(position).getId() + "') THEN 1 ELSE 0 END AS ContentIdExists");

        int exist = 0;
        while (cur.moveToNext()) {
            exist = cur.getInt(0);
        }
        if(exist == 1) { //si il y est
            favB.setImageResource(R.drawable.yellow_star);
        }else{//si il n'y est pas
            favB.setImageResource(R.drawable.hollow_star);
        }

        favB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ImageButton favB = (ImageButton) v.findViewById(R.id.favButton);

                //on regarde si l'item est favori ou non

                Cursor cur = myDb.executeQuery("SELECT CASE WHEN EXISTS(SELECT 1 FROM " + DatabaseHelper.FAVORITES_TABLE + " WHERE "+DatabaseHelper.CONT_FAV +"='" + ct.get(position).getId() + "') THEN 1 ELSE 0 END AS ContentIdExists");
                int exist = 0;
                while (cur.moveToNext()) {
                    exist = cur.getInt(0);
                }
                if(exist == 1) { //si il y est on l'enleve
                    myDb.getWritableDatabase().delete(DatabaseHelper.FAVORITES_TABLE, DatabaseHelper.CONT_FAV + " = ?", new String[] {Integer.toString(ct.get(position).getId())});
                    favB.setImageResource(R.drawable.hollow_star);
                }else{//si il ne l'est pas on l'ajoute
                    myDb.insertDataTable2(ct.get(position).getId());
                    favB.setImageResource(R.drawable.yellow_star);
                }
            }
        });

        text.setText(ct.get(position).getTitle());
        int id = activity.getResources().getIdentifier(ct.get(position).getIcon(), "drawable", activity.getPackageName());
        icon.setImageResource(id);

        return vi;
    }
}
