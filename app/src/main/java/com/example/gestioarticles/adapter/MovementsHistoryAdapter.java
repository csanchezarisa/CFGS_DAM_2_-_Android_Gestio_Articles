package com.example.gestioarticles.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.gestioarticles.R;
import com.example.gestioarticles.activities.stockmanage.MovementsHistoryActivity;
import com.example.gestioarticles.assets.datetype.Date;
import com.example.gestioarticles.databasetools.GestioArticlesDataSource;

public class MovementsHistoryAdapter extends SimpleCursorAdapter {

    // Constants que defineixen els colors que s'usaran més endavant
    private static final String BACKGROUND_COLOR_STOCK_OUT = "#FA8072";
    private static final String BACKGROUND_COLOR_STOCK_IN = "#72A8FA";

    private MovementsHistoryActivity context;

    public MovementsHistoryAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.context = (MovementsHistoryActivity) context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View fila = super.getView(position, convertView, parent);

        // Es recupera el moviment
        Cursor moviment = (Cursor) getItem(position);

        // Es modifica com es veu la data
        Date data = new Date(moviment.getString(moviment.getColumnIndexOrThrow(GestioArticlesDataSource.MOVIMENT_DIA)), true);
        TextView element = fila.findViewById(R.id.txt_movement_date);
        element.setText(data.getEuropeanDate());

        // Es posa una 'u' al final de les unitats
        element = fila.findViewById(R.id.txt_movement_quantity);
        element.setText(moviment.getInt(moviment.getColumnIndexOrThrow(GestioArticlesDataSource.MOVIMENT_QUANTITAT)) + " u");

        // Es canvia el tipus de moviment pel text corresponent, juntament amb el color i la icona de la fila
        element = fila.findViewById(R.id.txt_movement_type);
        ImageView imgMovementType = (ImageView) fila.findViewById(R.id.img_movement_type);
        switch (moviment.getString(moviment.getColumnIndexOrThrow(GestioArticlesDataSource.MOVIMENT_TIPUS)).toUpperCase().charAt(0)) {
            case 'E':
                element.setText(context.getString(R.string.activity_movement_history_row_type_stock_in));
                fila.setBackgroundColor(Color.parseColor(BACKGROUND_COLOR_STOCK_IN));
                imgMovementType.setImageResource(R.drawable.ic_movement_in);
                break;
            case 'S':
                element.setText(context.getString(R.string.activity_movement_history_row_type_stock_out));
                fila.setBackgroundColor(Color.parseColor(BACKGROUND_COLOR_STOCK_OUT));
                imgMovementType.setImageResource(R.drawable.ic_movement_out);
                break;
        }

        return fila;
    }
}
