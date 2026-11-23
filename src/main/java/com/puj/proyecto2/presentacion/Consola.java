package com.puj.proyecto2.presentacion;

import com.puj.proyecto2.excepciones.ParqueaderoException;
import com.puj.proyecto2.modelo.Convenio;
import com.puj.proyecto2.modelo.Tiquete;
import com.puj.proyecto2.modelo.Vehiculo;
import com.puj.proyecto2.negocio.IParqueadero;
import com.puj.proyecto2.negocio.Parqueadero;
import com.puj.proyecto2.persistencia.Conversor;
import com.puj.proyecto2.persistencia.ManejoArchivos;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class Consola {

    private static final String ARCHIVO_SISTEMA = "sistema.dat";

    private IParqueadero sistema;
    private Scanner lector;

    public Consola() {
        this.sistema = new Parqueadero();
        this.lector = new Scanner(System.in);
    }

    public static void main(String[] args) {
        Consola consola = new Consola();
        consola.iniciar();
    }

    public void iniciar() {
        int opcion = -1;
        while (opcion != 0) {
            this.mostrarMenu();
            opcion = this.leerOpcion();

            if (opcion == 1) {
                this.cargarMensualidades();
            } else if (opcion == 2) {
                this.procesarMovimientos();
            } else if (opcion == 3) {
                this.registrarEntrada();
            } else if (opcion == 4) {
                this.registrarSalida();
            } else if (opcion == 5) {
                this.verVehiculosAdentro();
            } else if (opcion == 6) {
                this.cierreDeCaja();
            } else if (opcion == 7) {
                this.verPlacasFrecuentes();
            } else if (opcion == 8) {
                this.guardarSistema();
            } else if (opcion == 9) {
                this.cargarSistema();
            } else if (opcion == 0) {
                System.out.println("Hasta luego.");
            } else {
                System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
        this.lector.close();
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("--- Parqueadero Plaza Central ---");
        System.out.println("1. Cargar mensualidades");
        System.out.println("2. Procesar movimientos");
        System.out.println("3. Registrar una entrada");
        System.out.println("4. Registrar una salida");
        System.out.println("5. Ver los vehículos adentro");
        System.out.println("6. Cierre de caja de un día");
        System.out.println("7. Placas frecuentes");
        System.out.println("8. Guardar el sistema");
        System.out.println("9. Cargar el sistema");
        System.out.println("0. Salir");
    }

    private int leerOpcion() {
        String texto = this.pedir("> ");
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String pedir(String mensaje) {
        System.out.print(mensaje);
        return this.lector.nextLine().trim();
    }

    private void imprimir(List<String> lineas) {
        for (int i = 0; i < lineas.size(); i++) {
            System.out.println(lineas.get(i));
        }
    }

    private void cargarMensualidades() {
        String carpeta = this.pedir("Carpeta de los archivos: ");
        String ruta = carpeta + System.getProperty("file.separator") + "Mensualidades.txt";
        try {
            List<String> mensajes = ManejoArchivos.cargarMensualidades(ruta, this.sistema);
            this.imprimir(mensajes);
        } catch (IOException e) {
            System.out.println("No se pudo leer el archivo: " + e.getMessage());
        }
    }

    private void procesarMovimientos() {
        String carpeta = this.pedir("Carpeta de los archivos: ");
        String ruta = carpeta + System.getProperty("file.separator") + "Movimientos.txt";
        try {
            List<String> mensajes = ManejoArchivos.procesarMovimientos(ruta, this.sistema);
            this.imprimir(mensajes);
        } catch (IOException e) {
            System.out.println("No se pudo leer el archivo: " + e.getMessage());
        }
    }

    private void registrarEntrada() {
        String placa = this.pedir("Placa: ");
        String tipo = this.pedir("Tipo (CARRO, MOTO, ELECTRICO): ").toUpperCase();
        String texto = this.pedir("Fecha y hora (aaaa-mm-dd hh:mm): ");

        Vehiculo vehiculo = Conversor.textoAVehiculo(tipo, placa);
        LocalDateTime fecha = Conversor.textoAFechaHora(texto);

        if (vehiculo == null) {
            System.out.println("Tipo de vehículo desconocido: " + tipo);
        } else if (fecha == null) {
            System.out.println("Fecha y hora mal escritas. Ejemplo: 2026-03-02 07:15");
        } else {
            try {
                Tiquete tiquete = this.sistema.registrarEntrada(vehiculo, fecha);
                System.out.println("Entrada registrada:");
                System.out.println(tiquete.toString());
            } catch (ParqueaderoException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void registrarSalida() {
        String placa = this.pedir("Placa: ");
        String texto = this.pedir("Fecha y hora (aaaa-mm-dd hh:mm): ");
        String textoConvenio = this.pedir("Convenio (NINGUNO, ALMACEN, RESTAURANTE, CINE): ").toUpperCase();

        LocalDateTime fecha = Conversor.textoAFechaHora(texto);
        Convenio convenio = Conversor.textoAConvenio(textoConvenio);

        if (fecha == null) {
            System.out.println("Fecha y hora mal escritas. Ejemplo: 2026-03-02 09:40");
        } else if (convenio == null) {
            System.out.println("Convenio desconocido: " + textoConvenio);
        } else {
            try {
                Tiquete tiquete = this.sistema.registrarSalida(placa, fecha, convenio);
                System.out.println("Salida registrada:");
                System.out.println(tiquete.toString());
            } catch (ParqueaderoException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void verVehiculosAdentro() {
        List<String> lineas = this.sistema.reporteVehiculosAdentro();
        System.out.println("Vehículos adentro");
        if (lineas.size() == 0) {
            System.out.println("No hay vehículos adentro.");
        } else {
            this.imprimir(lineas);
        }
    }

    private void cierreDeCaja() {
        String texto = this.pedir("Fecha del cierre (aaaa-mm-dd): ");
        LocalDate fecha = Conversor.textoAFecha(texto);

        if (fecha == null) {
            System.out.println("Fecha mal escrita. Ejemplo: 2026-03-02");
        } else {
            List<String> lineas = this.sistema.reporteCierre(fecha);
            this.imprimir(lineas);

            String ruta = "Cierre_" + fecha + ".txt";
            try {
                ManejoArchivos.exportarReporte(lineas, ruta);
                System.out.println("Cierre exportado en el archivo " + ruta);
            } catch (IOException e) {
                System.out.println("No se pudo exportar el cierre: " + e.getMessage());
            }
        }
    }

    private void verPlacasFrecuentes() {
        List<String> lineas = this.sistema.reportePlacasFrecuentes();
        System.out.println("Placas frecuentes");
        if (lineas.size() == 0) {
            System.out.println("Todavía no hay tiquetes cerrados.");
        } else {
            this.imprimir(lineas);
        }
    }

    private void guardarSistema() {
        try {
            ManejoArchivos.guardarSistema(this.sistema, ARCHIVO_SISTEMA);
            System.out.println("Sistema guardado en " + ARCHIVO_SISTEMA);
        } catch (IOException e) {
            System.out.println("No se pudo guardar el sistema: " + e.getMessage());
        }
    }

    private void cargarSistema() {
        try {
            this.sistema = ManejoArchivos.cargarSistema(ARCHIVO_SISTEMA);
            System.out.println("Sistema cargado desde " + ARCHIVO_SISTEMA);
        } catch (IOException e) {
            System.out.println("No se pudo cargar el sistema: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("El archivo no tiene el formato esperado.");
        }
    }
}
