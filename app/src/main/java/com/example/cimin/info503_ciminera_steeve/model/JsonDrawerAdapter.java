package com.example.cimin.info503_ciminera_steeve.model;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cimin.info503_ciminera_steeve.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by cimin on 18/10/2017.
 */

public class JsonDrawerAdapter extends BaseAdapter {
    ArrayList<JSONObject> jsonList = new ArrayList<>();
    private static LayoutInflater inflater = null;
    private Activity activity;

    public JsonDrawerAdapter(Activity a, ArrayList<JSONObject> jsonList) {
        this.jsonList = jsonList;
        activity = a;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return jsonList.size();
    }

    @Override
    public Object getItem(int position) {
        return jsonList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null) {
            vi = inflater.inflate(R.layout.drawer_row, null);
        }
        ImageView icon=(ImageView)vi.findViewById(R.id.drawerIv);
        TextView text=(TextView)vi.findViewById(R.id.drawerTv);
        try {
            //on triche pour creer un separateur (une ligne)
            //on transforme un element qui s'appelle "divider" en une ligne grise
            if(jsonList.get(position).getString("name").equals("divider")){
                text.setText("");
                text.setTextSize(0.1f);
                vi.setBackgroundColor(Color.parseColor("#C0C0C0"));
                icon.setImageDrawable(null);
                vi.setOnClickListener(null);
                vi.setOnLongClickListener(null);
                vi.setLongClickable(false);
            }else{
                String cleanedTMP = jsonList.get(position).getString("name").replace('_', ' ');
                String cleaned = cleanedTMP.substring(0, 1).toUpperCase() + cleanedTMP.substring(1);
                text.setText(cleaned);
                int id = activity.getResources().getIdentifier(jsonList.get(position).getString("icon"), "drawable", activity.getPackageName());
                icon.setImageResource(id);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return vi;
    }
}
