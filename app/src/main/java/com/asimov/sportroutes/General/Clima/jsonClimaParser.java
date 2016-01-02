package com.asimov.sportroutes.General.Clima;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Descripción:
 * Autor: Sergio.
 * Fecha de creación: 02/01/2016.
 */
public class jsonClimaParser {
    public static Clima getWeather(String data) throws JSONException {
        Clima clima = new Clima();

        // Se crea el objeto JSON de los datos
        JSONObject jObj = new JSONObject(data);

        // Extraemos la información de ciudad y país.

        JSONObject sysObj = getObject("sys", jObj);
        clima.localizacion.setPais(getString("country", sysObj));
        clima.localizacion.setCiudad(getString("name", jObj));

        // Obtenemos la información del clima.
        JSONArray jArr = jObj.getJSONArray("weather");

        // Solo recogemos el primer valor, que será el día actual.
        JSONObject JSONWeather = jArr.getJSONObject(0);
        clima.currentCondition.setIcon(getString("icon", JSONWeather));

        JSONObject mainObj = getObject("main", jObj);
        clima.temperature.setTemp(getFloat(mainObj));

        // El icono a mostrar se descargará.


        return clima;
    }


    private static JSONObject getObject(String tagName, JSONObject jObj)  throws JSONException {
        return jObj.getJSONObject(tagName);
    }

    private static String getString(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getString(tagName);
    }

    private static float  getFloat(JSONObject jObj) throws JSONException {
        return (float) jObj.getDouble("temp");
    }

}
