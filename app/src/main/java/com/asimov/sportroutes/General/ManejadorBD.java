package com.asimov.sportroutes.General;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


/**
 * Created by Javier on 15/11/2015.
 *
 * Comunica el programa con la base de datos, todas las consultas a la misma deben pasar por
 *  ManejadorDB.
 */
public class ManejadorBD extends SQLiteOpenHelper {
    //Información de la base de datos
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

    /**
     * Creador de la base de datos.
     * @param context requerido por el super.
     */
    public ManejadorBD(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //context.deleteDatabase(DATABASE_NAME);
    }

    /**
     * Crea la base de datos y elimina la antigua si es necesario.
     * @param context necesario para el creador de SQLiteOpenHelper.
     * @param a String
     */
    public ManejadorBD(Context context, String a) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //context.deleteDatabase(DATABASE_NAME);
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


        Log.d("SQL", "onCreate(). Creando base de datos");
        //Log.d("SQL", "Ejecutando SQL: " + CREATE_RUTA_TABLE);
        db.execSQL(CREATE_RUTA_TABLE);
        //Log.d("SQL", "Ejecutando SQL: " + CREATE_COORDENADA_TABLE);
        db.execSQL(CREATE_COORDENADA_TABLE);
        Log.d("SQL", "Base de datos creada");

