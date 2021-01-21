package com.example.gestioarticles.databasetools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gestioarticles.assets.datetype.Date;

public class GestioArticlesDataSource {

    /* .: 1. DEFINICIÓ DE LES DADES DE LA BBDD :. */
    // Nom de les taules en la BBDD
    public static final String TABLE_ARTICLE = GestioArticlesHelper.TABLE_ARTICLE;
    public static final String TABLE_MOVIMENT = GestioArticlesHelper.TABLE_MOVIMENT;

    // Nom dels camps que conformen la taula "ARTICLE"
    public static final String ARTICLE_ID = GestioArticlesHelper.ARTICLE_ID;
    public static final String ARTICLE_CODI = GestioArticlesHelper.ARTICLE_CODI;
    public static final String ARTICLE_DESCRIPCIO = GestioArticlesHelper.ARTICLE_DESCRIPCIO;
    public static final String ARTICLE_FAMILIA = GestioArticlesHelper.ARTICLE_FAMILIA;
    public static final String ARTICLE_PREU = GestioArticlesHelper.ARTICLE_PREU;
    public static final String ARTICLE_ESTOC = GestioArticlesHelper.ARTICLE_ESTOC;

    // Nom dels camps que conformen la taula "MOVIMENT"
    public static final String MOVIMENT_ID = GestioArticlesHelper.MOVIMENT_ID;
    public static final String MOVIMENT_CODI_ARTICLE = GestioArticlesHelper.MOVIMENT_CODI_ARTICLE;
    public static final String MOVIMENT_DIA = GestioArticlesHelper.MOVIMENT_DIA;
    public static final String MOVIMENT_QUANTITAT = GestioArticlesHelper.MOVIMENT_QUANTITAT;
    public static final String MOVIMENT_TIPUS = GestioArticlesHelper.MOVIMENT_TIPUS;


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


    /* .: 3. MÉTODES TAULA ARTICLE :. */
    /* .: 3.1. SELECTS - MÈTODES QUE RETORNEN LLISTATS AMB DADES :. */
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

    /** Retorna una select amb l'article que s'està buscant
     * @param codi Codi de l'article que es vol buscar */
    public Cursor getArticle(String codi) {

        return dbR.query(TABLE_ARTICLE,
                new String[]{ARTICLE_ID, ARTICLE_CODI, ARTICLE_DESCRIPCIO, ARTICLE_FAMILIA, ARTICLE_PREU, ARTICLE_ESTOC},
                ARTICLE_CODI + " = '" + codi + "'",
                null,
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

    /* .: 3.1.1. SELECTS - REALITZEN CONSULTES I RETORNEN BOOLEANS :. */
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

    /* .: 3.2. UPDATES/INSERTS/DELETES - MÈTODES QUE PERMETEN MANIPULAR LES DADES :. */
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


    /* .: 4. MÈTODES SOBRE LA TAULA MOVIMENT :. */
    /* .: 4.1. SELECTS - MÈTODES QUE RETORNEN LLISTATS AMB DADES :. */
    /** Retorna un cursor amb tots els elements. Ordenats segons el criteri.
     * @param orderBy String amb la clàusula SQL ORDER BY */
    public Cursor getMoviments(String orderBy) {
        return dbR.query(
                TABLE_MOVIMENT,
                new String[]{MOVIMENT_ID, MOVIMENT_CODI_ARTICLE, MOVIMENT_DIA, MOVIMENT_QUANTITAT, MOVIMENT_TIPUS},
                null,
                null,
                null,
                null,
                orderBy
        );
    }

    /** Retorna un cursor amb tots els elements filtrats pel codi de l'article
     * i ordenats segons el criteri.
     * @param articleCode String amb el codi de l'article pel que es vol filtrar
     * @param orderBy String amb la clàusula SQL ORDER BY */
    public Cursor getMovimentsByArticleCode(String articleCode, String orderBy) {

        final String QUERY =
                "SELECT * " +
                        "FROM " + TABLE_MOVIMENT + " a " +
                        "INNER JOIN " + TABLE_ARTICLE + " b " +
                        "ON a." + MOVIMENT_CODI_ARTICLE + " = b." + ARTICLE_ID +
                        " WHERE b." + ARTICLE_CODI + " = " + articleCode +
                        " ORDER BY " + orderBy;

        return dbR.rawQuery(QUERY, null);
    }

    /** Retorna un cursor amb tots els elements filtrats per l'ID de l'article
     * i ordenats segons el criteri.
     * @param idArticle Long amb l'ID de l'article pel que es vol filtrar
     * @param orderBy String amb la clàusula SQL ORDER BY */
    public Cursor getMovimentsByArticleID(long idArticle, String orderBy) {


        return dbR.query(
                TABLE_MOVIMENT,
                new String[]{MOVIMENT_ID, MOVIMENT_CODI_ARTICLE, MOVIMENT_DIA, MOVIMENT_QUANTITAT, MOVIMENT_TIPUS},
                MOVIMENT_CODI_ARTICLE + " = ?",
                new String[]{String.valueOf(idArticle)},
                null,
                null,
                orderBy
        );
    }

    /* .: 4.2. UPDATES/INSERTS/DELETES - MÈTODES QUE PERMETEN MANIPULAR LES DADES :. */
    /** Inserta un nou moviment, actualitzant també la quantiat d'estoc que hi ha de l'article */
    public boolean insertMovement(long idArticle, Date data, int quantitat, String tipus) {

        long id = -1;
        int afectedRows = -1;
        boolean insertCorrecte = false;

        ContentValues values = new ContentValues();
        values.put(MOVIMENT_CODI_ARTICLE, idArticle);
        values.put(MOVIMENT_DIA, data.getSQLDate());
        values.put(MOVIMENT_QUANTITAT, quantitat);
        values.put(MOVIMENT_TIPUS, tipus.toUpperCase());

        // Fa l'insert a la taula MOVIMENT i retorna un id. -1 si hi ha algún error
        try {
            id = dbW.insert(
                    TABLE_MOVIMENT,
                    null,
                    values);
        }
        catch (Exception e) {
            id = -1;
        }

        // Si tot ha anat correctament, i hi ha un ID vàlid, continua amb l'actualització de la taula
        if (id > -1) {

            // Recupera l'article afectat pel moviment, i aconsegueix el seu estoc
            Cursor articleAfectat = this.getArticle(idArticle);

            if (articleAfectat.moveToFirst()) {
                long estocArticle = articleAfectat.getLong(articleAfectat.getColumnIndexOrThrow(ARTICLE_ESTOC));
                articleAfectat.close();

                // Si el moviment es d'entrada suma la quantiat, si es de sortida en resta
                if (tipus.toUpperCase().charAt(0) == 'S') {
                    estocArticle = estocArticle - quantitat;
                }
                else if (tipus.toUpperCase().charAt(0) == 'E') {
                    estocArticle = estocArticle + quantitat;
                }

                // Recupera l'estoc i el prepara per insertar
                values = new ContentValues();
                values.put(ARTICLE_ESTOC, estocArticle);

                // Fa l'update
                afectedRows = dbW.update(
                        TABLE_ARTICLE,
                        values,
                        ARTICLE_ID + " = ?",
                        new String[]{String.valueOf(idArticle)}
                );

                // Si l'insert s'ha realitzat correctament, retornarà un true
                if (afectedRows > 0) insertCorrecte = true;
            }
            else {
                articleAfectat.close();
            }

        }

        return insertCorrecte;
    }
}
