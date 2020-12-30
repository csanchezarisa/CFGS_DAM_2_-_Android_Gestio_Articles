package com.example.gestioarticles.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gestioarticles.MainActivity;
import com.example.gestioarticles.R;
import com.example.gestioarticles.databasetools.GestioArticlesDataSource;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;


public class ArticlesAdapter extends android.widget.SimpleCursorAdapter {

    // Constants que defineixen els colors que s'usaran més endavant
    private static final String BACKGROUND_COLOR_NO_STOCK_ARTICLE = "#FA8072";

    // Permet emmagatzemar el context, per poder utilitzar-lo en altres mètodes
    private MainActivity context;

    // Constructor de l'adapter
    public ArticlesAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.context = (MainActivity) context;
    }

    // Creació i manipulació de les files de la llista
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View fila = super.getView(position, convertView, parent);

        // Es recupera l'article que s'ha de mostrar en la fila
        Cursor article = (Cursor) getItem(position);

        // De l'article recuperat, aconseguim el seu estoc i el preu
        int stock = article.getInt(article.getColumnIndexOrThrow(GestioArticlesDataSource.ARTICLE_ESTOC));
        double price = article.getDouble(article.getColumnIndexOrThrow(GestioArticlesDataSource.ARTICLE_PREU));

        // Si l'estoc es inferior o igual a 0, el fons de la fila es pinta de vermell
        if (stock <= 0) {
            fila.setBackgroundColor(Color.parseColor(BACKGROUND_COLOR_NO_STOCK_ARTICLE));
        }

        // Canvia el contingut dels preus, per mostrar el símbol € i calcular l'IVA
        TextView element = (TextView) fila.findViewById(R.id.txt_article_preu_iva);
        element.setText(String.valueOf(price * 1.21) + "€");

        element = (TextView) fila.findViewById(R.id.txt_article_preu_no_iva);
        element.setText(element.getText().toString() + "€, no IVA");

        ImageView btnEliminar = (ImageView) fila.findViewById(R.id.btn_delete_article);
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // S'aconsegueix la fila
                View fila = (View) v.getParent();

                // S'aconsegueix el llistat sencer, ListView
                ListView llistat = (ListView) fila.getParent();

                // Es buca la posició que ocupa la fila dins del llistat
                int position = llistat.getPositionForView(fila);

                // Amb la posició, aconseguim l'article i el pasem a Cursor (Select)
                Cursor article = (Cursor) getItem(position);

                // Recuperem el id de l'article
                article.moveToFirst();
                long idArticle = article.getLong(article.getColumnIndexOrThrow(GestioArticlesDataSource.ARTICLE_ID));

                // Executem el mètode per mostrar l'alert de la MainActivity
                context.mostrarAlertaEliminar(idArticle);
            }
        });

        return fila;
    }

}
