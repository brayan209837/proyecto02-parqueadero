package com.puj.proyecto2.modelo;

public class Moto extends Vehiculo {

    private static final long TARIFA_MINUTO = 80;

    public Moto(String placa) {
        super(placa);
    }

    public String getTipo() {
        return "MOTO";
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
        for (int i = 3; i < 5; i++) {
            if (this.esDigito(placa.charAt(i)) == false) {
                return false;
            }
        }
        if (this.esLetraMayuscula(placa.charAt(5)) == false) {
            return false;
        }
        return true;
    }

    public long calcularTarifa(long minutos) {
        return minutos * TARIFA_MINUTO;
    }
}
