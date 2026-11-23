package com.puj.proyecto2.modelo;

import java.io.Serializable;

public abstract class Vehiculo implements Serializable {

    private String placa;

    public Vehiculo(String placa) {
        this.placa = placa;
    }

    public String getPlaca() {
        return this.placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public abstract String getTipo();

    public abstract boolean placaValida();

    public abstract long calcularTarifa(long minutos);

    public long cargoAdicional(long minutos) {
        return 0;
    }

    protected boolean esLetraMayuscula(char c) {
        if (c >= 'A') {
            if (c <= 'Z') {
                return true;
            }
        }
        return false;
    }

    protected boolean esDigito(char c) {
        if (c >= '0') {
            if (c <= '9') {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return this.placa + " " + this.getTipo();
    }
}
