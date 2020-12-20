package com.example.gestioarticles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.gestioarticles.databasetools.GestioArticlesDataSource;
import com.example.gestioarticles.enumerator.FilterEnum;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    /* .: 1. VARIABLES GLOBALS PER TENIR MEMÒRIA :. */
    // 'ID' que s'assignen a les activities i fan el codi més entenedor
    private static int ACTIVITY_ADD_ARTICLE = 1;
    private static int ACTIVITY_UPDATE_ARTICLE = 2;

    // Complements per gestionar la BBDD i modificar la llista
    private GestioArticlesDataSource bbdd;

    // Columnes i camps de la BBDD
    private static String[] from = new String[]{GestioArticlesDataSource.ARTICLE_CODI, GestioArticlesDataSource.ARTICLE_DESCRIPCIO, GestioArticlesDataSource.ARTICLE_ESTOC, GestioArticlesDataSource.ARTICLE_PREU};

    // Enumerador per saber el tipus de filtre, y String que emmagatzema la descripció filtrada
    private FilterEnum filtreActual;
    private String description;


    /* .: 2. CREACIÓ DE L'ACTIVITY :. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Botó per afegir articles
        FloatingActionButton brn_add = findViewById(R.id.btn_add);
        brn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "hola", Toast.LENGTH_LONG).show();
            }
        });

        carregarArticles();
    }

    /* .: 3. MENÚ PERSONALITZAT :. */
    // Menú personalitzat amb filtres
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu, menu);

        return true;
    }

    // Listeners pels botons del menú personalitzat
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_btn_all:
                return true;
            case R.id.menu_btn_description:
                return true;
            case R.id.menu_btn_stock:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /* .: 4. FUNCIONS PRÒPIES :. */
    // Carrega les dades i les mostra en el llistat
    private void carregarArticles() {

        Cursor articles = null;

        // Es revisa quin tipus de filtre està actiu. Per fer una select o una altre
        switch (filtreActual) {
            // S'han de mostrar tots els articles
            case FILTER_ALL:
                articles = bbdd.articles_all();
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

    }

}