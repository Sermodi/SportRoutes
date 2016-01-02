package com.asimov.sportroutes.General.Clima;

import android.graphics.drawable.Drawable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Descripción: Genera las peticiones HTTP al API del clima para obtener un JSON con datos de interés.
 *
 * Autor: Sergio.
 * Fecha de creación: 02/01/2016.
 */
public class ClimaClienteHttp {

    public String getClima(String latitud, String longitud) {
        HttpURLConnection con = null ;
        InputStream is = null;

        try {
            //La url será del estilo a = http://api.openweathermap.org/data/2.5/forecast/daily?lat=41.652302&lon=-4.72854&cnt=1&mode=xml&appid=2de143494c0b295cca9337e1e96b00e0
            String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?";
            String KEY = "appid=a9922a0e88e32286b09fe75d8eb4c85e";
            String UNITS = "units=metric";
            String MODE = "mode=json";
            String LAT = "lat=";
            String LON = "lon=";
            String AND = "&";
            con = (HttpURLConnection) ( new URL(BASE_URL + LAT + latitud + AND +
                    LON + longitud + AND +
                    UNITS + AND +
                    MODE + AND +
                    KEY)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            // Let's read the response
            StringBuilder buffer = new StringBuilder();
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while (  (line = br.readLine()) != null )
                buffer.append(line).append("\r\n");

            is.close();
            con.disconnect();
            return buffer.toString();
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
        finally {
            try {
                assert is != null;
                is.close(); } catch(Throwable ignored) {}
            try {
                assert con != null;
                con.disconnect(); } catch(Throwable ignored) {}
        }

        return null;

    }

    public Drawable getImagen(String code) {
        InputStream is;
        try {
            String IMG_URL = "http://openweathermap.org/img/w/";
            is = (InputStream) new URL(IMG_URL + code + ".png").getContent();
            return Drawable.createFromStream(is, "src name");
        } catch (Exception e) {
            return null;
        }
    }
}
