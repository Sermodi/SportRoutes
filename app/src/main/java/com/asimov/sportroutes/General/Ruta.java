package com.asimov.sportroutes.General;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Esta clase contiene toda la información de una ruta, desde su nombre y todos los puntos que la
 *  comprenden hasta el tiempo de inicio y fin.
 * Implementa la clase Parcelable para poder ser enviada entre activities y ser guardada cuando se
 *  realice un giro de pantalla.
 *
 * Autor: antonio
 * Fecha: 12/11/15.
 */
public class Ruta implements Parcelable {
    private final String nombre;
    private final ArrayList<LatLng> coordenadas; /*Todos los puntos guardados por los q pasa la ruta.*/
    private long tiempoInicio;  /*Tiempo en milisegundos de inicio de la ruta.*/
    private long tiempoUltimo;
    private long tiempoMejor;

    public Ruta(String inombre){
        nombre = inombre;
        coordenadas = new ArrayList<>();
        tiempoMejor = Long.MAX_VALUE;
    }

    /**
     * Comprueba que el nombre no sea vacio, nulo o utilizado
     * @param context contexto de la aplicacion
     * @return false si el nombre es vacio o esta en la base de datos. true en caso contrario
     */
    public boolean compruebaNombre(Context context) {
        ManejadorBD m = new ManejadorBD(context);
        if (nombre == null ||
                nombre.equals("")||
                m.existeRuta(nombre)
                ){
            return false;
        }else{
            for (int i =0;i<nombre.length();i++){
                if(nombre.charAt(i) != 32){
                    return true;
                }

            }
            return false;
        }
    }

    private Ruta(Parcel in) {
        nombre = in.readString();
        coordenadas = in.createTypedArrayList(LatLng.CREATOR);
        //Es necesario recoger los tiempos en el mismo orden que se guardaron.
        tiempoUltimo = in.readLong();
        tiempoInicio = in.readLong();
        tiempoMejor = in.readLong();
    }

    public static final Creator<Ruta> CREATOR = new Creator<Ruta>() {
        @Override
        public Ruta createFromParcel(Parcel in) {
            return new Ruta(in);
        }

        @Override
        public Ruta[] newArray(int size) {
            return new Ruta[size];
        }
    };

    /**
     * Nombre Identificador para el usuario de la ruta.
     * @return un String con el nombre de la ruta.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Añade una coordenada al ArrayList de puntos guardados por los que pasa la ruta.
     * @param coord es la coordenada (LatLng) que se desea añadir a la ruta.
     */
    public void addCoordenada(LatLng coord){
        coordenadas.add(coord);
    }

