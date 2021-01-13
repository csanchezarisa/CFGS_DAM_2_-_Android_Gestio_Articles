package com.example.gestioarticles.databasetools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GestioArticlesHelper extends SQLiteOpenHelper {

    /* .: 1. DEFINICIÓ DE LES DADES DE LA BBDD :. */
    // Nom de les taules en la BBDD
    public static final String TABLE_ARTICLE = "article";

    // Nom dels camps que conformen la taula "ARTICLE"
    public static final String ARTICLE_ID = "_id";
    public static final String ARTICLE_CODI = "codiarticle";
    public static final String ARTICLE_DESCRIPCIO = "descripcio";
    public static final String ARTICLE_FAMILIA = "familia";
    public static final String ARTICLE_PREU = "preu";
    public static final String ARTICLE_ESTOC = "estoc";

    // Dades sobre la BBDD
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "GestioArticles_DataBase";


    public GestioArticlesHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // String amb el codi SQL per crear la taula "ARTICLE" amb els camps corresponents
        String CREATE_TABLE_ARTICLES =
                "CREATE TABLE "+ TABLE_ARTICLE + "(" +
                        ARTICLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        ARTICLE_CODI + " TEXT NOT NULL UNIQUE," +
                        ARTICLE_DESCRIPCIO + " TEXT NOT NULL," +
                        ARTICLE_FAMILIA + " INTEGER NOT NULL DEFAULT 0," +      // 0 -> None, 1 -> Software, 2 -> Hardware, 3 -> Other
                        ARTICLE_PREU + " REAL NOT NULL," +
                        ARTICLE_ESTOC + " REAL NOT NULL DEFAULT 0);";

        // S'executa el codi
        db.execSQL(CREATE_TABLE_ARTICLES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion > 2) {
            String SQL_QUERY =
                    "DROP TABLE " + TABLE_ARTICLE;

            db.execSQL(SQL_QUERY);

            SQL_QUERY =
                    "CREATE TABLE "+ TABLE_ARTICLE + "(" +
                            ARTICLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                            ARTICLE_CODI + " TEXT NOT NULL UNIQUE," +
                            ARTICLE_DESCRIPCIO + " TEXT NOT NULL," +
                            ARTICLE_FAMILIA + " INTEGER NOT NULL DEFAULT 0," +
                            ARTICLE_PREU + " REAL NOT NULL," +
                            ARTICLE_ESTOC + " REAL NOT NULL DEFAULT 0);";

            db.execSQL(SQL_QUERY);
        }

    }
}
