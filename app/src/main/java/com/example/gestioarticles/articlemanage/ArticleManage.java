package com.example.gestioarticles.articlemanage;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.gestioarticles.R;
import com.example.gestioarticles.databasetools.GestioArticlesDataSource;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ArticleManage extends AppCompatActivity {

    /* .: 1. VARIABLES GLOBALS :. */
    /** Variable que emmagatzema el id de l'article que s'està gestionant */
    private long idArticle;

    /** Variable que emmagatzema el DataSource, el qual permet treballar amb la BBDD
     * fer Inserts, Updates, Deletes, etc. */
    private GestioArticlesDataSource bbdd;

    /** Llistat amb els tipus de familia que poden tenir els articles */
    private static ArrayList<String> familiesArticle = new ArrayList<String>();

    // Elements del Layout
    // Buttons
    private FloatingActionButton btnAdd;
    private FloatingActionButton btnEdit;
    private FloatingActionButton btnDelete;

    // Inputs
    private EditText inpCode;
    private EditText inpDescription;
    private EditText inpPrice;
    private EditText inpStock;
    private Spinner inpFamily;              // Spinner equival a un llistat del tipus 'Dropdown'


    /* .: 2. CREACIÓ DE L'ACTIVITY :. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_manage);

        // S'instancia la variable, per poder tenir eines per tractar amb la bbdd
        bbdd = new GestioArticlesDataSource(this);

        // Es recupera el ID que se li envia des de la MainActivity
        idArticle = getIntent().getExtras().getLong("id");

        // Es recuperen els elements modificables del layout
        // Floating buttons
        btnAdd = (FloatingActionButton) findViewById(R.id.btn_add_action);
        btnEdit = (FloatingActionButton) findViewById(R.id.btn_edit_action);
        btnDelete = (FloatingActionButton) findViewById(R.id.btn_delete_action);
        // Inputs
        inpCode = (EditText) findViewById(R.id.input_article_codi);
        inpDescription = (EditText) findViewById(R.id.input_article_descripcio);
        inpPrice = (EditText) findViewById(R.id.input_article_preu);
        inpStock = (EditText) findViewById(R.id.input_article_estoc);
        inpFamily = (Spinner) findViewById(R.id.input_article_familia);

        // S'omple l'array dels tius de familia amb les traduccions corresponents
        crearArrayFamilies();

        // Es modifica la ActionBar per mostrar el botò per retornar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Es crea un adaptador amb les families disponibles per l'article i s'injecten al spinner (dropdown)
        ArrayAdapter adaptadorFamilia = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, familiesArticle);
        inpFamily.setAdapter(adaptadorFamilia);

        // Segons si s'ha d'afegir o modificar un element. Es canvien els elements del layout
        if (idArticle < 0) {
            // Es canvia el títol de l'activity
            actionBar.setTitle(R.string.activity_article_manage_article_add_title);

            // Cerca el TextView corresponent al títol de l'estoc, per poder amagar-lo
            TextView stockTitle = (TextView) findViewById(R.id.txt_article_stock);

            // Es desactiven els elements
            btnEdit.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
            inpStock.setVisibility(View.GONE);
            stockTitle.setVisibility(View.GONE);
        }
        else {
            // Es canvia el títol de l'activity
            actionBar.setTitle(R.string.activity_article_manage_article_update_title);

            // Es desactiva el botó per afegir articles
            btnAdd.setVisibility(View.GONE);

            // Es bloqueja l'input del codi, per que no es pugui modificar
            inpCode.setVisibility(View.GONE);

            // Es recupera l'article de la BBDD
            Cursor article = bbdd.getArticle(idArticle);

            // Es carreguen les dades de l'article recuperat en el layout
            carregarDadesArticle(article);
        }

        // Listeners pels elements del layout
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                // Quan es fa click en el botó, es comprova que els inputs estiguin correctament omplenats
                if (comprovarInputs()) {
                    long idArticleAfegit = afegirArticle();

                    if (idArticleAfegit >= 0) {
                        mostrarSnackBarCorrecte(getString(R.string.alert_success_article_created));
                        finalitzarActivity();
                    }
                    else {
                        mostrarSnackBarError(getString(R.string.alert_error_cant_create_article));
                    }
                }
                else {
                    mostrarSnackBarError(getString(R.string.alert_error_cant_create_article));
                }
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }


    /* .: 3. ACCIONS PER FINALITZAR L'ACTIVITY :. */
    /** Finalitza l'activity, marcant com a ResultCode un OK */
    private void finalitzarActivity() {
        setResult(RESULT_OK);
        finish();
    }


    /* .: 4. MENÚ PERSONALITZAT :. */
    /** Menú personalitzat amb icones d'acció */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_article_manage_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.home:
                finalitzarActivity();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /* .: 5. FUNCIONS PRÒPIES :. */
    /** Omple l'array dels tipus de familia amb les traduccions que es troben
     * en el recurs String. Segons l'idioma del dispositiu.
     * Per no acumular informació, sempre que es crida al mètode s'esborra
     * el contingut de l'array */
    private void crearArrayFamilies() {

        familiesArticle.clear();
        familiesArticle.add(getString(R.string.activity_article_manage_article_family_none));       // Cap
        familiesArticle.add(getString(R.string.activity_article_manage_article_family_software));   // Software
        familiesArticle.add(getString(R.string.activity_article_manage_article_family_hardware));   // Hardware
        familiesArticle.add(getString(R.string.activity_article_manage_article_family_other));      // Altres

    }

    /** Revisa els valors que tenen els inputs del layout. Si troba cap problema retorna un false,
     * si tot ha anat correctament, retornarà un true. */
    private boolean comprovarInputs() {

        boolean focusRequired = false;
        boolean inputsCorrectes = true;

        // Revisa el codi
        if (idArticle < 0) {
            try {
                String code = inpCode.getText().toString();

                if (code.length() <= 0)
                    throw new Exception("Codi buit");

                if (bbdd.checkCode(code))
                    throw new Exception("Codi duplicat");
            }
            catch (Exception e) {
                inpCode.setText(null);
                inpCode.requestFocus();
                focusRequired = true;
                inputsCorrectes = false;
            }
        }

        // Revisa la descripció
        try {
            String description = inpDescription.getText().toString();

            if (description.length() <= 0)
                throw new Exception("Descripció buida");

        }
        catch (Exception e) {
            inpDescription.setText(null);
            if (!focusRequired) {
                inpDescription.requestFocus();
                focusRequired = true;
            }
            inputsCorrectes = false;
        }

        // Revisa el preu
        try {
            double price = Double.parseDouble(inpPrice.getText().toString());

            if (price < 0)
                throw new Exception("Preu negatiu");

        }
        catch (Exception e) {
            inpPrice.setText(null);
            if (!focusRequired) {
                inpPrice.requestFocus();
                focusRequired = true;
            }

            inputsCorrectes = false;
        }

        // Revisa l'estoc
        if (idArticle >= 0) {
            try {
                int stock = Integer.parseInt(inpStock.getText().toString());
            }
            catch (Exception e) {
                inpStock.setText(null);
                if (!focusRequired) {
                    inpStock.requestFocus();
                    focusRequired = true;
                }
                inputsCorrectes = false;
            }
        }

        return inputsCorrectes;
    }

    /** S'encarrega de fer l'insert en la BBDD amb els camps que hi ha als inputs.
     * @return Retorna un 'long', que fa referència al ID de l'article. Si és negatiu, l'article no s'ha
     * pogut afegir. Si es positiu, serà l'identificador de l'article en la BBDD*/
    private long afegirArticle() {

        boolean inputsCorrectes = true;

        String code = "";
        String description = "";
        String family = "";
        double price = 0;

        // Revisa el codi
        try {
            code = inpCode.getText().toString();

            if (code.length() <= 0)
                throw new Exception("Codi buit");

            if (bbdd.checkCode(code))
                throw new Exception("Codi duplicat");
        }
        catch (Exception e) {
            inpCode.setText(null);
            inputsCorrectes = false;
        }

        // Revisa la descripció
        try {
            description = inpDescription.getText().toString();

            if (description.length() <= 0)
                throw new Exception("Descripció buida");

        }
        catch (Exception e) {
            inpDescription.setText(null);
            inputsCorrectes = false;
        }

        try {
            family = inpFamily.getSelectedItem().toString();
        }
        catch (Exception e) {
            inputsCorrectes = false;
        }

        // Revisa el preu
        try {
            price = Double.parseDouble(inpPrice.getText().toString());

            if (price < 0)
                throw new Exception("Preu negatiu");

        }
        catch (Exception e) {
            inpPrice.setText(null);
            inputsCorrectes = false;
        }

        // Comprova si ha hagut algun error amb la extracció dels valors.
        // Si tot ha funcionat correctament, fa l'insert i retorna l'id de l'article
        if (inputsCorrectes) {
            return bbdd.insertArticle(code, description, family, price);
        }
        else {
            return -1;
        }
    }

    /** Carrega les dades de l'article seleccionat per mostrar-les en els diferents
     * inputs del layout.
     * Si hi ha cap problema fa un finish i tanca l'activity.
     * @param article Rep un Cursor com a paràmetre, amb el qual omple els inputs del layout*/
    private void carregarDadesArticle(Cursor article) {

        // Mou el cursor al primer registre. Si hi ha cap problema, finalitza l'activity
        if (article.moveToFirst()) {

            // Recupera
            String code = article.getString(article.getColumnIndexOrThrow(bbdd.ARTICLE_CODI));
            String description = article.getString(article.getColumnIndexOrThrow(bbdd.ARTICLE_DESCRIPCIO));
            String family = article.getString(article.getColumnIndexOrThrow(bbdd.ARTICLE_FAMILIA));
            String price = String.valueOf(article.getDouble(article.getColumnIndexOrThrow(bbdd.ARTICLE_PREU)));
            String stock = String.valueOf(article.getInt(article.getColumnIndexOrThrow(bbdd.ARTICLE_ESTOC)));

            // Canvia els valors dels inputs pels recuperats de l'article
            TextView txtArticleCode = (TextView) findViewById(R.id.txt_article_codi);
            txtArticleCode.setText(txtArticleCode.getText().toString() + ": " + code);
            inpDescription.setText(description);
            inpPrice.setText(price);
            inpStock.setText(stock);
        }
        else {
            finish();
        }

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