    /**
     * DEBUGING
     * Método que imprime todos los puntos de la ruta por el logcat.
     */
    public void imprimeRuta() {
        for (int i =0;i<coordenadas.size();i++){
            Log.d("Coordenadas", ""+coordenadas.get(i));
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeTypedList(coordenadas);
        //Para elementos del mismo tipo es necesario escribirlos en orden para leerlos en el mismo.
        dest.writeLong(tiempoUltimo);
        dest.writeLong(tiempoInicio);
        dest.writeLong(tiempoMejor);
    }

    /**
     * Devuelve la cantidad de puntos guardados que tiene la ruta actualmente.
     * @return Int con la longitud del ArrayList de coordenadas.
     */
    public int getSize(){
        return coordenadas.size();
    }

    /**
     * Un conjunto con cada uno de los puntos guardados que tiene la ruta actualmente.
     * @return un ArrayList de LatLng
     */
    public ArrayList<LatLng> getCoordenadas() {
        return coordenadas;
    }

    /**
     * Dado un tiempo en milisegundos lo guarda como último tiempo de la ruta y si
     *  es menor que el mejor tiempo este es cambiado.
     * @param tiempo long con el tiempo en milisegundos.
     */
    public void setTiempo(long tiempo) {
        this.tiempoUltimo = tiempo;
        if (tiempoMejor>tiempo || tiempo == 0){
            this.tiempoMejor = tiempo;
        }
    }

    /**
     * Devuelve el último tiempo en el que se realizó la ruta (en segundos)
     * @return long con el último tiempo de la ruta en segundos.
     */
    public long getTiempoUltimo(){
        return tiempoUltimo;
    }

    /**
     * Devuelve el último tiempo en el que se realizó la ruta (en segundos) en un formato que
     * una persona podría leer sin hacer ningun cálculo
     * @return String con el último tiempo de la ruta en segundos.
     */
    public String getTiempoUltimoHumano(){
        String tiempo = "";
        long tiempAux = tiempoUltimo;
        long aux;
        if (tiempAux >= 3600) {//mayor de una hora
            aux = tiempAux/3600;
            tiempAux = tiempAux - aux * 3600;
            tiempo += aux + " h ";
        }
        if (tiempAux >= 60){ //mayor de un minuto
            aux = tiempAux/60;
            tiempAux = tiempAux - (aux * 60);
            tiempo += aux + " m ";
        }
        tiempo+= tiempAux + " s ";
        Log.d("DEBUG","El tiempoUltimo" + tiempoUltimo + " en Humano es: " + tiempo);
        return tiempo;
    }
    /**
     * Guarda el último tiempo calculado hasta ahora.
     * @param tiempoUltimo long con el último tiempo registrado.
     */
    public void setTiempoUltimo(long tiempoUltimo) {

        this.tiempoUltimo = tiempoUltimo;
    }

    /**
     * Devuelve el momento inicial en el que se ha empezado a capturar la ruta.
     * @return long con el tiempo en segundos.
     */
    public long getTiempoInicio() {
        return tiempoInicio;
    }

    /**
     * Almacena el valor del tiempo de inicio.
     * @param tiempoInicio long con el tiempo en milisegundos.
     */
    public void setTiempoInicio(long tiempoInicio) {
        this.tiempoInicio = tiempoInicio;
    }

    /**
     * Devuelve el mejor tiempo en el que se realizó la ruta, si existe.
      * @return 0 si el mejor tiempo no exite
     */
    public long getTiempoMejor() {
        return tiempoMejor;
    }

    /**
     * Devuelve el último tiempo en el que se realizó la ruta (en segundos) en un formato que
     * una persona podría leer sin hacer ningun cálculo
     * @return String con el último tiempo de la ruta en segundos.
     */
    public String getTiempoMejorHumano(){
        String tiempo = "";
        long tiempAux = tiempoMejor;
        long aux;
        if (tiempAux >= 3600) {//mayor de una hora
            aux = tiempAux/3600;
            tiempAux = tiempAux - aux * 3600;
            tiempo += aux + " h ";
        }
        if (tiempAux >= 60){ //mayor de un minuto
            aux = tiempAux/60;
            tiempAux = tiempAux - (aux * 60);
            tiempo += aux + " m ";
        }
        tiempo+= tiempAux + " s ";
        Log.d("DEBUG","El tiempoMejor" + tiempoMejor + "en Humano es: " + tiempo);
        return tiempo;
    }

    /**
     * Expresión del tiempo separada en 3 elementos, horas minutos y segundos, para que sea más
     *  comprensible
     * @return long[] con 3 elementos, ["horas","minutos", "segundos"]
     */
    public long[] tiempoUltimoToArray() {
        long[] t = new long[3];
        t[0] = tiempoUltimo / 3600;
        t[1] = (tiempoUltimo%3600)/60;
        t[2] = (tiempoUltimo%3600)%60;
        return t;
    }

    /**
     * Expresión del tiempo de forma que cualquier humano pueda entenderla.
     * @return un String con el formato HH horas MM minutos SS segundos, para mostrar al usuario.
     */
    private String tiempoUltimoHumano(){
        long[] t =  tiempoUltimoToArray();
        return (t[0])+ "h " + (t[1]) + "m " + (t[2]) + "s";
    }

    /**
     * Calcula la duración de la ruta en base al tiempo de inicio y de fin.
     */
    public void calculaDuracion() {
        setTiempo((tiempoUltimo - tiempoInicio) / 1000);
        Log.e("Duración", "La duración es:" + getTiempoUltimo());
    }

    @Override
    public String toString() {
        return nombre + " : " +  tiempoUltimoHumano();
    }


    public boolean alejado(LatLng nuevaCoord) {
        for (int i =0;i<coordenadas.size();i++){
            double d = Math.sqrt(Math.pow( (nuevaCoord.latitude - coordenadas.get(i).latitude) , 2 ) +
                            Math.pow( (nuevaCoord.longitude - coordenadas.get(i).longitude ) , 2 ));
            if(d<0.0001){
                return false;
            }
        }
        return true;

    }
}
