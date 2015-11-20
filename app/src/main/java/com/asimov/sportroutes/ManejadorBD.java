package com.asimov.sportroutes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


/**
 * Created by Javier on 15/11/2015. */
class ManejadorBD extends SQLiteOpenHelper {
    //InformaciÃ³n de la base de datos
    private static final String DATABASE_NAME = "routesDatabase";
    private static final int DATABASE_VERSION = 1;

    //Tablas de la base de datos
    private static final String TABLE_RUTA = "ruta";
    private static final String TABLE_COORDENADA = "coordenada";

    //Columnas de la tabla ruta
    private static final String KEY_RUTA_ID = "id_ruta";
    private static final String KEY_RUTA_NOMBRE = "nombre";
    private static final String KEY_RUTA_ULTIMO = "ultimo";
    private static final String KEY_RUTA_MEJOR = "mejor";

    //Columnas de la tabla coordenada
    private static final String KEY_COORDENADA_ID = "id_coordenada";
    private static final String KEY_COORDENADA_LAT = "latitud";
    private static final String KEY_COORDENADA_LONG = "longitud";
    private static final String KEY_COORDENADA_RUTA_ID_FK = "num_ruta";

    public ManejadorBD(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //context.deleteDatabase(DATABASE_NAME);
    }

    public ManejadorBD(Context context, String a) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        context.deleteDatabase(DATABASE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_RUTA_TABLE = "CREATE TABLE " + TABLE_RUTA + " (" +

                KEY_RUTA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_RUTA_NOMBRE + " TEXT NOT NULL, " + KEY_RUTA_ULTIMO +
                " INTEGER NOT NULL, " + KEY_RUTA_MEJOR + " INTEGER NOT NULL);";


        String CREATE_COORDENADA_TABLE = "CREATE TABLE " + TABLE_COORDENADA +
                " (" + KEY_COORDENADA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_COORDENADA_LAT + " REAL NOT NULL, " + KEY_COORDENADA_LONG +
                " REAL NOT NULL, " + KEY_COORDENADA_RUTA_ID_FK +
                " INTEGER NOT NULL, " + "FOREIGN KEY (" + KEY_COORDENADA_RUTA_ID_FK +
                ") REFERENCES " + TABLE_RUTA + " (" + KEY_RUTA_ID + "));";


        //Log.d("SQL", "onCreate(). Creando base de datos");
        //Log.d("SQL", "Ejecutando SQL: " + CREATE_RUTA_TABLE);
        db.execSQL(CREATE_RUTA_TABLE);
        //Log.d("SQL", "Ejecutando SQL: " + CREATE_COORDENADA_TABLE);
        db.execSQL(CREATE_COORDENADA_TABLE);

        //Log.d("SQL", "Base de datos creada");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Log.d("SQL", "onUpgrade() ManejadorBD");

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COORDENADA + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RUTA + ";");
        onCreate(db);
    }

    //Operaciones CRUD

    //MÃ©todo para almacenar una nueva ruta en la base de datos
    public void guardarRuta(Ruta ruta) {
        //Creamos un objeto de la clase ManejadorDB para realizar operaciones con la base de datos
        SQLiteDatabase db = getWritableDatabase();
        Integer i, num_ruta;

        //Creamos la consulta SQL para insertar los datos de la ruta en la base de datos
        String sql = "INSERT INTO " + TABLE_RUTA + " VALUES (null, \"" + ruta.getNombre() + "\", "
                + ruta.getTiempoUltimo() + " ," + ruta.getTiempoMejor() + ");";
        //Log.d("SQL", "Ejecutando SQL: " + sql);
        db.execSQL(sql);

        //Creamos una consulta SQL para obtener el nÃºmero de la ruta en la tabla de la base de
        // datos (columna autoincremental)
        sql = "SELECT " + KEY_RUTA_ID + " FROM " + TABLE_RUTA + " WHERE " + KEY_RUTA_NOMBRE +
                " LIKE '%" + ruta.getNombre() + "%';";
        //Log.d("SQL", "Ejecutando SQL: " + sql);
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        num_ruta = cursor.getInt(0);
        cursor.close();

        //Creamos un ArrayList para leer las coordenadas de la ruta
        ArrayList<LatLng> coord = ruta.getCoordenadas();

        //Recorremos el ArrayList de coordenadas y las vamos introduciendo en la base de datos
        for (i = 0; i < ruta.getSize(); i++) {
            sql = "INSERT INTO " + TABLE_COORDENADA + " VALUES (null, " + coord.get(i).latitude
                    + ", " + coord.get(i).longitude + ", " + num_ruta + ");";
            //Log.d("SQL", "Ejecutando SQL: " + sql);
            db.execSQL(sql);
        }

        //Cerramos la instancia de la base de datos
        db.close();
    }

    //MÃ©todo que recupera todas las rutas almacenadas en la base de datos y las devuelve en un
    // ArrayList<Ruta>
    public ArrayList<Ruta> obtenerRutas(){
        ArrayList<Ruta> rutas = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Integer num_ruta;

        //Creamos la consulta SQL y la ejecutamos
        String sql = "SELECT * FROM " + TABLE_RUTA + ";";
        //Log.d("SQL","Ejecutando SQL: " + sql);
        Cursor cursor = db.rawQuery(sql, null);

        //Obtenemos la informaciÃ³n de la Ruta y lo almacenamos en un objeto del mismo tipo
        while(cursor.moveToNext()){
            num_ruta = cursor.getInt(0);
            Ruta ruta = new Ruta(cursor.getString(1));
            ruta.setTiempoInicio(cursor.getLong(2));
            ruta.setTiempo(cursor.getLong(3));

            //Consultamos la tabla coordenada para encontrar las coordenadas que coincidan con el
            // id de la ruta que estamos leyendo en esta iteraciÃ³n del bucle
            String sql2 = "SELECT * FROM " + TABLE_COORDENADA + " WHERE " +
                    KEY_COORDENADA_RUTA_ID_FK + " = " + num_ruta + ";";
            /*
            Log.d("SQL","Ejecutando SQL: " + sql2);
            Log.d("SQL", "Resultado SQL ruta: " + cursor.getString(0) + " " + cursor.getString(1)
                    + " " + cursor.getString(2) + " " + cursor.getString(3));
            */
            Cursor cursor2 = db.rawQuery(sql2, null);

            //Almacenamos las coordenadas obtenidas para nuestra ruta y las aÃ±adimos al objeto de
            // tipo Ruta
            while(cursor2.moveToNext()){
                /*
                Log.d("SQL", "Resultado SQL coordenada: " + cursor2.getString(0) + " " +
                        cursor2.getString(1) + " " + cursor2.getString(2) + " " +
                        cursor2.getString(3));
                */
                LatLng coordenada = new LatLng(Double.valueOf(cursor2.getString(1)),
                        Double.valueOf(cursor2.getString(2)));
                ruta.addCoordenada(coordenada);
            }
            cursor2.close();
            rutas.add(ruta);
        }
        cursor.close();
        db.close();
        return rutas;
    }

    //Método que comprueba en la base de datos si hay almacenada alguna ruta con el nombre que
    // recibe como argumento.
    public boolean existeRuta(String ruta){
        SQLiteDatabase db = getReadableDatabase();

        //Creamos la consulta SQL y la ejecutamos
        String sql = "SELECT " + KEY_RUTA_NOMBRE + " FROM " + TABLE_RUTA + " WHERE " +
                KEY_RUTA_NOMBRE + " LIKE '%" + ruta + "%';";
        //Log.d("SQL", "Ejecutando SQL: " + sql);
        Cursor cursor = db.rawQuery(sql, null);

        //Contamos el número de fila devueltas. Si no devuelve ninguna la consulta SQL, la ruta no
        // se encuentra almacenada en la base de datos
        if (cursor.getCount() == 0){
            //Log.d("SQL", "La ruta " + ruta + " NO está en la base de datos");
            cursor.close();
            db.close();
            return false;
        }
        else{
            //Log.d("SQL", "La ruta " + ruta + " SÍ está en la base de datos");
            cursor.close();
            db.close();
            return true;
        }

    }

    //DEBUGGING
    //MÃ©todo que lee
    public void leerBD() {
        Log.d("SQL", "Leyendo base de datos");
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id_ruta, nombre from ruta", null);
        Log.d("SQL", "Imprimiendo tabla ruta");
        Integer j = 0;
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            j++;
            Log.d("SQL", "Linea " + j);

            Integer i;
            Integer col = cursor.getColumnCount();
            for (i = 0; i < col; i++) {
                Log.d("SQL", cursor.getString(i));
            }
        }

        cursor = db.rawQuery("SELECT * from coordenada", null);
        Log.d("SQL", "Imprimiendo tabla coordenada");
        j = 0;
        while (cursor.moveToNext()) {
            j++;
            Log.d("SQL", "Linea " + j);

            Integer i;
            Integer col = cursor.getColumnCount();
            for (i = 0; i < col; i++) {
                Log.d("SQL", cursor.getString(i));
            }
        }
        cursor.close();
        db.close();
    }

}