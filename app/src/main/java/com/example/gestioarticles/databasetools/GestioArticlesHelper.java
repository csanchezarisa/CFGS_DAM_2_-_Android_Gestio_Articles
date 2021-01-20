package com.example.gestioarticles.databasetools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GestioArticlesHelper extends SQLiteOpenHelper {

    /* .: 1. DEFINICIÓ DE LES DADES DE LA BBDD :. */
    // Nom de les taules en la BBDD
    public static final String TABLE_ARTICLE = "article";
    public static final String TABLE_MOVIMENT = "moviment";

    // Nom dels camps que conformen la taula "ARTICLE"
    public static final String ARTICLE_ID = "_id";
    public static final String ARTICLE_CODI = "codiarticle";
    public static final String ARTICLE_DESCRIPCIO = "descripcio";
    public static final String ARTICLE_FAMILIA = "familia";
    public static final String ARTICLE_PREU = "preu";
    public static final String ARTICLE_ESTOC = "estoc";

    // Nom dels camps que conformen la taula "MOVIMENT"
    public static final String MOVIMENT_ID = "_id";
    public static final String MOVIMENT_CODI_ARTICLE = "codiarticle";
    public static final String MOVIMENT_DIA = "data";
    public static final String MOVIMENT_QUANTITAT = "quantitat";
    public static final String MOVIMENT_TIPUS = "tipus";

    // Dades sobre la BBDD
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "GestioArticles_DataBase";


    public GestioArticlesHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // String amb el codi SQL per crear la taula "ARTICLE" amb els camps corresponents
        String SQL_CODE =
                "CREATE TABLE " + TABLE_ARTICLE + "(" +
                        ARTICLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        ARTICLE_CODI + " TEXT NOT NULL UNIQUE," +
                        ARTICLE_DESCRIPCIO + " TEXT NOT NULL," +
                        ARTICLE_FAMILIA + " INTEGER NOT NULL DEFAULT 0," +      // 0 -> None, 1 -> Software, 2 -> Hardware, 3 -> Other
                        ARTICLE_PREU + " REAL NOT NULL," +
                        ARTICLE_ESTOC + " REAL NOT NULL DEFAULT 0);";

        // S'executa el codi
        db.execSQL(SQL_CODE);

        // String amb el codi SQL per crear la taula "MOVIMENT" amb els camps corresponent i
        // la foreign key que apunta a la taula "ARTICLE"
        SQL_CODE =
                "CREATE TABLE " + TABLE_MOVIMENT + "(" +
                        MOVIMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        MOVIMENT_CODI_ARTICLE + " INTEGER NOT NULL," +
                        MOVIMENT_DIA + " TEXT NOT NULL," +
                        MOVIMENT_QUANTITAT + " INTEGER NOT NULL," +
                        MOVIMENT_TIPUS + " TEXT NOT NULL, " +
                        "FOREIGN KEY (" + MOVIMENT_CODI_ARTICLE + ") REFERENCES " + TABLE_ARTICLE + "(" + ARTICLE_ID + ")" +
                            "ON DELETE CASCADE " +
                            "ON UPDATE CASCADE);";

        // S'executa el codi
        db.execSQL(SQL_CODE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 2) {
            // Canvia el camp familia de tipus TEXT a INTEGER
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

        if (oldVersion < 3) {
            // Crea la nova taula de MOVIMENT amb la foreign key
            String SQL_QUERY =
                    "CREATE TABLE " + TABLE_MOVIMENT + "(" +
                            MOVIMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                            MOVIMENT_CODI_ARTICLE + " INTEGER NOT NULL," +
                            MOVIMENT_DIA + " TEXT NOT NULL," +
                            MOVIMENT_QUANTITAT + " INTEGER NOT NULL," +
                            MOVIMENT_TIPUS + " TEXT NOT NULL, " +
                            "FOREIGN KEY (" + MOVIMENT_CODI_ARTICLE + ") REFERENCES " + TABLE_ARTICLE + "(" + ARTICLE_ID + ")" +
                            "ON DELETE CASCADE " +
                            "ON UPDATE CASCADE);";

            db.execSQL(SQL_QUERY);
        }

    }
}
