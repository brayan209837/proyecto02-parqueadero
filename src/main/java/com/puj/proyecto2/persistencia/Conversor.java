package com.puj.proyecto2.persistencia;

import com.puj.proyecto2.modelo.Carro;
import com.puj.proyecto2.modelo.CarroElectrico;
import com.puj.proyecto2.modelo.Convenio;
import com.puj.proyecto2.modelo.Moto;
import com.puj.proyecto2.modelo.Vehiculo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class Conversor {

    public static LocalDate textoAFecha(String texto) {
        try {
            return LocalDate.parse(texto);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static LocalDateTime textoAFechaHora(String texto) {
        if (texto.length() != 16) {
            return null;
        }
        if (texto.charAt(10) != ' ') {
            return null;
        }
        try {
            return LocalDateTime.parse(texto.replace(" ", "T"));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static Vehiculo textoAVehiculo(String tipo, String placa) {
        if (tipo.equals("CARRO")) {
            return new Carro(placa);
        }
        if (tipo.equals("MOTO")) {
            return new Moto(placa);
        }
        if (tipo.equals("ELECTRICO")) {
            return new CarroElectrico(placa);
        }
        return null;
    }

    public static Convenio textoAConvenio(String texto) {
        if (texto.equals("NINGUNO")) {
            return Convenio.NINGUNO;
        }
        if (texto.equals("ALMACEN")) {
            return Convenio.ALMACEN;
        }
        if (texto.equals("RESTAURANTE")) {
            return Convenio.RESTAURANTE;
        }
        if (texto.equals("CINE")) {
            return Convenio.CINE;
        }
        return null;
    }
}
