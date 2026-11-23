package com.puj.proyecto2.negocio;

import com.puj.proyecto2.excepciones.FechaInvalidaException;
import com.puj.proyecto2.excepciones.ParqueaderoException;
import com.puj.proyecto2.excepciones.PlacaInvalidaException;
import com.puj.proyecto2.excepciones.VehiculoNoEncontradoException;
import com.puj.proyecto2.excepciones.VehiculoYaAdentroException;
import com.puj.proyecto2.modelo.Convenio;
import com.puj.proyecto2.modelo.Mensualidad;
import com.puj.proyecto2.modelo.Tiquete;
import com.puj.proyecto2.modelo.Vehiculo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Parqueadero implements IParqueadero, Serializable {

    private int siguienteNumero = 1;
    private ArrayList<Tiquete> tiquetes;
    private ArrayList<Mensualidad> mensualidades;

    public Parqueadero() {
        this.siguienteNumero = 1;
        this.tiquetes = new ArrayList<Tiquete>();
        this.mensualidades = new ArrayList<Mensualidad>();
    }

    public void agregarMensualidad(Mensualidad m) throws ParqueaderoException {
        if (m.getFin().isBefore(m.getInicio())) {
            throw new FechaInvalidaException("La mensualidad de " + m.getPlaca()
                    + " termina el " + m.getFin() + ", antes de su inicio el " + m.getInicio());
        }
        this.mensualidades.add(m);
    }

    public Tiquete registrarEntrada(Vehiculo v, LocalDateTime entrada) throws ParqueaderoException {
        if (v.placaValida() == false) {
            throw new PlacaInvalidaException("La placa " + v.getPlaca()
                    + " no es valida para un vehiculo " + v.getTipo());
        }
        if (this.buscarAbierto(v.getPlaca()) != null) {
            throw new VehiculoYaAdentroException("El vehiculo " + v.getPlaca()
                    + " ya esta dentro del parqueadero");
        }

        Tiquete tiquete = new Tiquete(this.siguienteNumero, v, entrada);
        this.siguienteNumero++;
        this.tiquetes.add(tiquete);
        return tiquete;
    }

    public Tiquete registrarSalida(String placa, LocalDateTime salida, Convenio convenio)
            throws ParqueaderoException {
        Tiquete tiquete = this.buscarAbierto(placa);
        if (tiquete == null) {
            throw new VehiculoNoEncontradoException("No hay un tiquete abierto para la placa " + placa);
        }
        if (salida.isBefore(tiquete.getEntrada())) {
            throw new FechaInvalidaException("La salida de " + placa + " ("
                    + salida.toString().replace("T", " ") + ") es anterior a su entrada ("
                    + tiquete.getEntrada().toString().replace("T", " ") + ")");
        }

        boolean mensualidad = this.tieneMensualidad(placa, tiquete.getEntrada().toLocalDate());
        tiquete.cerrar(salida, convenio, mensualidad);
        return tiquete;
    }

    public List<String> reporteVehiculosAdentro() {
        ArrayList<Tiquete> abiertos = new ArrayList<Tiquete>();
        for (int i = 0; i < this.tiquetes.size(); i++) {
            Tiquete t = this.tiquetes.get(i);
            if (t.estaAbierto()) {
                abiertos.add(t);
            }
        }

        List<String> lineas = new ArrayList<String>();
        while (abiertos.size() > 0) {
            int primero = 0;
            for (int i = 1; i < abiertos.size(); i++) {
                if (this.entraAntes(abiertos.get(i), abiertos.get(primero))) {
                    primero = i;
                }
            }
            Tiquete t = abiertos.get(primero);
            lineas.add(t.getNumero() + " " + t.getVehiculo().getPlaca() + " "
                    + t.getVehiculo().getTipo() + " "
                    + t.getEntrada().toString().replace("T", " "));
            abiertos.remove(primero);
        }
        return lineas;
    }

    public List<String> reporteCierre(LocalDate fecha) {
        int cantidad = 0;
        long parqueo = 0;
        long recarga = 0;
        long total = 0;

        int cantidadCarros = 0;
        int cantidadMotos = 0;
        int cantidadElectricos = 0;
        long totalCarros = 0;
        long totalMotos = 0;
        long totalElectricos = 0;

        for (int i = 0; i < this.tiquetes.size(); i++) {
            Tiquete t = this.tiquetes.get(i);
            if (t.estaAbierto() == false) {
                if (t.getSalida().toLocalDate().equals(fecha)) {
                    cantidad++;
                    parqueo = parqueo + t.getValorParqueo();
                    recarga = recarga + t.getValorRecarga();
                    total = total + t.getTotal();

                    String tipo = t.getVehiculo().getTipo();
                    if (tipo.equals("CARRO")) {
                        cantidadCarros++;
                        totalCarros = totalCarros + t.getTotal();
                    } else if (tipo.equals("MOTO")) {
                        cantidadMotos++;
                        totalMotos = totalMotos + t.getTotal();
                    } else if (tipo.equals("ELECTRICO")) {
                        cantidadElectricos++;
                        totalElectricos = totalElectricos + t.getTotal();
                    }
                }
            }
        }

        List<String> lineas = new ArrayList<String>();
        lineas.add("CIERRE " + fecha);
        lineas.add("tiquetes=" + cantidad);
        lineas.add("parqueo=" + parqueo);
        lineas.add("recarga=" + recarga);
        lineas.add("total=" + total);
        lineas.add("CARRO tiquetes=" + cantidadCarros + " total=" + totalCarros);
        lineas.add("MOTO tiquetes=" + cantidadMotos + " total=" + totalMotos);
        lineas.add("ELECTRICO tiquetes=" + cantidadElectricos + " total=" + totalElectricos);
        return lineas;
    }

    public List<String> reportePlacasFrecuentes() {
        ArrayList<String> placas = new ArrayList<String>();
        for (int i = 0; i < this.tiquetes.size(); i++) {
            Tiquete t = this.tiquetes.get(i);
            if (t.estaAbierto() == false) {
                String placa = t.getVehiculo().getPlaca();
                if (placas.contains(placa) == false) {
                    placas.add(placa);
                }
            }
        }

        List<String> lineas = new ArrayList<String>();
        for (int puesto = 1; puesto <= 3; puesto++) {
            if (placas.size() > 0) {
                int mejor = 0;
                for (int i = 1; i < placas.size(); i++) {
                    if (this.esMejorPlaca(placas.get(i), placas.get(mejor))) {
                        mejor = i;
                    }
                }
                String placa = placas.get(mejor);
                lineas.add(puesto + ". " + placa + " visitas=" + this.contarVisitas(placa)
                        + " pagado=" + this.sumarPagado(placa));
                placas.remove(mejor);
            }
        }
        return lineas;
    }

    public List<Tiquete> getTiquetes() {
        return this.tiquetes;
    }

    private Tiquete buscarAbierto(String placa) {
        for (int i = 0; i < this.tiquetes.size(); i++) {
            Tiquete t = this.tiquetes.get(i);
            if (t.estaAbierto()) {
                if (t.getVehiculo().getPlaca().equals(placa)) {
                    return t;
                }
            }
        }
        return null;
    }

    private boolean tieneMensualidad(String placa, LocalDate fecha) {
        for (int i = 0; i < this.mensualidades.size(); i++) {
            Mensualidad m = this.mensualidades.get(i);
            if (m.getPlaca().equals(placa)) {
                if (m.cubre(fecha)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean entraAntes(Tiquete a, Tiquete b) {
        if (a.getEntrada().isBefore(b.getEntrada())) {
            return true;
        }
        if (a.getEntrada().isAfter(b.getEntrada())) {
            return false;
        }
        return a.getVehiculo().getPlaca().compareTo(b.getVehiculo().getPlaca()) < 0;
    }

    private int contarVisitas(String placa) {
        int visitas = 0;
        for (int i = 0; i < this.tiquetes.size(); i++) {
            Tiquete t = this.tiquetes.get(i);
            if (t.estaAbierto() == false) {
                if (t.getVehiculo().getPlaca().equals(placa)) {
                    visitas++;
                }
            }
        }
        return visitas;
    }

    private long sumarPagado(String placa) {
        long pagado = 0;
        for (int i = 0; i < this.tiquetes.size(); i++) {
            Tiquete t = this.tiquetes.get(i);
            if (t.estaAbierto() == false) {
                if (t.getVehiculo().getPlaca().equals(placa)) {
                    pagado = pagado + t.getTotal();
                }
            }
        }
        return pagado;
    }

    private boolean esMejorPlaca(String a, String b) {
        int visitasA = this.contarVisitas(a);
        int visitasB = this.contarVisitas(b);
        if (visitasA > visitasB) {
            return true;
        }
        if (visitasA < visitasB) {
            return false;
        }
        long pagadoA = this.sumarPagado(a);
        long pagadoB = this.sumarPagado(b);
        if (pagadoA > pagadoB) {
            return true;
        }
        if (pagadoA < pagadoB) {
            return false;
        }
        return a.compareTo(b) < 0;
    }
}
