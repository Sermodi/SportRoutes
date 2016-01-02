package com.asimov.sportroutes.General.Clima;

import android.graphics.drawable.Drawable;

/**
 * Descripción:
 */
public class Clima {
    public final CurrentCondition currentCondition = new CurrentCondition();
    public final Temperature temperature = new Temperature();
    public final Localizacion localizacion = new Localizacion();

    public Drawable iconDrawable;

    public class Localizacion{
        private String ciudad;
        private String pais;

        public String getCiudad() {
            return ciudad;
        }

        public void setCiudad(String ciudad) {
            this.ciudad = ciudad;
        }

        public String getPais() {
            return pais;
        }

        public void setPais(String pais) {
            this.pais = pais;
        }
    }

    public  class CurrentCondition {
        private String icon;

        public String getIcon() {
            return icon;
        }
        public void setIcon(String icon) {
            this.icon = icon;
        }

    }

    public  class Temperature {
        private float temp;

        public float getTemp() {
            return temp;
        }
        public void setTemp(float temp) {
            this.temp = temp;
        }

    }


}
