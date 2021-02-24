package com.example.gestioarticles.activities.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import cz.msebera.android.httpclient.Header;

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
    private ImageView imgClouds;
    private TextView txtClouds;
    private ImageView imgThermicSensation;
    private TextView txtThermicSensation;
    private ImageView imgWind;
    private TextView txtWind;
    private ImageView imgHumidity;
    private TextView txtHumidity;
    private ImageView imgSnow;
    private TextView txtSnow;
    private ImageView imgRain;
    private TextView txtRain;
    private FloatingActionButton btnSearch;
    private ImageView imgNotLocation;
    private TextView txtNotLocation;
    private AlertDialog searchAlert;

    /** Guarda el contingut del JSON de la resposta per quan es roti la pantalla */
    private String jsonMessage = "";


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

        mostrarAlertCiutat();

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarAlertCiutat();
            }
        });

        ocultarLayout(true);
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
        searchAlert = new AlertDialog.Builder(this).create();
        searchAlert.setTitle(getString(R.string.activity_weather_location));

        EditText edtLocation = new EditText(this);
        searchAlert.setView(edtLocation);

        // Botó positiu
        searchAlert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.alert_info_accept), new DialogInterface.OnClickListener() {
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
                    ocultarLayout(true);
                }
            }
        });

        // Botó negatiu
        searchAlert.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.alert_info_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        searchAlert.show();
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


    /* .: 5. CONTROL DE ROTACIÓ DE LA PANTALLA :. */
    // Quan es rota la pantalla es guarda el contingut del json de la ciutat seleccionada
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putString("jsonMessage", jsonMessage);

        super.onSaveInstanceState(outState);
    }

    // Quan es torna a carregar l'activity, es recupera aquest json
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {

        String jsonContent = savedInstanceState.getString("jsonMessage");

        if (jsonContent.length() > 0) {
            jsonMessage = jsonContent;
            recuperarDadesCiutat(jsonContent);
        }

        super.onRestoreInstanceState(savedInstanceState);
    }


    /* .: 6. FUNCIONS PRÒPIES :. */
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
        imgClouds = (ImageView) findViewById(R.id.img_weather_clouds);
        txtClouds = (TextView) findViewById(R.id.txt_clouds);
        imgThermicSensation = (ImageView) findViewById(R.id.img_temperature_sensation);
        txtThermicSensation = (TextView) findViewById(R.id.txt_temperature_sensation);
        imgWind = (ImageView) findViewById(R.id.img_weather_wind);
        txtWind = (TextView) findViewById(R.id.txt_wind);
        imgHumidity = (ImageView) findViewById(R.id.img_weather_humidity);
        txtHumidity = (TextView) findViewById(R.id.txt_humidity);
        imgSnow = (ImageView) findViewById(R.id.img_weather_snow);
        txtSnow = (TextView) findViewById(R.id.txt_snow);
        imgRain = (ImageView) findViewById(R.id.img_weather_rain);
        txtRain = (TextView) findViewById(R.id.txt_rain);
        btnSearch = (FloatingActionButton) findViewById(R.id.btn_search_weather);
        imgNotLocation = (ImageView) findViewById(R.id.img_sad_face2);
        txtNotLocation = (TextView) findViewById(R.id.txt_empty_article2);
    }

    /** Permet fer la petició HTTP a OpenWeatherMap amb la localització especificada */
    private void getWeatherInfo(String location) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(0, 10000);

        String url = OpenWeatherMapApi.getUrlWeather(location, getString(R.string.app_lang));

        client.get(this, url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                progressDialog.setTitle(getString(R.string.activity_weather_getting_info));
                progressDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressDialog.setTitle(getString(R.string.activity_weather_processing_info));

                // Es pasa la resposta a String
                jsonMessage = new String(responseBody);
                JSONObject jsonResponse = null;

                // Es converteix la resposta en un objecte JSON per poder
                // treballar amb ell desde JAVA
                try {
                    jsonResponse = new JSONObject(jsonMessage);
                }
                catch (Exception e) {
                    mostrarSnackBarError(getString(R.string.activity_weather_error_getting_info));
                    jsonMessage = "";
                    ocultarLayout(true);
                }

                // El JSON s'ha convertit bé?
                if (jsonResponse != null) {
                    try {
                        procesarJson(jsonResponse);
                        ocultarLayout(false);
                        mostrarSnackBarCorrecte(getString(R.string.alert_success_processing_location));
                    }
                    catch (Exception e) {
                        mostrarSnackBarError(getString(R.string.activity_weather_error_getting_info));
                        jsonMessage = "";
                        ocultarLayout(true);
                    }
                }

                progressDialog.hide();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.hide();
                String errorString = error.getMessage().toString();
                errorString = getString(R.string.activity_weather_error_getting_info) + ". " + errorString;

                mostrarSnackBarError(errorString);
                ocultarLayout(true);
                jsonMessage = "";
                mostrarAlertCiutat();
            }
        });
    }

    private void recuperarDadesCiutat(String jsonContent) {

        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(jsonContent);
            procesarJson(jsonObject);
        }
        catch (Exception e) {
            ocultarLayout(true);
            return;
        }

        searchAlert.hide();
        ocultarLayout(false);
    }

    /** Procesa el JSON con la respuesta de OpenWeatherAPI
     * y va mostrando los datos por pantalla
     * @param json JSONObject con el json de la respuesta*/
    private void procesarJson(JSONObject json) throws JSONException, IOException {

        // Es recupera el nom de la ubicació treduit des del JSON
        String string = json.getString("name");
        txtLocation.setText(string);

        // Es recupera l'array amb la informació del clima. Es pasa a JSONObject
        JSONArray jsonArray = (JSONArray) json.get("weather");
        JSONObject jsonObject = (JSONObject) jsonArray.get(0);

        // Es recupera la icona
        String icon = jsonObject.getString("icon");
        icon = OpenWeatherMapApi.getUrlIcon(icon);
        Picasso.get().load(icon).into(imgWeather);
        Picasso.get().load(icon).into(imgDescription);

        // Es recupera la informació de la descripció i s'aplica al layout
        string = jsonObject.getString("description");
        string = string.substring(0, 1).toUpperCase() + string.substring(1);
        txtDescription.setText(string);

        // Es recupera la temperatura
        jsonObject = (JSONObject) json.get("main");
        string = String.valueOf(jsonObject.getDouble("temp"));
        string = string + "ºC";
        txtTemperature.setText(string);

        // Es recuepra la temperatura mínima
        string = String.valueOf(jsonObject.getDouble("temp_min"));
        string = string + "ºC";
        txtTemperatureMin.setText(string);

        // Es recupera la temperatura màxima
        string = String.valueOf(jsonObject.getDouble("temp_max"));
        string = string + "ºC";
        txtTemperatureMax.setText(string);

        // Es recupera la sensació tèrmica
        string = String.valueOf(jsonObject.getDouble("feels_like"));
        string = string + "ºC";
        txtThermicSensation.setText(string);

        // Es recupera la humitat
        string = String.valueOf(jsonObject.getDouble("humidity"));
        string = string + "%";
        txtHumidity.setText(string);

        // Es recupera la informació del vent
        jsonObject = (JSONObject) json.get("wind");
        string = String.valueOf(jsonObject.get("speed"));
        string = string + "m/s";
        txtWind.setText(string);

        // Es recupera la informació sobre els núvols
        try {
            jsonObject = (JSONObject) json.get("clouds");
            string = String.valueOf(jsonObject.getDouble("all"));
            string = string + "%";
            txtClouds.setText(string);
            txtClouds.setVisibility(View.VISIBLE);
            imgClouds.setVisibility(View.VISIBLE);
        }
        catch (Exception e) {
            txtClouds.setVisibility(View.GONE);
            imgClouds.setVisibility(View.GONE);
        }

        // Es recupera la informació sobre la pluja
        try {
            jsonObject = (JSONObject) json.get("rain");
            string = String.valueOf(jsonObject.getDouble("1h"));
            string = string + "mm";
            txtRain.setText(string);
            txtRain.setVisibility(View.VISIBLE);
            imgRain.setVisibility(View.VISIBLE);
        }
        catch (Exception e) {
            txtRain.setVisibility(View.GONE);
            imgRain.setVisibility(View.GONE);
        }

        // Es recupera la infomació sobre la neu
        try {
            jsonObject = (JSONObject) json.get("snow");
            string = String.valueOf(jsonObject.getDouble("1h"));
            string = string + "mm";
            txtSnow.setText(string);
            txtSnow.setVisibility(View.VISIBLE);
            imgSnow.setVisibility(View.VISIBLE);
        }
        catch (Exception e) {
            txtSnow.setVisibility(View.GONE);
            imgSnow.setVisibility(View.GONE);
        }
    }

    private void ocultarLayout(boolean bool) {
        if (bool) {
            layout.setVisibility(View.GONE);
            imgNotLocation.setVisibility(View.VISIBLE);
            txtNotLocation.setVisibility(View.VISIBLE);
        }
        else {
            layout.setVisibility(View.VISIBLE);
            imgNotLocation.setVisibility(View.GONE);
            txtNotLocation.setVisibility(View.GONE);
        }
    }
}