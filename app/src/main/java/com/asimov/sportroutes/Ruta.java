package com.asimov.sportroutes;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by antonio on 12/11/15.
 *
 * Esta clase contiene toda la información de una ruta, desde su nombre y todos los puntos que la
 *  comprenden hasta el tiempo de inicio y fin.
 * Implementa la clase Parcelable para poder ser enviada entre activities y ser guardada cuando se
 *  realice un giro de pantalla.
 */
public class Ruta implements Parcelable {
    private String nombre;
    private ArrayList<LatLng> coordenadas; /*Todos los puntos guardados por los q pasa la ruta.*/
    private long tiempoInicio;  /*Tiempo en milisegundos de inicio de la ruta.*/
    private long tiempoUltimo;
    private long tiempoMejor;

    public Ruta(String inombre){
        nombre = inombre;
        coordenadas = new ArrayList<>();
        tiempoMejor = Long.MAX_VALUE;


    }

    protected Ruta(Parcel in) {
        nombre = in.readString();
        coordenadas = in.createTypedArrayList(LatLng.CREATOR);
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

    public void almacena(){
        //TODO
        //pendiente del subsistema de persistencia
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
        if (tiempoMejor>tiempo){
            this.tiempoMejor = tiempo;
        }
    }

    /**
     * Devuelve el último tiempo en el que se realizó la ruta (en milisegundos)
     * @return long con el último tiempo de la ruta en milisegundos.
     */
    public long getTiempoUltimo(){
        return tiempoUltimo;
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
     * @return long con el tiempo en milisegundos.
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
     * Expresión del tiempo de forma que cualquier humano pueda entenderla.
     * @return un String con el formato HH horas MM minutos SS segundos, para mostrar al usuario.
     */
    public long[] tiempoUltimoHumano() {
        long[] t = new long[3];
        t[0] = tiempoUltimo / 3600;
        t[1] = (tiempoUltimo%3600)/60;
        t[2] = (tiempoUltimo%3600)%60;
        return t;
    }

    /**
     * Calcula la duración de la ruta en base al tiempo de inicio y de fin.
     */
    public void calculaDuracion() {
        setTiempo((tiempoUltimo - tiempoInicio) / 1000);
    }
}
