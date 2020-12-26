package com.example.gestioarticles.articlemanage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.gestioarticles.R;
import com.example.gestioarticles.databasetools.GestioArticlesDataSource;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ArticleManage extends AppCompatActivity {

    /* .: 1. VARIABLES GLOBALS :. */
    /** Variable que emmagatzema el id de l'article que s'està gestionant */
    private long idArticle;

    /** Variable que emmagatzema el DataSource, el qual permet treballar amb la BBDD
     * fer Inserts, Updates, Deletes, etc. */
    private GestioArticlesDataSource bbdd;


    /* .: 2. CREACIÓ DE L'ACTIVITY :. */
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_manage);

        // Es recupera el ID que se li envia des de la MainActivity
        idArticle = getIntent().getExtras().getLong("id");

        // Es recuperen els tres Floating Button que hi ha al layout
        FloatingActionButton btnAdd = (FloatingActionButton) findViewById(R.id.btn_add_action);
        FloatingActionButton btnEdit = (FloatingActionButton) findViewById(R.id.btn_edit_action);
        FloatingActionButton btnDelete = (FloatingActionButton) findViewById(R.id.btn_delete_action);

        // Es modifica la ActionBar per mostrar el botò per retornar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

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
}