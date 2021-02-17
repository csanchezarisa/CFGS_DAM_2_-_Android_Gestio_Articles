package com.example.gestioarticles.activities.weather;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import com.example.gestioarticles.R;

public class WeatherActivity extends AppCompatActivity {

    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        // Es modifica la ActionBar per mostrar el botò per retornar i canviar el títol
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.activity_main_menu_btn_weather));

        // Inicialitzem el progress dialog, tot i no mostrar-lo encara
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }
}