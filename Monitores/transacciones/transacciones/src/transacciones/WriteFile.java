/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transacciones;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.ArrayList;
import java.util.stream.Stream;

/**
 *
 * @author Admin
 */
public class WriteFile {

    public void write(ArrayList<Transaccion> today) {

        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter out = null;
        ArrayList<Transaccion> yesterday = read();
        try {
            fw = new FileWriter("Transacciones.txt", true);
            bw = new BufferedWriter(fw);
            out = new PrintWriter(bw);
            for (int i = 0; i < today.size(); i++) {
                //System.out.println(today.get(i).getCantRowsDia() + "" + yesterday.get(i).getCantRowsDia());
                Integer TD = (today.get(i).getCantRows() - yesterday.get(i).getCantRows());
                //TD = (TD == 0) ? TD =yesterday.get(i).getCantRowsDia() : TD;
                if ((i + 1) >= today.size()) {
                    out.print(today.get(i).getTablespace()
                            + ";" + today.get(i).getFecha()
                            + ";" + today.get(i).getCantRows()
                            + ";" + TD + '\n');
                } else {
                    out.print(today.get(i).getTablespace()
                            + ";" + today.get(i).getFecha()
                            + ";" + today.get(i).getCantRows()
                            + ";" + TD + ',');
                }
            }
            //out = new PrintWriter(bw);
            //out.print(t.getTablespace() + ";" + t.getFecha() + ";" + t.getCantRows() + ";" + t.getCantRowsDia() + ",");
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

    public static ArrayList<Transaccion> read() {
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;
        ArrayList<Transaccion> trans = new ArrayList();
        String linea;
        try {
            archivo = new File("./Transacciones.txt");
            fr = new FileReader(archivo);
            br = new BufferedReader(fr);
            Long tam = br.lines().count();
            fr = new FileReader(archivo);
            br = new BufferedReader(fr);
            String[] l;
            String ts = null;
            int cantRows = 0;
            int cantRowsDia = 0;
            Transaccion t;
            String next;
            Integer j = 1;
            linea = br.readLine();
            while (linea != null) {
                if (j == Integer.parseInt(tam.toString())) {
                    String[] tableSpaces = linea.split(",");
                    for (int i = 0; i < tableSpaces.length; i++) {
                        String[] informacion = tableSpaces[i].split(";");
                        String nombre = informacion[0];
                        Date fecha = Date.valueOf(informacion[1]);
                        Integer rows = Integer.valueOf(informacion[2]);
                        Integer rowsD = Integer.valueOf(informacion[3]);
                        t = new Transaccion(fecha, rows, rowsD, nombre);
                        trans.add(t);
                    }
                }
                linea = br.readLine();
                j++;
//                l = linea.split(";");
//                ts = l[0];
////                fecha = Date.valueOf(l[1]);
//                cantRows = Integer.valueOf(l[2]);
//                cantRowsDia = Integer.valueOf(l[3]);

                //System.out.print(t);
            }

        } catch (IOException | NumberFormatException e) {
            System.err.println(e.getMessage());
        }
        return trans;
    }
}
