package com.example.gestioarticles.activities.stockmanage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.gestioarticles.R;
import com.example.gestioarticles.databasetools.GestioArticlesDataSource;
import com.google.android.material.snackbar.Snackbar;

public class MovementsHistoryActivity extends AppCompatActivity {

    /* .: 1. VARIABLES GLOBALS :. */
    /** Variable que emmagatzema el id de l'article que s'està gestionant.
     * -1 si no hi ha cap seleccionat */
    private long idArticle;

    /** Variable que emmagatzemen l'ordre per mostrar els articles.
     * Per defecte, s'ordenarà pel camp DIA de manera descendent */
    private String sortType = GestioArticlesDataSource.MOVIMENT_DIA + " desc";
    private int sortPosition = 0;

    /** Variable que emmagatzema el DataSource, el qual permet treballar amb la BBDD
     * fer Inserts, Updates, Deletes, etc. */
    private GestioArticlesDataSource bbdd;

    // Elements del layout
    ImageButton btnFilterDate;
    EditText inputDateFrom;
    EditText inputDateTo;
    ListView listHistory;


    /* .: 2. CREACIÓ DE L'ACTIVITY :. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movements_history);

        // Es modifica la ActionBar per mostrar el botò per retornar i canviar el títol
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.activity_movements_history_title));

        // S'instancia la variable, per poder tenir eines per tractar amb la bbdd
        bbdd = new GestioArticlesDataSource(this);

        // Es recupera el ID que se li envia des de la MainActivity
        idArticle = getIntent().getExtras().getLong("id");

        // Es vinculen els elements del layout amb les variables
        btnFilterDate = (ImageButton) findViewById(R.id.btn_history_date_search);
        inputDateFrom = (EditText) findViewById(R.id.input_history_date_from);
        inputDateTo = (EditText) findViewById(R.id.input_history_date_to);
        listHistory = (ListView) findViewById(R.id.list_history_movements);
    }


    /* .: 3. MENÚ PERSONALITZAT :. */
    /** Menú personalitzat amb icones d'acció */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_movements_history_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finalitzarActivity();
                return true;
            case R.id.menu_btn_search_article_history:
                mostrarAlertCercarArticle();
                return true;
            case R.id.menu_btn_history_order:
                mostrarAlertOrdenarLlistat();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /* .: 4. ACCIONS PER FINALITZAR L'ACTIVITY :. */
    /** Finalitza l'activity, marcant com a ResultCode un OK */
    private void finalitzarActivity() {
        setResult(RESULT_OK);
        finish();
    }

    // Listener que controla l'acció del botó "Endarrera" del telèfon
    @Override
    public void onBackPressed() {
        finalitzarActivity();
    }


    /* .: 5. ALERTES :. */
    /** Mostra un dialog que permet introduir el codi de l'article que es vol cercar */
    private void mostrarAlertOrdenarLlistat() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.alert_info_title_order));

        String[] sorts = new String[]{
                getString(R.string.alert_info_order_by_date_desc),
                getString(R.string.alert_info_order_by_date)
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

        alert.show();
    }

    /** Mostra un dialog que permet seleccionar quin serà l'ordre en el que es mostrarà la llista */
    private void mostrarAlertCercarArticle() {

    }

    /** Mostra un Snackbar de color vermell en la part superior de la pantalla
     * notificant d'un error
     * @param error String amb el contingut del missatge que s'ha de mostrar*/
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


    /* .: 6. FUNCIONS PRÒPIES :. */
    private void seleccionarOrdre() {

        switch (sortPosition) {
            case 1:
                sortType = GestioArticlesDataSource.MOVIMENT_DIA;
                break;
            default:
                sortType = GestioArticlesDataSource.MOVIMENT_DIA + " desc";
        }

    }
}