package com.puj.proyecto2.persistencia;

import com.puj.proyecto2.excepciones.ParqueaderoException;
import com.puj.proyecto2.modelo.Convenio;
import com.puj.proyecto2.modelo.Mensualidad;
import com.puj.proyecto2.modelo.Vehiculo;
import com.puj.proyecto2.negocio.IParqueadero;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ManejoArchivos {

    public static List<String> cargarMensualidades(String ruta, IParqueadero sistema)
            throws IOException {
        List<String> mensajes = new ArrayList<String>();
        int cargadas = 0;
        int rechazadas = 0;

        FileReader archivo = new FileReader(ruta);
        BufferedReader lector = new BufferedReader(archivo);

        int numeroLinea = 0;
        String linea = lector.readLine();
        while (linea != null) {
            numeroLinea++;
            String texto = linea.trim();
            if (texto.length() > 0) {
                if (texto.startsWith("#") == false) {
                    String error = procesarMensualidad(texto, sistema);
                    if (error == null) {
                        cargadas++;
                    } else {
                        rechazadas++;
                        mensajes.add("Línea " + numeroLinea + ": " + error);
                    }
                }
            }
            linea = lector.readLine();
        }
        lector.close();

        mensajes.add("Mensualidades cargadas: " + cargadas + " | rechazadas: " + rechazadas);
        return mensajes;
    }

    public static List<String> procesarMovimientos(String ruta, IParqueadero sistema)
            throws IOException {
        List<String> mensajes = new ArrayList<String>();
        int aplicados = 0;
        int rechazados = 0;

        FileReader archivo = new FileReader(ruta);
        BufferedReader lector = new BufferedReader(archivo);

        int numeroLinea = 0;
        String linea = lector.readLine();
        while (linea != null) {
            numeroLinea++;
            String texto = linea.trim();
            if (texto.length() > 0) {
                if (texto.startsWith("#") == false) {
                    String error = procesarMovimiento(texto, sistema);
                    if (error == null) {
                        aplicados++;
                    } else {
                        rechazados++;
                        mensajes.add("Línea " + numeroLinea + ": " + error);
                    }
                }
            }
            linea = lector.readLine();
        }
        lector.close();

        mensajes.add("Movimientos aplicados: " + aplicados + " | rechazados: " + rechazados);
        return mensajes;
    }

    public static void exportarReporte(List<String> lineas, String ruta) throws IOException {
        FileWriter archivo = new FileWriter(ruta);
        BufferedWriter escritor = new BufferedWriter(archivo);
        for (int i = 0; i < lineas.size(); i++) {
            escritor.write(lineas.get(i));
            escritor.newLine();
        }
        escritor.close();
    }

    public static void guardarSistema(IParqueadero sistema, String ruta) throws IOException {
        FileOutputStream archivo = new FileOutputStream(ruta);
        ObjectOutputStream salida = new ObjectOutputStream(archivo);
        salida.writeObject(sistema);
        salida.close();
    }

    public static IParqueadero cargarSistema(String ruta) throws IOException, ClassNotFoundException {
        FileInputStream archivo = new FileInputStream(ruta);
        ObjectInputStream entrada = new ObjectInputStream(archivo);
        IParqueadero sistema = (IParqueadero) entrada.readObject();
        entrada.close();
        return sistema;
    }

    private static ArrayList<String> separar(String linea) {
        ArrayList<String> campos = new ArrayList<String>();
        String actual = "";
        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);
            if (c == '*') {
                campos.add(actual.trim());
                actual = "";
            } else {
                actual = actual + c;
            }
        }
        campos.add(actual.trim());
        return campos;
    }

    private static String procesarMensualidad(String texto, IParqueadero sistema) {
        ArrayList<String> campos = separar(texto);
        if (campos.size() != 3) {
            return "Campos incorrectos: una mensualidad lleva tres (placa*inicio*fin)";
        }

        String placa = campos.get(0);
        LocalDate inicio = Conversor.textoAFecha(campos.get(1));
        LocalDate fin = Conversor.textoAFecha(campos.get(2));

        if (placa.length() == 0) {
            return "La placa está vacía";
        }
        if (inicio == null) {
            return "Fecha de inicio mal escrita: " + campos.get(1);
        }
        if (fin == null) {
            return "Fecha de fin mal escrita: " + campos.get(2);
        }

        try {
            sistema.agregarMensualidad(new Mensualidad(placa, inicio, fin));
            return null;
        } catch (ParqueaderoException e) {
            return e.getMessage();
        }
    }

    private static String procesarMovimiento(String texto, IParqueadero sistema) {
        ArrayList<String> campos = separar(texto);
        String tipoMovimiento = campos.get(0);

        if (tipoMovimiento.equals("E")) {
            return procesarEntrada(campos, sistema);
        }
        if (tipoMovimiento.equals("S")) {
            return procesarSalida(campos, sistema);
        }
        return "Movimiento desconocido: " + tipoMovimiento + " (solo hay E y S)";
    }

    private static String procesarEntrada(ArrayList<String> campos, IParqueadero sistema) {
        if (campos.size() != 4) {
            return "Campos incorrectos: una entrada lleva cuatro (E*placa*tipo*fecha hora)";
        }

        String placa = campos.get(1);
        String tipo = campos.get(2);
        Vehiculo vehiculo = Conversor.textoAVehiculo(tipo, placa);
        LocalDateTime fechaHora = Conversor.textoAFechaHora(campos.get(3));

        if (vehiculo == null) {
            return "Tipo de vehículo desconocido: " + tipo;
        }
        if (fechaHora == null) {
            return "Fecha y hora mal escritas: " + campos.get(3);
        }

        try {
            sistema.registrarEntrada(vehiculo, fechaHora);
            return null;
        } catch (ParqueaderoException e) {
            return e.getMessage();
        }
    }

    private static String procesarSalida(ArrayList<String> campos, IParqueadero sistema) {
        if (campos.size() != 4) {
            return "Campos incorrectos: una salida lleva cuatro (S*placa*fecha hora*convenio)";
        }

        String placa = campos.get(1);
        LocalDateTime fechaHora = Conversor.textoAFechaHora(campos.get(2));
        Convenio convenio = Conversor.textoAConvenio(campos.get(3));

        if (fechaHora == null) {
            return "Fecha y hora mal escritas: " + campos.get(2);
        }
        if (convenio == null) {
            return "Convenio desconocido: " + campos.get(3);
        }

        try {
            sistema.registrarSalida(placa, fechaHora, convenio);
            return null;
        } catch (ParqueaderoException e) {
            return e.getMessage();
        }
    }
}
