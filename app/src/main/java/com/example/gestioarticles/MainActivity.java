package com.example.gestioarticles;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
import android.widget.FrameLayout;
import android.widget.ListView;

import com.example.gestioarticles.adapter.ArticlesAdapter;
import com.example.gestioarticles.articlemanage.ArticleManage;
import com.example.gestioarticles.databasetools.GestioArticlesDataSource;
import com.example.gestioarticles.enumerator.FilterEnum;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    /* .: 1. VARIABLES GLOBALS PER TENIR MEMÒRIA :. */
    // 'ID' que s'assignen a les activities i fan el codi més entenedor
    private static int ACTIVITY_ADD_ARTICLE = 1;
    private static int ACTIVITY_UPDATE_ARTICLE = 2;

    // Complements per gestionar la BBDD i modificar la llista
    private GestioArticlesDataSource bbdd;
    private ArticlesAdapter adaptadorArticles;

    // Columnes i camps de la BBDD
    private static String[] from = new String[]{GestioArticlesDataSource.ARTICLE_CODI, GestioArticlesDataSource.ARTICLE_DESCRIPCIO, GestioArticlesDataSource.ARTICLE_ESTOC, GestioArticlesDataSource.ARTICLE_PREU};
    private static int[] to = new int[]{R.id.txt_codi_article, R.id.txt_descripcio_article, R.id.txt_article_estoc, R.id.txt_article_preu_no_iva};

    // Variabes que permeten accedir a la traducció dels botons acceptar i cancel·lar
    public static String alertBtnAccept = "";
    public static String alertBtnCancel = "";

    // Enumerador per saber el tipus de filtre, y String que emmagatzema la descripció filtrada
    private FilterEnum filtreActual;
    private String description;

    // Elements del Layout
    ListView llistatArticles;

    /* .: 2. CREACIÓ DE L'ACTIVITY :. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enllaçar variables amb els elements de layout
        llistatArticles = (ListView) findViewById(R.id.article_list);
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
                carregarArticles();
                return true;
            case R.id.menu_btn_filter:
                return true;
            case R.id.menu_btn_order:
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Revisa que l'activity que es tanca sigui l'encarregada de modificar o afegir articles
        // I s'hagi tancat amb un ResultCode OK
        if ((requestCode == ACTIVITY_ADD_ARTICLE || requestCode == ACTIVITY_UPDATE_ARTICLE) && resultCode == RESULT_OK) {
            refrescarArticles();
        }

    }

    /* .: 4. FUNCIONS PRÒPIES :. */
    /** Carrega les dades de tots els articles */
    private void carregarArticles() {

        Cursor articles = bbdd.getArticlesAll();

        filtreActual = FilterEnum.FILTER_ALL;

        adaptadorArticles = new ArticlesAdapter(this, R.layout.activity_main_fila, articles, from, to, 1);

        llistatArticles.setAdapter(adaptadorArticles);
    }

    /** Refresca les dades i les mostra en el llistat segons el filtre que
     * s'estigui utilitzant actualment*/
    private void refrescarArticles() {

        Cursor articles = null;

        // Es revisa quin tipus de filtre està actiu. Per fer una select o una altre
        switch (filtreActual) {
            // S'han de mostrar tots els articles
            case FILTER_ALL:
                articles = bbdd.getArticlesAll();
                break;

            // S'han de mostrar els articles filtrats per una descripció
            case FILTER_DESCRIPTION:
                break;

            // S'han de mostrar els articles sense estoc
            case FILTER_STOCK:
                break;

            // S'han de mostrar els articles filtrats per descripció i sense estoc
            case FILTER_DESCRIPTION_AND_STOCK:
                break;
        }

        adaptadorArticles.changeCursor(articles);
        adaptadorArticles.notifyDataSetChanged();

        llistatArticles.setSelection(0);
    }

    /* .: 5. ALERTES :. */
    /** Mostra un AlertDialog per confirmar l'eliminació de l'element seleccionat */
    private void mostrarAlertaEliminar(String titol, String contingut, long idArticle) {
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

    /** Mostra un Snackbar de color vermell en la part superior de la pantalla
     * notificant d'un error
     * @param error String amb el contingut del missatge que s'ha de mostrar*/
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void mostrarSnackBarError(String error) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(parentLayout, Html.fromHtml("<font color=\"#000000\">" + error + "</font>"), Snackbar.LENGTH_LONG);

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
        Snackbar snackbar = Snackbar.make(parentLayout, Html.fromHtml("<font color=\"#000000\">" + missatge + "</font>"), Snackbar.LENGTH_LONG);

        View snackbarView = snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
        params.gravity = Gravity.TOP;
        snackbarView.setLayoutParams(params);
        snackbarView.setBackgroundColor(getColor(android.R.color.holo_green_dark));

        snackbar.show();
    }

}