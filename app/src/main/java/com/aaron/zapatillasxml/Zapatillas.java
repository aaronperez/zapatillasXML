package com.aaron.zapatillasxml;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

/**
 * Created by Aaron Perez on 20/11/2014.
 */

public class Zapatillas implements Parcelable, Comparable<Zapatillas> {
    private String modelo, caract, peso;
    private int marca;

    public Zapatillas() {
    }

    public Zapatillas(String modelo, String caract, String peso, int marca) {
        this.modelo = modelo;
        this.caract = caract;
        this.peso = peso;
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getCaract() {
        return caract;
    }

    public void setCaract(String caract) {
        this.caract = caract;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public int getMarca() {
        return marca;
    }

    public void setMarca(int marca) {
        this.marca = marca;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Zapatillas)) return false;

        Zapatillas that = (Zapatillas) o;

        if (marca != that.marca) return false;
        if (!caract.equals(that.caract)) return false;
        if (!modelo.equals(that.modelo)) return false;
        if (!peso.equals(that.peso)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = modelo.hashCode();
        result = 31 * result + caract.hashCode();
        result = 31 * result + peso.hashCode();
        result = 31 * result + marca;
        return result;
    }

    @Override
    public int compareTo(Zapatillas zapatillas) {
        return this.getModelo().toLowerCase().compareTo(zapatillas.getModelo().toLowerCase());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private void readFromParcel(Parcel in) {
        modelo=in.readString();
        caract=in.readString();
        peso=in.readString();
        marca=in.readInt();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(modelo);
        dest.writeString(caract);
        dest.writeString(peso);
        dest.writeInt(marca);
    }
}
