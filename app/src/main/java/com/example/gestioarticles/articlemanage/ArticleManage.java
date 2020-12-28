package com.example.gestioarticles.articlemanage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.gestioarticles.R;
import com.example.gestioarticles.databasetools.GestioArticlesDataSource;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

            // Es desactiva el botó per editar l'article
            btnEdit.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
        }
        else {
            // Es canvia el títol de l'activity
            actionBar.setTitle(R.string.activity_article_manage_article_update_title);

            // Es desactiva el botó per afegir articles
            btnAdd.setVisibility(View.GONE);
        }

    }


    /* .: 3. ACCIONS PER FINALITZAR L'ACTIVITY :. */
    private void finalitzarActivity() {

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
     * en el recurs String. Segons l'idioma del dispositiu */
    private void crearArrayFamilies() {
        familiesArticle.add(getString(R.string.activity_article_manage_article_family_none));       // Cap
        familiesArticle.add(getString(R.string.activity_article_manage_article_family_software));   // Software
        familiesArticle.add(getString(R.string.activity_article_manage_article_family_hardware));   // Hardware
        familiesArticle.add(getString(R.string.activity_article_manage_article_family_other));      // Altres
    }
}