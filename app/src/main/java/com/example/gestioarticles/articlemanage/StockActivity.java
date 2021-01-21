package com.example.gestioarticles.articlemanage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.gestioarticles.MainActivity;
import com.example.gestioarticles.R;
import com.example.gestioarticles.assets.datepicker.DatePickerFragment;
import com.example.gestioarticles.assets.datetype.Date;
import com.example.gestioarticles.databasetools.GestioArticlesDataSource;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class StockActivity extends AppCompatActivity {

    /* .: 1. VARIABLES GLOBALS :. */
    /** Variable que emmagatzema el id de l'article que s'està gestionant */
    private long id;
    /** Variable que emmagatzema el tipus de crida que s'ha fet en aquesta activity */
    private int stockType;

    /** True quan s'ha buscat i s'ha verificat l'existencia de l'article amb el que
     * es vol enregistrar un moviment */
    private boolean validArticle;

    /** Variable que emmagatzema el DataSource, el qual permet treballar amb la BBDD
     * fer Inserts, Updates, Deletes, etc. */
    private GestioArticlesDataSource bbdd;

    // Elements del layout
    private EditText inputCode;
    private EditText inputQuantity;
    private EditText inputDate;
    private ImageButton btnSearch;
    private FloatingActionButton btnSetStock;


    /* .: 2. CREACIÓ DE L'ACTIVITY :. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        // Es modifica la ActionBar per mostrar el botò per retornar i canviar el títol
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.activity_stock_manage_title));

        // Es recuperen les dades que es passen des de l'activity que fa la crida
        id = getIntent().getExtras().getLong("id");
        stockType = getIntent().getExtras().getInt("stockType");

        // Revisa si l'id es negatiu. En cas de ser-ho, per evitar problemes, sempre serà -1
        if (id < 0) id = -1;

        // S'inicialitza el DataSource
        bbdd = new GestioArticlesDataSource(this);

        // Es vinculen els elements del layout
        inputCode = (EditText) findViewById(R.id.input_article_codi_search);
        inputDate = (EditText) findViewById(R.id.input_stock_date);
        inputQuantity = (EditText) findViewById(R.id.input_stock_quantity);
        btnSearch = (ImageButton) findViewById(R.id.btn_search);
        btnSetStock = (FloatingActionButton) findViewById(R.id.btn_stock_set);

        // Segons quin tipus de crida ha tingut, configura el layout
        switch (stockType) {
            case MainActivity.ACTIVITY_STOCK_IN:
                layoutStockIn();
                break;

            case MainActivity.ACTIVITY_STOCK_OUT:
                layoutStockOut();
                break;

            default:
                finish();
        }

        // Si l'id es positiu, carregarà les dades de l'article
        if (id >= 0) {
            carregarDadesArticle(id);
        }
        else {
            articleValid(false);
        }

        // Listeners
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cercarArticle();
            }
        });

        btnSetStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validArticle) {
                    registrarStock();
                }
                else {
                    mostrarSnackBarError(getText(R.string.activity_stock_manage_article_not_founded).toString());
                }

            }
        });

        inputDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePickerDialog();
            }
        });

    }


    /* .: 3. ACCIONS PER FINALITZAR L'ACTIVITY :. */
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


    /* .: 4. MENÚ :. */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finalitzarActivity();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* .: 5. PERSONALITZAR LAYOUT :. */
    /** Personalitza el layout per canviar el títol de l'activity
     * pel de moviments d'entrada */
    private void layoutStockIn() {
        TextView title = (TextView) findViewById(R.id.txt_title);
        title.setText(getString(R.string.activity_main_stock_in));
    }

    /** Personalitza el layout per canviar el títol de l'activity
     * pel de moviments de sortida */
    private void layoutStockOut() {
        TextView title = (TextView) findViewById(R.id.txt_title);
        title.setText(getString(R.string.activity_main_stock_out));
    }

    /** Fa una consulta que retorna un article. Amb les dades d'aquest
     * es personalitzen els elements del layout. */
    private void carregarDadesArticle(long id) {
        Cursor articleSeleccionat = bbdd.getArticle(id);

        if (articleSeleccionat.moveToFirst()) {
            String codiArticle = articleSeleccionat.getString(articleSeleccionat.getColumnIndexOrThrow(bbdd.ARTICLE_CODI));
            inputCode.setText(codiArticle);
            articleValid(true);
        }
        else {
            finish();
        }

    }

    /* .: 6. FUNCIONS PRÒPIES :. */
    /** Cerca l'article pel codi introduit dins del input de codi */
    private void cercarArticle() {

        String codiPerCercar;

        try {

            // Recupera el contingut del input de codi i revisa si hi ha contingut
            codiPerCercar = inputCode.getText().toString();
            if (codiPerCercar.length() <= 0) throw new Exception("Contingut nul");

        }
        catch (Exception e) {

            // Si hi ha algun problema es buida el input i no es fa la cerca
            inputCode.setText(null);
            return;

        }

        Cursor article = bbdd.getArticle(codiPerCercar);

        if (article.moveToFirst()) {
            id = article.getLong(article.getColumnIndexOrThrow(bbdd.ARTICLE_ID));
            articleValid(true);
            mostrarSnackBarCorrecte(getText(R.string.activity_stock_manage_article_founded).toString());
        }
        else {
            id = -1;
            articleValid(false);
            mostrarSnackBarError(getText(R.string.activity_stock_manage_article_not_founded).toString());
        }

    }

    private void articleValid(boolean valid) {
        this.validArticle = valid;
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

    /** Mostra un Dialog que permet seleccionar una data de manera gràfica */
    private void mostrarDatePickerDialog() {
        DatePickerFragment datePickerDialog;
        datePickerDialog = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Date data = new Date(dayOfMonth, month, year);
                inputDate.setText(data.getEuropeanDate());
            }
        });

        datePickerDialog.show(getSupportFragmentManager(), "datePicker");
    }

    /** Comprova els valors dels inputs i fa l'insert en la taula de moviments de la BBDD
     * i fa l'update en el número d'estoc de l'article */
    private void registrarStock() {

        Date data = null;
        int stock = 0;
        boolean dadesCorrectes = true;



        // Intenta recuperar la quantitat d'estoc
        try {
            stock = Integer.parseInt(inputQuantity.getText().toString());
        }
        catch (Exception e) {
            inputQuantity.setText(null);
            dadesCorrectes = false;
        }

        // Intenta recuperar la data i fer la transformació al tipus Date
        try {
            data = new Date(inputDate.getText().toString(), false);
        }
        catch (Exception e) {
            inputDate.setText(null);
            dadesCorrectes = false;
        }

        // Si ha hagut algún problema o l'id de l'article a insertar es negatiu, mostra l'error
        if (!dadesCorrectes || id < 0) {
            mostrarSnackBarError(getString(R.string.alert_error_cant_create_movement));
            return;
        }

        // Assigna un valor a la variable per decidir el tipus de moviment a insertar
        String tipusMoviment;
        switch (stockType) {
            case MainActivity.ACTIVITY_STOCK_IN:
                tipusMoviment = "E";
                break;

            case MainActivity.ACTIVITY_STOCK_OUT:
                tipusMoviment = "S";
                break;

            default:
                finish();
                return;
        }

        // Fa l'insert
        boolean movimentInsertatCorrectament = bbdd.insertMovement(id, data, stock, tipusMoviment);

        // Si ha funcionat correctament, tanca l'activity. Sino, mostra un error
        if (movimentInsertatCorrectament) {
            finalitzarActivity();
        }
        else {
            mostrarSnackBarError(getString(R.string.alert_error_cant_create_movement));
        }

    }
}