package com.example.gestioarticles.activities.stockmanage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gestioarticles.R;
import com.example.gestioarticles.adapter.MovementsHistoryAdapter;
import com.example.gestioarticles.assets.datepicker.DatePickerFragment;
import com.example.gestioarticles.assets.datetype.Date;
import com.example.gestioarticles.databasetools.GestioArticlesDataSource;
import com.google.android.material.snackbar.Snackbar;

public class MovementsHistoryActivity extends AppCompatActivity {

    /* .: 1. VARIABLES GLOBALS :. */
    /** Variable que emmagatzema el id de l'article que s'està gestionant.
     * -1 si no hi ha cap seleccionat */
    private long idArticle;

    /** Variable que emmagatzemen l'ordre per mostrar els articles.
     * Per defecte, s'ordenarà pel camp DIA de manera descendent */
    private String sortType = GestioArticlesDataSource.MOVIMENT_CODI_ARTICLE;
    private int sortPosition = 0;

    /** Variable que emmagatzema el filtre que s'està utilitzant actualment */
    private char filterType = ' ';
    private int filterPosition = 0;

    /** Variable que emmagatzema el DataSource, el qual permet treballar amb la BBDD
     * fer Inserts, Updates, Deletes, etc. */
    private GestioArticlesDataSource bbdd;

    /** Adapter per mostrar els elements en el llistat */
    private MovementsHistoryAdapter adapter;

    // Columnes i camps de la BBDD
    private static final String[] from = new String[]{GestioArticlesDataSource.ARTICLE_CODI, GestioArticlesDataSource.MOVIMENT_DIA, GestioArticlesDataSource.MOVIMENT_QUANTITAT, GestioArticlesDataSource.MOVIMENT_TIPUS};
    private static final int[] to = new int[]{R.id.txt_movement_article_code, R.id.txt_movement_date, R.id.txt_movement_quantity, R.id.txt_movement_type};

    // Dates per fer el filtratge
    private Date startDateFilter;
    private Date finalDateFilter;

    // Elements del layout
    ImageButton btnFilterDate;
    ImageButton btnClearDate;
    EditText inputDateFrom;
    EditText inputDateTo;
    ListView listHistory;
    TextView title;


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
        btnClearDate = (ImageButton) findViewById(R.id.btn_history_date_clear);
        inputDateFrom = (EditText) findViewById(R.id.input_history_date_from);
        inputDateTo = (EditText) findViewById(R.id.input_history_date_to);
        listHistory = (ListView) findViewById(R.id.list_history_movements);
        title = (TextView) findViewById(R.id.txt_history_title);

        // Es carreguen els moviments en el llistat
        loadMovements();

