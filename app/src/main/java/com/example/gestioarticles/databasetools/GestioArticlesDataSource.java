package com.example.gestioarticles.databasetools;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GestioArticlesDataSource {

    /* .: 1. DEFINICIÓ DE LES DADES DE LA BBDD :. */
    // Nom de les taules en la BBDD
    public static final String TABLE_ARTICLE = GestioArticlesHelper.TABLE_ARTICLE;

    // Nom dels camps que conformen la taula "ARTICLE"
    public static final String ARTICLE_ID = GestioArticlesHelper.ARTICLE_ID;
    public static final String ARTICLE_CODI = GestioArticlesHelper.ARTICLE_CODI;
    public static final String ARTICLE_DESCRIPCIO = GestioArticlesHelper.ARTICLE_DESCRIPCIO;
    public static final String ARTICLE_FAMILIA = GestioArticlesHelper.ARTICLE_FAMILIA;
    public static final String ARTICLE_PREU = GestioArticlesHelper.ARTICLE_PREU;
    public static final String ARTICLE_ESTOC = GestioArticlesHelper.ARTICLE_ESTOC;


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
    /** Retorna una select amb tots els articles */
    public Cursor articles_all() {

        return dbR.query(TABLE_ARTICLE,
                new String[]{ARTICLE_ID, ARTICLE_CODI, ARTICLE_DESCRIPCIO, ARTICLE_FAMILIA, ARTICLE_PREU, ARTICLE_ESTOC},
                null,
                null,
                null,
                null,
                null);
    }

    /** Retorna una select filtrada amb els articles que contenen un string determinat
     * @param description Paraula o conjunt d'elles que s'usaran en la consulta per filtrar-la*/
    public Cursor articles_description(String description) {

        String QUERY = "SELECT * " +
                "FROM " + TABLE_ARTICLE +
                " WHERE ? LIKE '%?%'";

        return dbR.rawQuery(QUERY, new String[]{ARTICLE_DESCRIPCIO, description});
    }

    /** Retorna una select filtrada amb els articles amb estoc inferior al número passat
     * @param stock Número per filtrar els articles amb estoc inferior a ell */
    public Cursor articles_stock_lower(int stock) {

        String QUERY = "SELECT * " +
                "FROM " + TABLE_ARTICLE +
                " WHERE ? <= ?;";

        return dbR.rawQuery(QUERY, new String[]{ARTICLE_ESTOC, String.valueOf(stock)});
    }

    /** Retorna una select filtrada amb els articles amb estoc inferior al número passat i amb la paraula dins de la descripció
     * @param description Paraula o conjunt d'elles que s'usaran en la consulta per filtrar-la
     * @param stock Número per filtrar els articles amb estoc inferior a ell */
    public Cursor articles_description_stock_lower(String description, int stock) {

        String QUERY = "SELECT * " +
                "FROM " + TABLE_ARTICLE +
                " WHERE ? <= ? AND ? LIKE '%?%'";

        return dbR.rawQuery(QUERY, new String[]{ARTICLE_ESTOC, String.valueOf(stock), ARTICLE_DESCRIPCIO, description});
    }

    /* .: 4. UPDATES/INSERTS/DELETES - MÈTODES QUE PERMETEN MANIPULAR LES DADES :. */
}
