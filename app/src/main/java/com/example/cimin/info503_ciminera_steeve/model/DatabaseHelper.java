package com.example.cimin.info503_ciminera_steeve.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by cimin on 19/10/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "info503.db";
    public static final String CATEGORIES_TABLE = "categories_table";
    public static final String ID_CAT = "ID";
    public static final String NAME_CAT = "NAME";
    public static final String CONTENT_TABLE = "content_table";
    public static final String ID_CONT = "ID";
    public static final String CAT_CONT = "CATEGORY";
    public static final String TITLE_CONT = "TITLE";
    public static final String TEXT_CONT = "TEXTCONTENT";
    public static final String ICON_CONT = "ICON";
    public static final String FAVORITES_TABLE = "favorites_table";
    public static final String ID_FAV = "ID";
    public static final String CONT_FAV = "CONTENTID";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + CATEGORIES_TABLE +
                "("+ ID_CAT +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ NAME_CAT +" TEXT)");
        db.execSQL("create table " + CONTENT_TABLE +
                "("+ ID_CONT +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ CAT_CONT +" INTEGER, "+ TITLE_CONT +" TEXT, "+ TEXT_CONT +" TEXT, "+
                ICON_CONT +" TEXT, FOREIGN KEY("+ CAT_CONT +") REFERENCES "+ CATEGORIES_TABLE +"(ID))");
        db.execSQL("CREATE TABLE " + FAVORITES_TABLE +
                "("+ ID_FAV +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ CONT_FAV +" INTEGER, FOREIGN KEY("+ CONT_FAV +") REFERENCES "+ CONTENT_TABLE +"(ID))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CATEGORIES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CONTENT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + FAVORITES_TABLE);
        onCreate(db);
    }

    public boolean insertDataTable0(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        //on ne veut pas 2 fois la meme categorie
        Cursor cur = db.rawQuery("SELECT CASE WHEN EXISTS(SELECT 1 FROM " + CATEGORIES_TABLE + " WHERE NAME='" + name + "') THEN 1 ELSE 0 END AS NameExists", null);
        int exist = 0;
        while (cur.moveToNext()) {
            exist = cur.getInt(0);
        }
        if(exist != 1){//si la categorie n'existe pas (nom)
            ContentValues contentValues = new ContentValues();
            contentValues.put(NAME_CAT, name);
            long res = db.insert(CATEGORIES_TABLE, null, contentValues);
            return res == -1;
        }
        return false;
    }

    public boolean insertDataTable1(int category, String title, String textContent, String icon){
        SQLiteDatabase db = this.getWritableDatabase();
        //on ne veut pas 2 fois la meme contenu (titre)
        Cursor cur = db.rawQuery("SELECT CASE WHEN EXISTS(SELECT 1 FROM " + CONTENT_TABLE + " WHERE TITLE='" + title + "') THEN 1 ELSE 0 END AS TitleExists", null);
        int exist = 0;
        while (cur.moveToNext()) {
            exist = cur.getInt(0);
        }
        if(exist != 1){
            ContentValues contentValues = new ContentValues();
            contentValues.put(CAT_CONT, category);
            contentValues.put(TITLE_CONT, title);
            contentValues.put(TEXT_CONT, textContent);
            contentValues.put(ICON_CONT, icon);
            long res = db.insert(CONTENT_TABLE, null, contentValues);
            return res == -1;
        }
        return false;
    }

    public boolean insertDataTable2(int contentId){
        SQLiteDatabase db = this.getWritableDatabase();
        //on ne veut pas 2 fois le meme favori (id)
        Cursor cur = db.rawQuery("SELECT CASE WHEN EXISTS(SELECT 1 FROM " + FAVORITES_TABLE + " WHERE CONTENTID='" + contentId + "') THEN 1 ELSE 0 END AS ContentIdExists", null);
        int exist = 0;
        while (cur.moveToNext()) {
            exist = cur.getInt(0);
        }
        if(exist != 1){
            ContentValues contentValues = new ContentValues();
            contentValues.put(CONT_FAV, contentId);
            long res = db.insert(FAVORITES_TABLE, null, contentValues);
            return res == -1;
        }
        return false;
    }

    public Cursor getAllData(String query){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery(query, null);
        return cur;
    }

    public Cursor executeQuery(String query){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery(query, null);
        return cur;
    }

    public Cursor getAllDataFromTable(String table){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM "+ table, null);
        return cur;
    }

    public boolean isEmpty(String table){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + table, null);
        return cur.getCount() == 0;
    }

    public void clean(){
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);
    }

    public void clearTable(String table) {
        SQLiteDatabase db = this.getWritableDatabase();
        String clearDBQuery = "DELETE FROM "+table;
        db.execSQL(clearDBQuery);
    }
}