        // Listeners
        inputDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePickerDialog(inputDateTo);
            }
        });

        inputDateTo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                inputDateTo.setText(null);
                return false;
            }
        });

        inputDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePickerDialog(inputDateFrom);
            }
        });

        inputDateFrom.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                inputDateFrom.setText(null);
                return false;
            }
        });

        btnClearDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearDates();
            }
        });

        btnFilterDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDates();
            }
        });
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
            case R.id.menu_btn_filter:
                mostrarAlertFiltrar();
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
    /** Mostra un dialog que permet seleccionar quin serà l'ordre en el que es mostrarà la llista */
    private void mostrarAlertOrdenarLlistat() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.alert_info_title_order));

        String[] sorts = new String[]{
                getString(R.string.alert_info_order_by_code),
                getString(R.string.alert_info_order_by_code_desc),
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

    /** Mostra un dialog que permet introduir el codi de l'article que es vol cercar */
    private void mostrarAlertCercarArticle() {

        AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setTitle(getString(R.string.activity_main_txt_article_codi));

        EditText edtArticleCode = new EditText(this);
        alert.setView(edtArticleCode);

        alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.alert_info_accept), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String articleCode;

                try {
                    articleCode  = edtArticleCode.getText().toString();
                }
                catch (Exception e) {
                    mostrarSnackBarError(getString(R.string.alert_error_cant_filter_articles));
                    title.setText(getString(R.string.activity_movement_history_all_movements));
                    return;
                }

                if (articleCode.length() > 0) {
                    filtrarArticle(articleCode);
                }
                else {
                    mostrarSnackBarError(getString(R.string.alert_error_cant_filter_articles));
                }
            }
        });

        alert.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.alert_info_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // No fa res
            }
        });

        alert.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.alert_info_reset), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                idArticle = -1;
                loadMovements();

            }
        });

        alert.show();

    }

    /** Mostra un AlertDialog que permet seleccionar els filtres pels quals poder
     * filtrar els elements que es mostren en el llistat. */
    private void mostrarAlertFiltrar() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(getString(R.string.alert_info_title_filter));

        String[] filtres = new String[] {getString(R.string.activity_movement_history_all_movements), getString(R.string.activity_movement_history_row_type_stock_in), getString(R.string.activity_movement_history_row_type_stock_out)};

        alert.setSingleChoiceItems(filtres, filterPosition, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                filterPosition = which;
            }
        });

        alert.setPositiveButton(R.string.alert_info_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                seleccionarFiltre();
            }
        });

        alert.setNegativeButton(R.string.alert_info_cancel, null);

        alert.setNeutralButton(R.string.alert_info_reset, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                filterPosition = 0;
                filterType = ' ';
                loadMovements();
            }
        });

        alert.show();
    }

    /** Mostra un Dialog que permet seleccionar una data de manera gràfica
     * @param input Permet especificar a quin input se li ha de posar el resultat */
    private void mostrarDatePickerDialog(EditText input) {
        DatePickerFragment datePickerDialog;
        datePickerDialog = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Date data = new Date(dayOfMonth, month, year);
                input.setText(data.getEuropeanDate());
            }
        });

        datePickerDialog.show(getSupportFragmentManager(), "datePicker");
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
    private void clearDates() {
        inputDateFrom.setText(null);
        inputDateTo.setText(null);
    }

    /** Carrega per primer cop els moviments en el llistat */
    private void loadMovements() {

        Cursor moviments;

        // Comprova si algun article ha estat seleccionat
        if (idArticle > -1) {

            // Fa la consulta dels moviments de l'article seleccionat
            moviments = bbdd.getMovimentsByArticleID(idArticle, sortType, startDateFilter, finalDateFilter);
            title.setText(getString(R.string.activity_movement_history_article_selected_movements));

        }
        else {

            // Fa la consulta de tots els moviments
            moviments = bbdd.getMoviments(sortType, startDateFilter, finalDateFilter);
            title.setText(getString(R.string.activity_movement_history_all_movements));

        }

        // S'inicialitza l'adapter amb la select feta
        adapter = new MovementsHistoryAdapter(this, R.layout.activity_movements_history_fila, moviments, from, to, 1);

        // Es vincula l'adapter amb el llistat
        listHistory.setAdapter(adapter);

    }

    /** Canvia l'ordre en que es mostren els elements del llistat */
    private void seleccionarOrdre() {

        switch (sortPosition) {
            case 1:
                sortType = GestioArticlesDataSource.MOVIMENT_CODI_ARTICLE + " desc";
                break;
            case 2:
                sortType = GestioArticlesDataSource.MOVIMENT_DIA + " desc";
                break;
            case 3:
                sortType = GestioArticlesDataSource.MOVIMENT_DIA;
                break;
            default:
                sortType = GestioArticlesDataSource.MOVIMENT_CODI_ARTICLE;
        }

        loadMovements();

    }

    /** Canvia el tipus de filtre segons quina posició hi ha seleccionada */
    private void seleccionarFiltre() {

        switch (filterPosition) {
            case 1:
                filterType = 'E';
                break;
            case 2:
                filterType = 'S';
                break;
            default:
                filterType = ' ';
        }

        loadMovements();

    }

    /** Fa la cerca de l'article segons el codi introduit. Si el troba, canvia la variable id
     * pel de l'article que s'ha seleccionat. Sino, la posa a -1 i mostra un error.
     * Després, refresca el llistat
     * @param articleCode String amb el codi de l'article que es vol cercar*/
    private void filtrarArticle(String articleCode) {

        // Es recupera un cursor amb els moviments de l'article seleccionat
        Cursor movimentsArticle = bbdd.getMovimentsByArticleCode(articleCode, sortType, startDateFilter, finalDateFilter);

        // Si la select funciona correctament, filtra. Sino, mostra un error
        if (movimentsArticle.moveToFirst()) {

            mostrarSnackBarCorrecte(getString(R.string.activity_stock_manage_article_founded));

            // Recupera l'id de l'article seleccionat
            idArticle = movimentsArticle.getLong(movimentsArticle.getColumnIndexOrThrow(bbdd.TABLE_ARTICLE + "." + bbdd.ARTICLE_ID));

            reloadList(movimentsArticle);

            title.setText(getString(R.string.activity_movement_history_article_selected_movements));

        }
        else {

            mostrarSnackBarError(getString(R.string.activity_stock_manage_article_not_founded));

            title.setText(getString(R.string.activity_movement_history_all_movements));

        }

    }

    private void setDates() {

        try {
            startDateFilter = new Date(inputDateFrom.getText().toString(), false);
        }
        catch (Exception e) {
            startDateFilter = null;
            inputDateFrom.setText(null);
        }

        try {
            finalDateFilter = new Date(inputDateTo.getText().toString(), false);
        }
        catch (Exception e) {
            finalDateFilter = null;
            inputDateTo.setText(null);
        }

        loadMovements();

    }

    private void filtrarArticle(long id) {



    }

    /** Refresca el contingut del llistat
     * @param movements Cursor amb la select per mostrar*/
    private void reloadList(Cursor movements) {

        adapter.changeCursor(movements);
        adapter.notifyDataSetChanged();

        listHistory.setSelection(0);

    }
}