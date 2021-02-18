package com.example.gestioarticles.activities.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gestioarticles.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

public class WeatherActivity extends AppCompatActivity {

    // Elements del layout
    private Dialog progressDialog;
    private LinearLayout layout;
    private TextView txtLocation;
    private ImageView imgWeather;
    private TextView txtTemperature;
    private ImageView imgDescription;
    private TextView txtDescription;
    private TextView txtTemperatureMin;
    private TextView txtTemperatureMax;
    private ImageView imgWind;
    private TextView txtWind;
    private ImageView imgHumidity;
    private TextView txtHumidity;
    private ImageView imgSnow;
    private TextView txtSnow;
    private ImageView imgRain;
    private TextView txtRain;
    private FloatingActionButton btnSearch;


    /* .: 2. CREACIÓ DE L'ACTIVITY :. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        // Es modifica la ActionBar per mostrar el botò per retornar i canviar el títol
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.activity_weather_title));

        // Inicialitzem el progress dialog, tot i no mostrar-lo encara
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        vincularElements();

        // layout.setVisibility(View.GONE);

        mostrarAlertCiutat();

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarAlertCiutat();
            }
        });

    }


    /* .: 3. MENÚ PERSONALITZAT :. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_weather_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_btn_search_weather:
                mostrarAlertCiutat();
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /* .: 4. ALERTES :. */
    /** Mostra l'alert que permet introduir una ubicació per obtenir-ne el dels dades
     * meteorològiques. */
    private void mostrarAlertCiutat() {
        AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setTitle(getString(R.string.activity_weather_location));

        EditText edtLocation = new EditText(this);
        alert.setView(edtLocation);

        // Botó positiu
        alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.alert_info_accept), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    String location = edtLocation.getText().toString().toLowerCase();
                    if (location.length() == 0) {
                        throw new Exception("Text null");
                    }
                    getWeatherInfo(location);
                }
                catch (Exception e) {
                    mostrarSnackBarError(getString(R.string.alert_error_invalid_location));
                }
            }
        });

        // Botó negatiu
        alert.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.alert_info_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.show();
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


    /* .: 5. FUNCIONS PRÒPIES :. */
    /** Permite vincular los elementos del layout con las variables de la activity */
    private void vincularElements() {
        layout = (LinearLayout) findViewById(R.id.layout_weather);
        txtLocation = (TextView) findViewById(R.id.txt_location);
        imgWeather = (ImageView) findViewById(R.id.img_weather);
        txtTemperature = (TextView) findViewById(R.id.txt_temperatura);
        imgDescription = (ImageView) findViewById(R.id.img_weather_description);
        txtDescription = (TextView) findViewById(R.id.txt_description);
        txtTemperatureMin = (TextView) findViewById(R.id.txt_temperature_min);
        txtTemperatureMax = (TextView) findViewById(R.id.txt_temperature_max);
        imgWind = (ImageView) findViewById(R.id.img_weather_wind);
        txtWind = (TextView) findViewById(R.id.txt_wind);
        imgHumidity = (ImageView) findViewById(R.id.img_weather_humidity);
        txtHumidity = (TextView) findViewById(R.id.txt_humidity);
        imgSnow = (ImageView) findViewById(R.id.img_weather_snow);
        txtSnow = (TextView) findViewById(R.id.txt_snow);
        imgRain = (ImageView) findViewById(R.id.img_weather_rain);
        txtRain = (TextView) findViewById(R.id.txt_rain);
        btnSearch = (FloatingActionButton) findViewById(R.id.btn_search_weather);
    }


    private void getWeatherInfo(String location) {

    }
}