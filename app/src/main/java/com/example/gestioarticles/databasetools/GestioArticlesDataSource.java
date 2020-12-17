package com.example.gestioarticles.databasetools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class GestioArticlesDataSource {

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


    // Mecanismes per treballar amb SQLite
    private GestioArticlesHelper dbHelper;
    private SQLiteDatabase dbW, dbR;


    /* .: 2. CONSTRUCTOR I DESTRUCTOR :. */
    public GestioArticlesDataSource(Context context) {
        // Crea una comunicació amb la BBDD
        dbHelper = new GestioArticlesHelper(context);

        // S'instancien els dos Database per poder llegir i escriure en la taula
        dbW = dbHelper.getWritableDatabase();
        dbR = dbHelper.getReadableDatabase();
    }

    @Override
    protected void finalize() throws Throwable {
        // En finalitzar tanca els dos objectes Database
        dbW.close();
        dbR.close();
        super.finalize();
    }


    /* .: 3. SELECTS - MÈTODES QUE RETORNEN LLISTATS AMB DADES :. */


    /* .: 4. UPDATES/INSERTS/DELETES - MÈTODES QUE PERMETEN MANIPULAR LES DADES :. */
}
