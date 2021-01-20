package com.example.gestioarticles.databasetools;

import android.content.ContentValues;
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
    private final GestioArticlesHelper dbHelper;
    private final SQLiteDatabase dbW, dbR;


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
    /** Retorna una select amb l'article que s'està buscant
     * @param id ID de l'article que es vol buscar */
    public Cursor getArticle(long id) {

        return dbR.query(TABLE_ARTICLE,
                new String[]{ARTICLE_ID, ARTICLE_CODI, ARTICLE_DESCRIPCIO, ARTICLE_FAMILIA, ARTICLE_PREU, ARTICLE_ESTOC},
                ARTICLE_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null);
    }

    /** Retorna una select amb tots els articles */
    public Cursor getArticlesAll() {

        return dbR.query(TABLE_ARTICLE,
                new String[]{ARTICLE_ID, ARTICLE_CODI, ARTICLE_DESCRIPCIO, ARTICLE_FAMILIA, ARTICLE_PREU, ARTICLE_ESTOC},
                null,
                null,
                null,
                null,
                null);
    }

    /** Retorna una select amb tots els articles
     * Ordenats pel paràmetre passat
     * @param sort Columna per la que es vol filtrar */
    public Cursor getArticlesAll(String sort) {

        return dbR.query(TABLE_ARTICLE,
                new String[]{ARTICLE_ID, ARTICLE_CODI, ARTICLE_DESCRIPCIO, ARTICLE_FAMILIA, ARTICLE_PREU, ARTICLE_ESTOC},
                null,
                null,
                null,
                null,
                sort);
    }

    /** Retorna una select filtrada amb els articles que contenen un string determinat
     * @param description Paraula o conjunt d'elles que s'usaran en la consulta per filtrar-la*/
    public Cursor getArticlesByDescription(String description) {

        return dbR.query(TABLE_ARTICLE,
                new String[]{ARTICLE_ID, ARTICLE_CODI, ARTICLE_DESCRIPCIO, ARTICLE_FAMILIA, ARTICLE_PREU, ARTICLE_ESTOC},
                ARTICLE_DESCRIPCIO + " LIKE \"%" + description + "%\"",
                null,
                null,
                null,
                null);
    }

    /** Retorna una select filtrada amb els articles que contenen un string determinat.
     * Ordenats pel paràmetre passat
     * @param description Paraula o conjunt d'elles que s'usaran en la consulta per filtrar-la
     * @param sort Columna per la que es vol filtrar*/
    public Cursor getArticlesByDescription(String description, String sort) {

        return dbR.query(TABLE_ARTICLE,
                new String[]{ARTICLE_ID, ARTICLE_CODI, ARTICLE_DESCRIPCIO, ARTICLE_FAMILIA, ARTICLE_PREU, ARTICLE_ESTOC},
                ARTICLE_DESCRIPCIO + " LIKE \"%" + description + "%\"",
                null,
                null,
                null,
                sort);
    }

    /** Retorna una select filtrada amb els articles amb estoc inferior al número passat
     * @param stock Número per filtrar els articles amb estoc inferior a ell */
    public Cursor getArticlesStockLower(int stock) {

        return dbR.query(TABLE_ARTICLE,
                new String[]{ARTICLE_ID, ARTICLE_CODI, ARTICLE_DESCRIPCIO, ARTICLE_FAMILIA, ARTICLE_PREU, ARTICLE_ESTOC},
                ARTICLE_ESTOC + " <= " + stock,
                null,
                null,
                null,
                null);
    }

    /** Retorna una select filtrada amb els articles amb estoc inferior al número passat.
     * Ordenats pel paràmetre passat
     * @param stock Número per filtrar els articles amb estoc inferior a ell
     * @param sort Columna per la que es vol filtrar la select*/
    public Cursor getArticlesStockLower(int stock, String sort) {

        return dbR.query(TABLE_ARTICLE,
                new String[]{ARTICLE_ID, ARTICLE_CODI, ARTICLE_DESCRIPCIO, ARTICLE_FAMILIA, ARTICLE_PREU, ARTICLE_ESTOC},
                ARTICLE_ESTOC + " <= " + stock,
                null,
                null,
                null,
                sort);
    }

    /** Retorna una select filtrada amb els articles amb estoc inferior al número passat i amb la paraula dins de la descripció
     * @param description Paraula o conjunt d'elles que s'usaran en la consulta per filtrar-la
     * @param stock Número per filtrar els articles amb estoc inferior a ell */
    public Cursor getArticlesByDescriptionStockLower(String description, int stock) {

        return dbR.query(TABLE_ARTICLE,
                new String[]{ARTICLE_ID, ARTICLE_CODI, ARTICLE_DESCRIPCIO, ARTICLE_FAMILIA, ARTICLE_PREU, ARTICLE_ESTOC},
                ARTICLE_DESCRIPCIO + " LIKE \"%" + description + "%\" AND " +
                        ARTICLE_ESTOC + " <= " + stock,
                null,
                null,
                null,
                null);
    }

    /** Retorna una select filtrada amb els articles amb estoc inferior al número passat i amb la paraula dins de la descripció.
     * Ordenats pel paràmetre passat
     * @param description Paraula o conjunt d'elles que s'usaran en la consulta per filtrar-la
     * @param stock Número per filtrar els articles amb estoc inferior a ell
     * @param sort Columna per la que es vol filtrar la select */
    public Cursor getArticlesByDescriptionStockLower(String description, int stock, String sort) {

        return dbR.query(TABLE_ARTICLE,
                new String[]{ARTICLE_ID, ARTICLE_CODI, ARTICLE_DESCRIPCIO, ARTICLE_FAMILIA, ARTICLE_PREU, ARTICLE_ESTOC},
                ARTICLE_DESCRIPCIO + " LIKE \"%" + description + "%\" AND " +
                        ARTICLE_ESTOC + " <= " + stock,
                null,
                null,
                null,
                sort);
    }

    /* .: 3.1. SELECTS - REALITZEN CONSULTES I RETORNEN BOOLEANS :. */
    /** Fa una consulta filtrant pel codi de l'article.
     * Retorna un boolean segons si existeix algun
     * article amb el mateix codi o no
     * @param code Codi que es vol cercar en la BBDD*/
    public boolean checkCode(String code) {
        boolean exists = false;

        Cursor select = dbR.query(
                TABLE_ARTICLE,
                new String[]{ARTICLE_ID},
                ARTICLE_CODI + " = '%" + code + "%'",
                null,
                null,
                null,
                null
        );

        // Es comprova el resultat de la select. Si es diferent a 0, el boolean en posarà en true
        if (select.getCount() != 0) {
            exists = true;
        }

        select.close();

        return exists;
    }

    /* .: 4. UPDATES/INSERTS/DELETES - MÈTODES QUE PERMETEN MANIPULAR LES DADES :. */
    /** Permet fer la inserció d'un nou article en la BBDD */
    public long insertArticle(String code, String description, int family, double price) {

        long id = -1;

        ContentValues values = new ContentValues();
        values.put(ARTICLE_CODI, code);
        values.put(ARTICLE_DESCRIPCIO, description);
        values.put(ARTICLE_FAMILIA, family);
        values.put(ARTICLE_PREU, price);

        try {
            id = dbW.insert(TABLE_ARTICLE,
                    null,
                    values);
        }
        catch (Exception e) {
            id = -1;
        }

        return id;
    }

    /** Permet actualitzar les dades d'un registre. Fa servir el ID per filtrar-lo en el WHERE */
    public int updateArticle(long id, String description, int family, double price) {

        int afectedRows = -1;

        ContentValues values = new ContentValues();
        values.put(ARTICLE_DESCRIPCIO, description);
        values.put(ARTICLE_FAMILIA, family);
        values.put(ARTICLE_PREU, price);

        try {
            afectedRows = dbW.update(TABLE_ARTICLE,
                    values,
                    ARTICLE_ID + " = ?",
                    new String[]{String.valueOf(id)});
        }
        catch (Exception e) {
            afectedRows = -1;
        }

        return afectedRows;

    }

    /** Permet eliminar un registre de la BBDD, segons el ID
     * @param id ID del registre en la taula, per poder eliminar-lo */
    public int deleteArticle(long id) {

        int afectedRows = -1;

        try {
            afectedRows = dbW.delete(TABLE_ARTICLE,
                    ARTICLE_ID + " = ?",
                    new String[]{String.valueOf(id)});
        }
        catch (Exception e) {
            afectedRows = -1;
        }

        return afectedRows;
    }
}
