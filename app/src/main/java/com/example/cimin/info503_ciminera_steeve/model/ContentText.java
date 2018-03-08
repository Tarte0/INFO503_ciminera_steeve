package com.example.cimin.info503_ciminera_steeve.model;

/**
 * Created by cimin on 19/10/2017.
 */

public class ContentText {
    private int id;
    private int category;
    private String icon;
    private String title;
    private String longText;

    public ContentText(int id, int category, String title, String longText, String icon){
        this.id = id;
        this.category = category;
        this.icon = icon;
        this.title = title;
        this.longText = longText;
    }

    public int getCategory() {
        return category;
    }

    public String getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public String getLongText() {
        return longText;
    }

    public int getId() {
        return id;
    }
}
