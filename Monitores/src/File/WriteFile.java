package File;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.ArrayList;
import monitores.Alerta;

public class WriteFile {

    public static ArrayList<Transaccion> read() {
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;
        ArrayList<Transaccion> trans = new ArrayList();
        try {
            archivo = new File("./transacciones/transacciones/Transacciones.txt");
            fr = new FileReader(archivo);
            br = new BufferedReader(fr);
            String linea;
            String[] l;
            String ts = null;
            int cantRows = 0;
            int cantRowsDia = 0;
            Long tam = br.lines().count();
            //archivo = new File("./Transacciones.txt");
            fr = new FileReader(archivo);
            br = new BufferedReader(fr);
            Transaccion t= null;
            String next;
            Integer j=1;
            linea = br.readLine();
            while (linea != null) {
                if(j == Integer.parseInt(tam.toString())){
                    String[] tableSpaces = linea.split(",");
                    for(int i=0; i< tableSpaces.length; i++){
                        String[] informacion = tableSpaces[i].split(";");
                        String nombre = informacion[0];
                        Date fecha = Date.valueOf(informacion[1]);
                        Integer rows = Integer.valueOf(informacion[2]);
                        Integer rowsD = Integer.valueOf(informacion[3]);
                        t= new Transaccion(fecha, rows, rowsD, nombre);
                        trans.add(t);
                    }
                }
               
                linea = br.readLine();
                j++;
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println(e.getMessage());
        }
        return trans;
    }
    public void write(String ax, String bx, String cx, String dx, String ex) {

        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter out = null;
        try {
            fw = new FileWriter("Alertas.txt", true);
            bw = new BufferedWriter(fw);
            out = new PrintWriter(bw);
            out.println(ax + ";" + bx + ";" + cx + ";" + dx + ";" + ex);

            out.close();
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        } finally {
            if (out != null) {
                out.close();
            } //exception handling left as an exercise for the reader
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }
            try {
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }
        }
    }

    public static ArrayList<Alerta> read1() {
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;
        ArrayList<Alerta> leido = new ArrayList();
        try {
            // Apertura del fichero y creacion de BufferedReader para poder
            // hacer una lectura comoda (disponer del metodo readLine()).
            archivo = new File("Alertas.txt");
            fr = new FileReader(archivo);
            br = new BufferedReader(fr);

            // Lectura del fichero
            String linea;
            String[] l;
            String nombre;
            String sql;
            String fecha;
            String memoria;
            Double tiempo;
            Alerta al ;
            while ((linea = br.readLine()) != null) {
                l = linea.split(";");
                al = new Alerta(l[0],l[1],l[2],l[3],Double.parseDouble(l[4]));
                leido.add(al);
            }
            return leido;
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return null;
    }
}
