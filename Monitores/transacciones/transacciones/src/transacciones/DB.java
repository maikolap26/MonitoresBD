/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transacciones;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

/**
 *
 * @author jimen
 */
public class DB {

    private Connection conexion;
    public final double HWT = 0.80;
    public int NumTs;
    public int temp;

    public Connection getConexion() {
        return conexion;
    }

    public void setConexion(Connection conexion) {
        this.conexion = conexion;
    }

    public DB Conectar() {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            Properties info = new Properties();
            info.setProperty("user", "maikol");
            info.setProperty("password", "maikol");
            conexion = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", info);
            if (conexion != null) {
                System.out.println("Conexion exitosa a esquema HR");
            } else {
                System.out.println("Conexion fallida");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this;
    }

    public void query(String query) {
        Statement st;
        ResultSet rs;
        Conectar();
        CallableStatement cs = null;
        try {
            st = conexion.createStatement();
            boolean execute = st.execute(query);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public Transaccion getTran(String name) {
        Statement ts;
        ResultSet rs;
        Conectar();
        CallableStatement cs = null;
        Transaccion t = null;
        try {
            ts = conexion.createStatement();
            rs = ts.executeQuery("select sysdate, sum(num_rows),tablespace_name from all_tables where tablespace_name=" + "'" + name + "'" + "group by tablespace_name");
            rs.next();
            Date date = rs.getDate(1);
            int cant = rs.getInt(2);
            String tablespace = rs.getString(3);
            System.out.println(cant);
            System.out.println(date);
            conexion.close();
            t = new Transaccion(date, cant, 0, tablespace);
        } catch (SQLException e) {
            System.err.print(e.getMessage());
        }
        return t;
    }

    public void guardar() {
        Statement ts;
        ResultSet rs;

        Conectar();
        CallableStatement cs = null;
        try {
            ts = conexion.createStatement();
            rs = ts.executeQuery("select tablespace_name from dba_tablespaces order by(tablespace_name)");
            WriteFile wf = new WriteFile();
            ArrayList<Transaccion> t= new ArrayList();
            while (rs.next()) {
                if (!rs.getString(1).equals("TEMP") && !rs.getString(1).equals("UNDOTBS1")) {
                    t.add(this.getTran(rs.getString(1)));
                }
            }
            wf.write(t);
            conexion.close();
        } catch (SQLException e) {
            System.err.print(e.getMessage());
        }
    }
}
