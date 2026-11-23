package com.puj.proyecto2.modelo;

public class Carro extends Vehiculo {

    private static final long TARIFA_MINUTO = 120;

    public Carro(String placa) {
        super(placa);
    }

    public String getTipo() {
        return "CARRO";
    }

    public boolean placaValida() {
        String placa = this.getPlaca();
        if (placa.length() != 6) {
            return false;
        }
        for (int i = 0; i < 3; i++) {
            if (this.esLetraMayuscula(placa.charAt(i)) == false) {
                return false;
            }
        }
        for (int i = 3; i < 6; i++) {
            if (this.esDigito(placa.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    public long calcularTarifa(long minutos) {
        return minutos * TARIFA_MINUTO;
    }
}
