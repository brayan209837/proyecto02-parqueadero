package com.puj.proyecto2.modelo;

import java.io.Serializable;
import java.time.LocalDate;

public class Mensualidad implements Serializable {

    private String placa;
    private LocalDate inicio;
    private LocalDate fin;

    public Mensualidad(String placa, LocalDate inicio, LocalDate fin) {
        this.placa = placa;
        this.inicio = inicio;
        this.fin = fin;
    }

    public String getPlaca() {
        return this.placa;
    }

    public LocalDate getInicio() {
        return this.inicio;
    }

    public LocalDate getFin() {
        return this.fin;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public void setInicio(LocalDate inicio) {
        this.inicio = inicio;
    }

    public void setFin(LocalDate fin) {
        this.fin = fin;
    }

    public boolean cubre(LocalDate fecha) {
        if (fecha.isBefore(this.inicio)) {
            return false;
        }
        if (fecha.isAfter(this.fin)) {
            return false;
        }
        return true;
    }

    public String toString() {
        return this.placa + " desde " + this.inicio + " hasta " + this.fin;
    }
}
