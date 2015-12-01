package com.asimov.sportroutes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;

public class ListaDeRutasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_de_rutas);
        TableLayout tabla = (TableLayout) findViewById(R.id.tablaRutas);
        TableRow fila = (TableRow) findViewById(R.id.Fila);

    }
}
