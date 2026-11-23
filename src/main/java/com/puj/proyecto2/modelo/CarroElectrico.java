package com.puj.proyecto2.modelo;

public class CarroElectrico extends Carro implements Recargable {

    public CarroElectrico(String placa) {
        super(placa);
    }

    public String getTipo() {
        return "ELECTRICO";
    }

    public long calcularTarifa(long minutos) {
        return super.calcularTarifa(minutos) * 90 / 100;
    }

    public long costoRecarga(long minutos) {
        return minutos * COSTO_MINUTO;
    }

    public long cargoAdicional(long minutos) {
        return this.costoRecarga(minutos);
    }
}
