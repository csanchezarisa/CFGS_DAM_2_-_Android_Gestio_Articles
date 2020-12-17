package com.example.gestioarticles.databasetools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GestioArticlesHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "GestioArticles_DataBase";


    public GestioArticlesHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // String amb el codi SQL per crear la taula "ARTICLE" amb els camps corresponents
        String CREATE_TABLE_ARTICLES =
                "CREATE TABLE article (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "codiarticle TEXT NOT NULL UNIQUE," +
                        "descripcio TEXT NOT NULL," +
                        "familia TEXT," +
                        "preu REAL NOT NULL," +
                        "estoc REAL DEFAULT 0);";

        // S'executa el codi
        db.execSQL(CREATE_TABLE_ARTICLES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
