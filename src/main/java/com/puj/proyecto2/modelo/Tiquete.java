package com.puj.proyecto2.modelo;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

public class Tiquete implements Serializable {

    private int numero;
    private Vehiculo vehiculo;
    private LocalDateTime entrada;
    private LocalDateTime salida;
    private Convenio convenio;
    private long valorParqueo;
    private long valorRecarga;

    public Tiquete(int numero, Vehiculo vehiculo, LocalDateTime entrada) {
        this.numero = numero;
        this.vehiculo = vehiculo;
        this.entrada = entrada;
        this.salida = null;
        this.convenio = null;
        this.valorParqueo = 0;
        this.valorRecarga = 0;
    }

    public int getNumero() {
        return this.numero;
    }

    public Vehiculo getVehiculo() {
        return this.vehiculo;
    }

    public LocalDateTime getEntrada() {
        return this.entrada;
    }

    public LocalDateTime getSalida() {
        return this.salida;
    }

    public Convenio getConvenio() {
        return this.convenio;
    }

    public long getValorParqueo() {
        return this.valorParqueo;
    }

    public long getValorRecarga() {
        return this.valorRecarga;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public void setEntrada(LocalDateTime entrada) {
        this.entrada = entrada;
    }

    public void setSalida(LocalDateTime salida) {
        this.salida = salida;
    }

    public void setConvenio(Convenio convenio) {
        this.convenio = convenio;
    }

    public void setValorParqueo(long valorParqueo) {
        this.valorParqueo = valorParqueo;
    }

    public void setValorRecarga(long valorRecarga) {
        this.valorRecarga = valorRecarga;
    }

    public boolean estaAbierto() {
        return this.salida == null;
    }

    public long getMinutos() {
        if (this.salida == null) {
            return 0;
        }
        return Duration.between(this.entrada, this.salida).toMinutes();
    }

    public void cerrar(LocalDateTime salida, Convenio convenio, boolean mensualidad) {
        this.salida = salida;
        this.convenio = convenio;

        long minutos = this.getMinutos();
        long cobrables = minutos - convenio.getMinutosGratis();
        if (cobrables < 0) {
            cobrables = 0;
        }

        long parqueo = 0;
        if (mensualidad == false) {
            parqueo = this.vehiculo.calcularTarifa(cobrables);
        }

        this.valorParqueo = parqueo;
        this.valorRecarga = this.vehiculo.cargoAdicional(minutos);
    }

    public long getTotal() {
        return this.valorParqueo + this.valorRecarga;
    }

    public String toString() {
        String texto = "Tiquete " + this.numero + " | " + this.vehiculo.toString()
                + " | Entrada: " + this.entrada.toString().replace("T", " ");
        if (this.estaAbierto()) {
            texto = texto + " | Abierto";
        } else {
            texto = texto + " | Salida: " + this.salida.toString().replace("T", " ")
                    + " | Convenio: " + this.convenio
                    + " | Minutos: " + this.getMinutos()
                    + " | Parqueo: " + this.valorParqueo
                    + " | Recarga: " + this.valorRecarga
                    + " | Total: " + this.getTotal();
        }
        return texto;
    }
}
