package com.puj.proyecto2.negocio;

import com.puj.proyecto2.excepciones.ParqueaderoException;
import com.puj.proyecto2.modelo.Convenio;
import com.puj.proyecto2.modelo.Mensualidad;
import com.puj.proyecto2.modelo.Tiquete;
import com.puj.proyecto2.modelo.Vehiculo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IParqueadero {

    void agregarMensualidad(Mensualidad m) throws ParqueaderoException;

    Tiquete registrarEntrada(Vehiculo v, LocalDateTime entrada) throws ParqueaderoException;

    Tiquete registrarSalida(String placa, LocalDateTime salida, Convenio convenio)
            throws ParqueaderoException;

    List<String> reporteVehiculosAdentro();

    List<String> reporteCierre(LocalDate fecha);

    List<String> reportePlacasFrecuentes();

    List<Tiquete> getTiquetes();
}
