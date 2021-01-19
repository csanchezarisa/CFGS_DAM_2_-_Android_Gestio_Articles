package com.example.gestioarticles;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gestioarticles.adapter.ArticlesAdapter;
import com.example.gestioarticles.articlemanage.ArticleManage;
import com.example.gestioarticles.articlemanage.StockActivity;
import com.example.gestioarticles.databasetools.GestioArticlesDataSource;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    /* .: 1. VARIABLES GLOBALS PER TENIR MEMÒRIA :. */
    // 'ID' que s'assignen a les activities i fan el codi més entenedor
    public static final int ACTIVITY_ADD_ARTICLE = 1;
    public static final int ACTIVITY_UPDATE_ARTICLE = 2;
    public static final int ACTIVITY_STOCK_IN = 3;
    public static final int ACTIVITY_STOCK_OUT = 4;

    // Complements per gestionar la BBDD i modificar la llista
    private GestioArticlesDataSource bbdd;
    private ArticlesAdapter adaptadorArticles;

    // Columnes i camps de la BBDD
    private static final String[] from = new String[]{GestioArticlesDataSource.ARTICLE_CODI, GestioArticlesDataSource.ARTICLE_DESCRIPCIO, GestioArticlesDataSource.ARTICLE_ESTOC, GestioArticlesDataSource.ARTICLE_PREU};
    private static final int[] to = new int[]{R.id.txt_codi_article, R.id.txt_descripcio_article, R.id.txt_article_estoc, R.id.txt_article_preu_no_iva};

    // Variabes que permeten accedir a la traducció dels botons acceptar i cancel·lar
    public static String alertBtnAccept = "";
    public static String alertBtnCancel = "";

    // Variables que emmagatzemen el filtre actual
    private boolean filterDescription = false;
    private String description;
    private boolean filterStock = false;

    /** Variable que emmagatzemen l'ordre per mostrar els articles.
     * Per defecte, s'ordenarà pel camp ID */
    private String sortType = GestioArticlesDataSource.ARTICLE_CODI;
    private int sortPosition = 0;


    // Elements del Layout
    ListView llistatArticles;
    ImageView imgSadFace;
    TextView txtEmptyArticle;
    ImageView imgArrow;

    /* .: 2. CREACIÓ DE L'ACTIVITY :. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enllaçar variables amb els elements de layout
        llistatArticles = (ListView) findViewById(R.id.article_list);
        imgSadFace = (ImageView) findViewById(R.id.img_sad_face);
        txtEmptyArticle = (TextView) findViewById(R.id.txt_empty_article);
        imgArrow = (ImageView) findViewById(R.id.img_arrow);
        // Botó per afegir articles
        FloatingActionButton brn_add = findViewById(R.id.btn_add);
        brn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Es crida a l'activity de gestió d'articles, passant un id negatiu per obligar a crear-ne un article nou
                gestionarArticle(-1);
            }
        });

        alertBtnAccept = getString(R.string.alert_info_accept);
        alertBtnCancel = getString(R.string.alert_info_cancel);

        // S'instancia el DataSource per poder treballar amb les dades de la BBDD
        bbdd = new GestioArticlesDataSource(this);

        // Es carreguen els artícles en la llista
        carregarArticles();

        // Es crea un listener pels elements del llistat
        llistatArticles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gestionarArticle(id);
            }
        });

        llistatArticles.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mostrarAlertaEliminar(getString(R.string.alert_info_title_delete_article), getString(R.string.alert_info_delete_article), id);
                return true;
            }
        });
    }

    /* .: 3. MENÚ PERSONALITZAT :. */
    /** Menú personalitzat amb filtres */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu, menu);

        return true;
    }

    /** Listeners pels botons del menú personalitzat */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_btn_clear:
                eliminarFiltres();
                return true;
            case R.id.menu_btn_filter:
                mostrarAlertFiltrar();
                return true;
            case R.id.menu_btn_order:
                mostrarAlertOrdre();
                return true;
            case R.id.menu_btn_stock_in:
                gestionarStock(-1, ACTIVITY_STOCK_IN);
                return true;
            case R.id.menu_btn_stock_out:
                gestionarStock(-1, ACTIVITY_STOCK_OUT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /* .: 4. COMUNICACIÓ AMB ALTRES ACTIVITIES :. */
    /** S'encarrega de fer la crida a l'Activity que permet gestionar els article.
     * Modificar-los i afegir-ne de nous
     * @param id Rep l'id de l'article. Negatiu en cas de voler crear-ne un */
    private void gestionarArticle(long id) {

        // Es prepara un bundle per passar el id a la nova activity
        Bundle data = new Bundle();
        data.putLong("id", id);

        // Es prepara un intent que obrirà l'activity per gestionar articles
        // Passant-li un bundle am el id de l'article
        Intent intent = new Intent(this, ArticleManage.class);
        intent.putExtras(data);

        // Per assignar un ID a l'activity per poder gestionar correctament la resposta
        // Es revisa si l'id és negatiu (S'ha de crear un nou article)
        // O si l'id és positiu (S'ha de modificar un article)
        int idTipusActivity;
        if (id < 0) {
            idTipusActivity = ACTIVITY_ADD_ARTICLE;
        }
        else {
            idTipusActivity = ACTIVITY_UPDATE_ARTICLE;
        }

        // Es fa la crida a l'intent i se li assigna un ID segons què ha de fer
        startActivityForResult(intent, idTipusActivity);
    }

    /** S'encarrega d'obrir l'activity que permet notificar l'entrada i la sortida
     * d'estoc d'un article en concret
     * @param id Long que permet saber quin és l'article a modificar. -1 si no es selecciona cap
     * @param tipusActivity Int que permet saber quin tipus de crida es farà en la nova activity.
     * 3 per notificar l'entrada d'estoc i 4 per notificar-ne la sortida*/
    public void gestionarStock(long id, int tipusActivity) {

        // Es prepara un bundle per passar el ID i el tipus de crida que es fa a l'Activity
        Bundle data = new Bundle();
        data.putLong("id", id);
        data.putInt("stockType", tipusActivity);

        // Es genera l'intent i s'afageixen les dades
        Intent intent = new Intent(this, StockActivity.class);
        intent.putExtras(data);

        // S'inicia l'Activity a l'espera d'un resultat
        startActivityForResult(intent, tipusActivity);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Revisa que l'activity que es tanca sigui l'encarregada de modificar o afegir articles
        // o l'encarregada de gestionar els stocks
        // I s'hagi tancat amb un ResultCode OK
        if ((requestCode == ACTIVITY_ADD_ARTICLE ||
                requestCode == ACTIVITY_UPDATE_ARTICLE ||
                requestCode == ACTIVITY_STOCK_IN ||
                requestCode == ACTIVITY_STOCK_OUT) && resultCode == RESULT_OK) {

            // Si desde l'activity que es tanca es passa algún StockType, vol dir que s'ha de fer la crida
            // A l'activity per modificar els stocks
            // Sino, refresca la llista
            try {
                int stockType = data.getExtras().getInt("stockType");
                if (stockType != 0) {
                    long id = data.getExtras().getLong("id");

                    gestionarStock(id, stockType);
                }
                else {
                    refrescarArticles();
                }
            }
            catch (Exception e) {
                refrescarArticles();
            }

        }

    }

    /* .: 4. FUNCIONS PRÒPIES :. */
    /** Carrega les dades de tots els articles */
    private void carregarArticles() {

        Cursor articles = bbdd.getArticlesAll();

        adaptadorArticles = new ArticlesAdapter(this, R.layout.activity_main_fila, articles, from, to, 1);

        llistatArticles.setAdapter(adaptadorArticles);

        mostrarMissatgeEmptyArticle();

    }

    /** Refresca les dades i les mostra en el llistat segons el filtre que
     * s'estigui utilitzant actualment*/
    private void refrescarArticles() {

        Cursor articles;

        if (filterDescription && filterStock) {
            articles = bbdd.getArticlesByDescriptionStockLower(description, 0, sortType);
        }
        else if (filterDescription) {
            articles = bbdd.getArticlesByDescription(description, sortType);
        }
        else if (filterStock) {
            articles = bbdd.getArticlesStockLower(0, sortType);
        }
        else {
            articles = bbdd.getArticlesAll(sortType);
        }

        adaptadorArticles.changeCursor(articles);
        adaptadorArticles.notifyDataSetChanged();

        llistatArticles.setSelection(0);

        mostrarMissatgeEmptyArticle();
    }

    /** Segons la posició del filtre seleccionat, es canvia la variable amb la
     * sentència SQL per poder filtrar per aquell camp */
    private void seleccionarOrdre() {

        switch (sortPosition) {
            case 1:
                sortType = GestioArticlesDataSource.ARTICLE_ID + " desc";
                break;

            case 2:
                sortType = GestioArticlesDataSource.ARTICLE_CODI;
                break;

            case 3:
                sortType = GestioArticlesDataSource.ARTICLE_CODI + " desc";
                break;

            case 4:
                sortType = GestioArticlesDataSource.ARTICLE_PREU;
                break;

            case 5:
                sortType = GestioArticlesDataSource.ARTICLE_PREU + " desc";
                break;

            default:
                sortType = GestioArticlesDataSource.ARTICLE_ID;
        }

        refrescarArticles();

    }

    /** Reestableix tots els filtres i els ordres modificats, per deixar-ho per
     * defecte */
    private void eliminarFiltres() {

        filterDescription = false;
        description = "";
        filterStock = false;
        sortPosition = 0;
        seleccionarOrdre();

    }

    /** Permet mostrar o amagar el missatge que notifica que no hi ha cap
     * article creat encara. */
    private void mostrarMissatgeEmptyArticle() {

        if (llistatArticles.getCount() <= 0) {

            imgSadFace.setVisibility(View.VISIBLE);
            txtEmptyArticle.setVisibility(View.VISIBLE);
            imgArrow.setVisibility(View.VISIBLE);

        }
        else {

            imgSadFace.setVisibility(View.GONE);
            txtEmptyArticle.setVisibility(View.GONE);
            imgArrow.setVisibility(View.GONE);

        }

    }


    /* .: 5. ALERTES :. */
    /** Mostra un AlertDialog per confirmar l'eliminació de l'element seleccionat.
     * Es personalitza amb el títol i el contingut que rep per paràmetres
     * @param titol String amb el títol que tindrà l'alert
     * @param contingut String amb el missatge que tindrà l'alert
     * @param idArticle Long que fa referència al ID de l'article per esborrar*/
    public void mostrarAlertaEliminar(String titol, String contingut, long idArticle) {
        AlertDialog alert = new AlertDialog.Builder(this).create();

        alert.setTitle(titol);
        alert.setMessage(contingut);

        alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.alert_info_accept), new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int articlesEliminats = bbdd.deleteArticle(idArticle);

                if (articlesEliminats > 0) {
                    mostrarSnackBarCorrecte(getString(R.string.alert_success_article_deleted));
                }
                else {
                    mostrarSnackBarError(getString(R.string.alert_error_cant_delete_article));
                }

                refrescarArticles();
            }
        });

        alert.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.alert_info_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // No fa res
            }
        });

        alert.show();
    }

    /** Mostra un AlertDialog per confirmar l'eliminació de l'element seleccionat */
    public void mostrarAlertaEliminar(long idArticle) {
        AlertDialog alert = new AlertDialog.Builder(this).create();

        alert.setTitle(getString(R.string.alert_info_title_delete_article));
        alert.setMessage(getString(R.string.alert_info_delete_article));

        alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.alert_info_accept), new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int articlesEliminats = bbdd.deleteArticle(idArticle);

                if (articlesEliminats > 0) {
                    mostrarSnackBarCorrecte(getString(R.string.alert_success_article_deleted));
                }
                else {
                    mostrarSnackBarError(getString(R.string.alert_error_cant_delete_article));
                }

                refrescarArticles();
            }
        });

        alert.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.alert_info_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // No fa res
            }
        });

        alert.show();
    }

    /** Mostra un AlertDialog que permet seleccionar els filtres pels quals poder
     * filtrar els elements que es mostren en el llistat.
     * Depenent què s'esculli, s'obrirà un altre AlertDialog demanant més informació o no.
     * Mostra, també, un botó per restablir els filtres*/
    private void mostrarAlertFiltrar() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(getString(R.string.alert_info_title_filter));

        String[] filtres = new String[] {getString(R.string.activity_main_txt_article_description), getString(R.string.activity_main_txt_article_stock)};
        boolean[] filtresSeleccionats = new boolean[] {filterDescription, filterStock};

        alert.setMultiChoiceItems(filtres, filtresSeleccionats, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                filtresSeleccionats[which] = isChecked;
            }
        });

        alert.setPositiveButton(R.string.alert_info_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                filterDescription = filtresSeleccionats[0];
                filterStock = filtresSeleccionats[1];

                if (filterDescription) {

                    mostrarAlertDemanarDescripcio();

                }
                else {
                    refrescarArticles();
                }

            }
        });

        alert.setNegativeButton(R.string.alert_info_cancel, null);

        alert.setNeutralButton(R.string.alert_info_reset, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminarFiltres();
            }
        });

        alert.show();
    }

    /** Mostra un alert que permet seleccionar amb quin tipus de filtre es
     * volen mostrar els artícles en el llistat */
    private void mostrarAlertOrdre() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.alert_info_title_order));

        String[] sorts = new String[]{
                getString(R.string.alert_info_order_by_date),
                getString(R.string.alert_info_order_by_date_desc),
                getString(R.string.alert_info_order_by_code),
                getString(R.string.alert_info_order_by_code_desc),
                getString(R.string.alert_info_order_by_price),
                getString(R.string.alert_info_order_by_price_desc)
        };

        alert.setSingleChoiceItems(sorts, sortPosition, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sortPosition = which;
            }
        });

        alert.setPositiveButton(R.string.alert_info_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                seleccionarOrdre();
            }
        });

        alert.setNegativeButton(R.string.alert_info_cancel, null);

        alert.setNeutralButton(R.string.alert_info_reset, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminarFiltres();
            }
        });

        alert.show();

    }

    /** Mostra un AlertDialog que permet introduir les paraules per filtrar
     * els articles per la descripció */
    private void mostrarAlertDemanarDescripcio() {

        if (filterDescription) {

            AlertDialog alert = new AlertDialog.Builder(this).create();
            alert.setTitle(getString(R.string.alert_info_title_select_description));

            EditText edtDescription = new EditText(this);
            alert.setView(edtDescription);

            alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.alert_info_accept), new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    try {
                        description = edtDescription.getText().toString().toLowerCase();
                    }
                    catch (Exception e) {
                        description = "";
                        mostrarSnackBarError(getString(R.string.alert_error_cant_filter_articles));
                    }

                    if (description.length() <= 0) {
                        filterDescription = false;
                    }

                    refrescarArticles();

                }
            });

            alert.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.alert_info_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // No fa res
                }
            });

            alert.show();

        }

    }

    /** Mostra un Snackbar de color vermell en la part superior de la pantalla
     * notificant d'un error
     * @param error String amb el contingut del missatge que s'ha de mostrar*/
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void mostrarSnackBarError(String error) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(parentLayout, Html.fromHtml("<font color=\"#FFFFFF\">" + error + "</font>"), Snackbar.LENGTH_LONG);

        View snackbarView = snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
        params.gravity = Gravity.TOP;
        snackbarView.setLayoutParams(params);
        snackbarView.setBackgroundColor(getColor(R.color.design_default_color_error));

        snackbar.show();
    }

    /** Mostra un Snackbar de color verd en la part superior de la pantalla
     * avisant que tot ha funcionat correctament
     * @param missatge String amb el contingut del missatge que s'ha de mostrar*/
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void mostrarSnackBarCorrecte(String missatge) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(parentLayout, Html.fromHtml("<font color=\"#FFFFFF\">" + missatge + "</font>"), Snackbar.LENGTH_LONG);

        View snackbarView = snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
        params.gravity = Gravity.TOP;
        snackbarView.setLayoutParams(params);
        snackbarView.setBackgroundColor(getColor(android.R.color.holo_green_dark));

        snackbar.show();
    }

}