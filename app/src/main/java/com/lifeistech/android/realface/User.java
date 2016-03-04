package com.lifeistech.android.realface;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by kokushiseiya on 16/03/05.
 */public class User {
    private int score;
    private String name;
    private Context context;

    public User() {

    }

    public User(String name, int score, Context context) {
        this.name = name;
        this.score = score;
        this.context = context;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public Context getContext() {
        return context;
    }

}