        Log.d("SQL", "Poblando base de datos");
        this.poblarBD(db);
        Log.d("SQL", "Base de datos poblada");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Log.d("SQL", "onUpgrade() ManejadorBD");

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COORDENADA + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RUTA + ";");
        onCreate(db);
    }

    //Operaciones CRUD


    /**
     * Método para almacenar una nueva ruta en la base de datos
     *  @param ruta Ruta, clase que contiene el contenido de la ruta(nombre, puntos...)
     */
    public void guardarRuta(Ruta ruta) {
        //Creamos un objeto de la clase ManejadorDB para realizar operaciones con la base de datos
        //Log.d("SQL","Obteniendo instancia de BD");
        SQLiteDatabase db = getWritableDatabase();
        //Log.d("SQL","Instancia de BD creada");
        Integer i, num_ruta;

        //Creamos la consulta SQL para insertar los datos de la ruta en la base de datos
        String sql = "INSERT INTO " + TABLE_RUTA + " VALUES (null, \"" + ruta.getNombre() + "\", "
                + ruta.getTiempoUltimo() + " ," + ruta.getTiempoMejor() + ");";
        //Log.d("SQL", "Ejecutando SQL: " + sql);
        Log.d("TIEMPO", "ULTIMO: "+ruta.getTiempoUltimo()+ "MEJOR"+ ruta.getTiempoMejor());
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

    /**
     * Método que recupera todas las rutas almacenadas en la base de datos y las devuelve en un
     *  @return ArrayList de Rutas que contendrá todas las rutas almacenadas en la base de datos.
     */
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
            ruta.setTiempoUltimo(cursor.getLong(2));
            ruta.setTiempo(cursor.getLong(3));
            Log.d("LOGD","El tiempo comparado: Ultimo"+ cursor.getLong(2) +  "-->" + ruta.getTiempoUltimo()+ "\nMejor "+ cursor.getLong(3) + " -->" +ruta.getTiempoMejor());

            //Consultamos la tabla coordenada para encontrar las coordenadas que coincidan con el
            // id de la ruta que estamos leyendo en esta iteraciÃ³n del bucle
            String sql2 = "SELECT * FROM " + TABLE_COORDENADA + " WHERE " +
                    KEY_COORDENADA_RUTA_ID_FK + " = " + num_ruta + ";";

//            Log.d("SQL","Ejecutando SQL: " + sql2);
            Log.d("SQL", "Resultado SQL ruta: " + cursor.getString(0) + " " + cursor.getString(1)
                    + " " + cursor.getString(2) + " " + cursor.getString(3));

            Cursor cursor2 = db.rawQuery(sql2, null);

            //Almacenamos las coordenadas obtenidas para nuestra ruta y las aÃ±adimos al objeto de
            // tipo Ruta
            while(cursor2.moveToNext()){
                Log.d("SQL", "Resultado SQL coordenada: " + cursor2.getString(0) + " " +
                        cursor2.getString(1) + " " + cursor2.getString(2) + " " +
                        cursor2.getString(3));

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


    /**
     * Comprueba si la ruta dada está almacenada en la base de datos.
     * @param ruta String, nombre de la ruta que se desea comprobar.
     * @return Boolean será true si la Ruta existe en la base de datos y false en caso contrario.
     */
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

    /**
     * Actualiza el mejor tiempo de la ruta indicada.
     * @param ruta String, nombre de la ruta que se desea actualizar.
     * @param tiempo Long, nuevo tiempo de la ruta.
     * @return Integer con el número de filas afectadas por la actualización.
     */
    public int actualizarRuta(String ruta, long tiempo){
        SQLiteDatabase db = getWritableDatabase();

        //Creamos el mapa de valores que deseamos cambiar de la tabla de la base de datos.
        ContentValues valores = new ContentValues();
        valores.put(KEY_RUTA_ULTIMO, tiempo);

        //Ejecutamos la actualización de la tabla de la base de datos.
        Log.d("SQL", "Actualizando ruta + " + ruta + ". Nuevo tiempo: " + tiempo + ".");
        return db.update(TABLE_RUTA, valores, KEY_RUTA_NOMBRE + " = ?",
                new String[] {ruta});

        //db.close();

    }
/**
     * Actualiza el mejor tiempo de la ruta indicada.
     * @param ruta String, nombre de la ruta que se desea actualizar.
     * @param tiempo Long, nuevo tiempo de la ruta.
     * @param best Boolean, indica si el tiempo es mejor
     * @return Integer con el número de filas afectadas por la actualización.
     */
    public int actualizarRuta(String ruta, long tiempo,Boolean best){
        SQLiteDatabase db = getWritableDatabase();

        //Creamos el mapa de valores que deseamos cambiar de la tabla de la base de datos.
        ContentValues valores = new ContentValues();
        valores.put(KEY_RUTA_ULTIMO, tiempo);
        if (best){
            valores.put(KEY_RUTA_MEJOR, tiempo);
        }

        //Ejecutamos la actualización de la tabla de la base de datos.
        Log.d("SQL", "Actualizando ruta + " + ruta + ". Nuevo tiempo: " + tiempo + ".");
        return db.update(TABLE_RUTA, valores, KEY_RUTA_NOMBRE + " = ?",
                new String[] {ruta});

        //db.close();

    }

    /**
     * Elimina la ruta deseada de la base de datos.
     * @param ruta String, nombre de la ruta que se desea eliminar de la base de datos.
     */
    public void borrarRuta(String ruta){
        int num_ruta;
        SQLiteDatabase db = getWritableDatabase();

        //Obtenemos el ID de la ruta que deseamos borrar de la base de datos.
        String sql = "SELECT " + KEY_RUTA_ID + " FROM " + TABLE_RUTA + " WHERE " +
                KEY_RUTA_NOMBRE + " LIKE '%" + ruta + "%';";
        //Log.d("SQL","Ejecutando SQL: " + sql);
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        num_ruta = cursor.getInt(0);
        cursor.close();

        //Log.d("SQL", "Eliminando ruta: " + ruta + ". ID ruta: " + num_ruta + ".");

        //Eliminamos de la tabla coordenadas aquellas entradas que sean de la ruta indicada.
        db.delete(TABLE_COORDENADA, KEY_COORDENADA_RUTA_ID_FK + " = ?", new String[]
                {String.valueOf(num_ruta)});

        //Eliminamos de la tabla ruta la entrada de la ruta que deseamos eliminar.
        db.delete(TABLE_RUTA, KEY_RUTA_NOMBRE + " = ?", new String[] {ruta});

    }

    /**
     * Puebla la base de datos con tres rutas a modo de ejemplo..
     */
    private void poblarBD(SQLiteDatabase db){
        Ruta ruta;
        int num_ruta;
        String sql;
        LatLng coord;

        //RUTA 1.
        //Creamos el objeto de tipo Ruta. Le damos nombre y añadimos el mejor tiempo.
        ruta = new Ruta("por defecto 1");
        ruta.setTiempo(79);

        //Creamos una a una las coordenadas de la ruta y las vamos añadiendo.
        coord = new LatLng(41.6426,-4.73203);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6426,-4.732);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6426,-4.73213);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6428,-4.73253);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6431,-4.73313);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6435,-4.73378);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6439,-4.73449);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6444,-4.73537);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.645,-4.73636);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6456,-4.73723);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6461,-4.73803);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6465,-4.73869);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6467,-4.73909);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6468,-4.73924);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6471,-4.73963);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6475,-4.74022);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.648,-4.74064);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6484,-4.74121);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6486,-4.74188);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6487,-4.74271);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6487,-4.74271);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6488,-4.74319);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.649,-4.74403);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.649,-4.74493);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6489,-4.74574);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6488,-4.74656);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6486,-4.74734);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6485,-4.74806);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6485,-4.74806);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6485,-4.74879);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6484,-4.7493);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6483,-4.74946);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6482,-4.7496);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6483,-4.74951);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6481,-4.74954);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6477,-4.74937);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6472,-4.74947);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6468,-4.74988);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6463,-4.75039);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6457,-4.75104);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6452,-4.75181);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6447,-4.75267);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6442,-4.75357);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6438,-4.7545);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6434,-4.75526);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6431,-4.75587);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6426,-4.75607);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6421,-4.7562);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6417,-4.75628);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6413,-4.7562);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6407,-4.75587);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6403,-4.75547);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6398,-4.75495);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6393,-4.75446);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6389,-4.75417);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6387,-4.75416);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6386,-4.75408);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6384,-4.75409);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6383,-4.75433);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6382,-4.75461);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6382,-4.75478);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6383,-4.75485);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6382,-4.75491);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6382,-4.75503);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6382,-4.75534);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.638,-4.75557);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6379,-4.75561);
        ruta.addCoordenada(coord);

        sql = "INSERT INTO " + TABLE_RUTA + " VALUES (null, \"" + ruta.getNombre() + "\", "
                + ruta.getTiempoUltimo() + " ," + ruta.getTiempoMejor() + ");";
        //Log.d("SQL", "Ejecutando SQL: " + sql);
        Log.d("TIEMPO", "ULTIMO: " + ruta.getTiempoUltimo() + "MEJOR" + ruta.getTiempoMejor());
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
        ArrayList<LatLng> coords = ruta.getCoordenadas();

        //Recorremos el ArrayList de coordenadas y las vamos introduciendo en la base de datos
        int i;
        for (i = 0; i < ruta.getSize(); i++) {
            sql = "INSERT INTO " + TABLE_COORDENADA + " VALUES (null, " + coords.get(i).latitude
                    + ", " + coords.get(i).longitude + ", " + num_ruta + ");";
            //Log.d("SQL", "Ejecutando SQL: " + sql);
            db.execSQL(sql);
        }

        //RUTA 2.
        //Creamos el objeto de tipo Ruta. Le damos nombre y añadimos el mejor tiempo.
        ruta = new Ruta("por defecto 2");
        ruta.setTiempo(332);

        //Creamos una a una las coordenadas de la ruta y las vamos añadiendo.
        coord = new LatLng(41.6683,-4.7239);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6686,-4.72298);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.669,-4.72204);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6694,-4.72117);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6695,-4.72066);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6691,-4.72023);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6691,-4.72023);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6685,-4.71981);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.668,-4.71958);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6675,-4.71921);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6669,-4.71884);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6666,-4.71872);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6661,-4.71844);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6659,-4.71842);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6651,-4.71811);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6646,-4.71795);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6641,-4.718);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6636,-4.71812);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6634,-4.7183);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6632,-4.71837);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.663,-4.71818);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6627,-4.71777);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6624,-4.71734);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6621,-4.71694);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6618,-4.71664);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6618,-4.71648);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6617,-4.71637);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6614,-4.71594);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.661,-4.71557);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6606,-4.7152);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6602,-4.71484);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6599,-4.71461);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6597,-4.71429);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6596,-4.71439);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6597,-4.71427);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6596,-4.71412);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6595,-4.71378);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6595,-4.71343);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6595,-4.71328);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6595,-4.71338);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6595,-4.71337);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6595,-4.71325);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6595,-4.71289);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6595,-4.71214);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6596,-4.71161);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6595,-4.7107);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6595,-4.71015);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6596,-4.70943);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6595,-4.70878);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6595,-4.70803);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6595,-4.70742);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6593,-4.70749);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.659,-4.70795);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6584,-4.70876);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6581,-4.70919);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6579,-4.70952);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6579,-4.70976);
        ruta.addCoordenada(coord);

        sql = "INSERT INTO " + TABLE_RUTA + " VALUES (null, \"" + ruta.getNombre() + "\", "
                + ruta.getTiempoUltimo() + " ," + ruta.getTiempoMejor() + ");";
        //Log.d("SQL", "Ejecutando SQL: " + sql);
        Log.d("TIEMPO", "ULTIMO: " + ruta.getTiempoUltimo() + "MEJOR" + ruta.getTiempoMejor());
        db.execSQL(sql);

        //Creamos una consulta SQL para obtener el nÃºmero de la ruta en la tabla de la base de
        // datos (columna autoincremental)
        sql = "SELECT " + KEY_RUTA_ID + " FROM " + TABLE_RUTA + " WHERE " + KEY_RUTA_NOMBRE +
                " LIKE '%" + ruta.getNombre() + "%';";
        //Log.d("SQL", "Ejecutando SQL: " + sql);
        cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        num_ruta = cursor.getInt(0);
        cursor.close();

        //Creamos un ArrayList para leer las coordenadas de la ruta
       coords = ruta.getCoordenadas();

        //Recorremos el ArrayList de coordenadas y las vamos introduciendo en la base de datos
        for (i = 0; i < ruta.getSize(); i++) {
            sql = "INSERT INTO " + TABLE_COORDENADA + " VALUES (null, " + coords.get(i).latitude
                    + ", " + coords.get(i).longitude + ", " + num_ruta + ");";
            //Log.d("SQL", "Ejecutando SQL: " + sql);
            db.execSQL(sql);
        }

        //RUTA 3.
        //Creamos el objeto de tipo Ruta. Le damos nombre y añadimos el mejor tiempo.
        ruta = new Ruta("por defecto 3");
        ruta.setTiempo(572);

        //Creamos una a una las coordenadas de la ruta y las vamos añadiendo.
        coord = new LatLng(41.6564,-4.71092);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6564,-4.71094);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6561,-4.71094);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.656,-4.71098);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6559,-4.71105);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6559,-4.71105);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6559,-4.71105);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6558,-4.71111);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6557,-4.71107);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6555,-4.7111);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6554,-4.71124);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6553,-4.71114);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6552,-4.71115);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6551,-4.71121);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6549,-4.71126);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6548,-4.71134);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6547,-4.71137);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6545,-4.71132);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6544,-4.71124);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6543,-4.71132);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6542,-4.71122);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6542,-4.7114);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6541,-4.71147);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6539,-4.71153);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6538,-4.71152);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6537,-4.71153);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6535,-4.71168);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6532,-4.71163);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6532,-4.71183);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6532,-4.71194);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6531,-4.71189);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.653,-4.71191);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6529,-4.71185);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6528,-4.71198);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6527,-4.71211);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6526,-4.71219);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6524,-4.7122);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6522,-4.71228);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6521,-4.71235);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6519,-4.71243);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6518,-4.71237);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6517,-4.71247);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6517,-4.71258);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6516,-4.71272);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6515,-4.71279);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6514,-4.71274);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6513,-4.71285);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6512,-4.71284);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6511,-4.71278);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6511,-4.71293);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.651,-4.71311);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6509,-4.71325);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6509,-4.71334);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6507,-4.71341);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6507,-4.71341);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6507,-4.71352);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6507,-4.71354);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6506,-4.71363);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6505,-4.71366);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6504,-4.7138);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6501,-4.71395);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.65,-4.71406);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6499,-4.71427);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6499,-4.7144);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6499,-4.71453);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6501,-4.71467);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6501,-4.71482);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.65,-4.71498);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6499,-4.71502);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6498,-4.71509);
        ruta.addCoordenada(coord);
        coord = new LatLng(41.6498,-4.7152);
        ruta.addCoordenada(coord);

        sql = "INSERT INTO " + TABLE_RUTA + " VALUES (null, \"" + ruta.getNombre() + "\", "
                + ruta.getTiempoUltimo() + " ," + ruta.getTiempoMejor() + ");";
        //Log.d("SQL", "Ejecutando SQL: " + sql);
        Log.d("TIEMPO", "ULTIMO: " + ruta.getTiempoUltimo() + "MEJOR" + ruta.getTiempoMejor());
        db.execSQL(sql);

        //Creamos una consulta SQL para obtener el nÃºmero de la ruta en la tabla de la base de
        // datos (columna autoincremental)
        sql = "SELECT " + KEY_RUTA_ID + " FROM " + TABLE_RUTA + " WHERE " + KEY_RUTA_NOMBRE +
                " LIKE '%" + ruta.getNombre() + "%';";
        //Log.d("SQL", "Ejecutando SQL: " + sql);
        cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        num_ruta = cursor.getInt(0);
        cursor.close();

        //Creamos un ArrayList para leer las coordenadas de la ruta
        coords = ruta.getCoordenadas();

        //Recorremos el ArrayList de coordenadas y las vamos introduciendo en la base de datos
        for (i = 0; i < ruta.getSize(); i++) {
            sql = "INSERT INTO " + TABLE_COORDENADA + " VALUES (null, " + coords.get(i).latitude
                    + ", " + coords.get(i).longitude + ", " + num_ruta + ");";
            //Log.d("SQL", "Ejecutando SQL: " + sql);
            db.execSQL(sql);
        }
    }

    /**
     * DEBUGGING
     * Método que lee la base de datos y muestra en el log la información de la misma, puede utilizarse
     *  para comprobar que la base de datos es como se desea, DEBUG.
     */
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