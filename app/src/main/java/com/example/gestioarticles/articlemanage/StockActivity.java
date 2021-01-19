package com.example.gestioarticles.articlemanage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.gestioarticles.R;
import com.example.gestioarticles.databasetools.GestioArticlesDataSource;

public class StockActivity extends AppCompatActivity {

    /* .: 1. VARIABLES GLOBALS :. */
    /** Variable que emmagatzema el id de l'article que s'està gestionant */
    private long id;
    /** Variable que emmagatzema el tipus de crida que s'ha fet en aquesta activity */
    private int stockType;

    /** Variable que emmagatzema el DataSource, el qual permet treballar amb la BBDD
     * fer Inserts, Updates, Deletes, etc. */
    private GestioArticlesDataSource bbdd;


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
}