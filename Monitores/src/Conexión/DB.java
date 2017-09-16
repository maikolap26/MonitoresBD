/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Conexión;

import File.Transaccion;
import File.WriteFile;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import oracle.jdbc.OracleTypes;

public class DB {

    private Connection conexion;
    public double HWT = 0.80;
    public String actual = "";
    public int NumTs;
    public int temp;
    public ArrayList<Object[]> freeMem = new ArrayList<>();

    public double getHWT() {
        return HWT;
    }

    public void setHWT(double HWT) {
        this.HWT = HWT / 100;
    }

    public Connection getConexion() {
        //HWT = 0.80;
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
            //info.setProperty("internal_logon","sysdba");
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
            boolean execute = st.execute(query); // = conexion.prepareCall(query);
            //cs.execute();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public String[] getTableSpace_name() {
        Statement ts;
        ResultSet rs;
        int size = getNumTS();
        String[] names = new String[size];
        Conectar();
        CallableStatement cs = null;
        try {
            ts = conexion.createStatement();
            rs = ts.executeQuery("select tablespace_name from dba_tablespaces order by(tablespace_name)");

            int i = 0;
            while (rs.next()) {
                names[i] = rs.getString("tablespace_name");
                if ("TEMP".equals(names[i])) {
                    temp = i;
                }
                System.out.println(rs.getString("tablespace_name"));
                i++;
            }
            conexion.close();
            return names;
        } catch (SQLException e) {
            System.err.print(e.getMessage());
        }
        return null;
    }

    public int getNumTS() {
        Statement ts;
        ResultSet rs;
        Conectar();
        CallableStatement cs = null;
        try {
            ts = conexion.createStatement();
            rs = ts.executeQuery("SELECT COUNT(tablespace_name) ax FROM dba_tablespaces");
            rs.next();
            NumTs = rs.getInt("ax");
            conexion.close();
            return NumTs;
        } catch (SQLException e) {
            System.err.print(e.getMessage());
        }
        return 0;
    }

    public ArrayList<Object[]> getDays(Double change) {
        Statement ts;
        ResultSet rs;
        Conectar();
        CallableStatement cs = null;
        Object[][] ob;
        ArrayList<Object[]> array = new ArrayList();
        for (int i = 0; i < this.freeMem.size(); i++) {
            Object[] obj = this.freeMem.get(i);
            String nombre = obj[0].toString();
            String libre = obj[1].toString();
            String blue = obj[2].toString();
            Object[] result = new Object[3];
            String tpd = "";
            ArrayList<Transaccion> trans = WriteFile.read();
            for (Transaccion t : trans) {
                tpd = (t.getTablespace().equals(nombre)) ? String.valueOf(t.getCantRowsDia()) : tpd;
            }
            try {
                ts = conexion.createStatement();
                Double libreDays = (Double.valueOf(libre) * 1024 * 1024);
                String sql = "select (Monitor('%s',%f,%f)) ax from dual";
                sql = String.format(sql, nombre, Float.valueOf(tpd), Float.valueOf(libreDays.toString()));
                rs = ts.executeQuery(sql);
                rs.next();
                result[2] = Math.ceil(rs.getDouble(1));
                if (!blue.equals("0")) {
                    if (change == -1.0) {
                        sql = "select (Monitor('%s',%f,%f)) from dual";
                        sql = String.format(sql, nombre, Float.valueOf(tpd), (Float.valueOf(blue) * 1024 * 1024));
                        rs = ts.executeQuery(sql);

                    } else {
                        sql = "select (Monitor('%s',%f,%f)) from dual";
                        sql = String.format(sql, nombre, Float.valueOf(tpd), Float.valueOf(change.toString()));
                        rs = ts.executeQuery(sql);
                    }
                    rs.next();
                    result[1] = Math.ceil(rs.getDouble(1));
                } else {
                    result[1] = 0.0;
                }

            } catch (Exception e) {
                System.err.println(e.getMessage());
                continue;
            }
            result[0] = nombre;
            array.add(result);
        }

        try {

            conexion.close();
        } catch (SQLException e) {
            System.err.print(e.getMessage());
        }
        return array;
    }

    public double[][] getSize() {
        if (!freeMem.isEmpty()) {
            freeMem = new ArrayList();
        }

        ResultSet rs;
        ResultSet rs1;
        Conectar();
        CallableStatement cs = null;
        CallableStatement cs1 = null;
        try {
            String sql = "{call getMemory(?)}";
            String sql1 = "{call Getmemorytemp(?)}";
            cs = conexion.prepareCall(sql);
            cs1 = conexion.prepareCall(sql1);
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs1.registerOutParameter(1, OracleTypes.CURSOR);
            cs.executeQuery();
            cs1.executeQuery();
            rs = (ResultSet) cs.getObject(1);
            rs1 = (ResultSet) cs1.getObject(1);

            double[][] data = new double[][]{
                new double[NumTs],
                new double[NumTs],
                new double[NumTs],};
            for (int i = 0; i < 5; i++) {
                if (i == temp) {
                    rs1.next();
                    double usado = rs1.getDouble(2) - rs1.getDouble(3);
                    data[0][i] = usado;
                    double topeA1 = 0;

                    topeA1 = rs1.getDouble(2) * HWT;

                    if (usado > topeA1) {
                        data[2][i] = rs1.getDouble(2) - usado;
                    } else {
                        double a = rs1.getDouble(2) - topeA1;
                        data[1][i] = rs1.getDouble(3) - a;
                        data[2][i] = a;
                    }
                } else {
                    rs.next();
                    data[0][i] = rs.getDouble(2);
                    double topeA = 0;

                    topeA = rs.getDouble(4) * HWT;

                    if (rs.getDouble(2) > topeA) {

                        data[2][i] = rs.getDouble(4) - rs.getDouble(2);

                        Object[] a11 = new Object[3];
                        a11[0] = (rs.getString(1));
                        a11[1] = (String.valueOf(rs.getDouble(3)));
                        a11[2] = ("0");
                        freeMem.add(a11);
                    } else {
                        double a = rs.getDouble(4) - topeA; // rojo
                        data[1][i] = rs.getDouble(3) - a;
                        Object[] a11 = new Object[3];
                        a11[0] = (rs.getString(1));
                        a11[1] = (rs.getDouble(3));//total libre
                        a11[2] = (rs.getDouble(3) - a); // azul
                        freeMem.add(a11);
                        data[2][i] = a;
                    }
                }
            }
            getDays(-1.0);
            return data;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public double[][] getSizeTS(String name) {
        if (!freeMem.isEmpty()) {
            freeMem = new ArrayList();
        }

        ResultSet rs;
        ResultSet rs1;
        Conectar();
        CallableStatement cs = null;
        CallableStatement cs1 = null;
        try {
            String sql = "{call getMemory(?)}";
            String sql1 = "{call Getmemorytemp(?)}";
            cs = conexion.prepareCall(sql);
            cs1 = conexion.prepareCall(sql1);
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs1.registerOutParameter(1, OracleTypes.CURSOR);
            cs.executeQuery();
            cs1.executeQuery();
            rs = (ResultSet) cs.getObject(1);
            rs1 = (ResultSet) cs1.getObject(1);
            this.getNumTS();
            double[][] data = new double[][]{
                new double[1],
                new double[1],
                new double[1],};
            for (int i = 0; i < NumTs; i++) {
                if (i == temp) {
                    rs1.next();
                    if (name.equals(rs1.getString(1))) {
                        double usado = rs1.getDouble(2) - rs1.getDouble(3);
                        data[0][0] = usado;

                        double topeA1 = rs1.getDouble(2) * HWT;
                        if (usado > topeA1) {
                            data[2][0] = rs1.getDouble(2) - usado;
                        } else {
                            double a = rs1.getDouble(2) - topeA1;
                            data[1][0] = rs1.getDouble(3) - a;
                            data[2][0] = a;
                        }
                    }
                } else {
                    rs.next();
                    if (name.equals(rs.getString(1))) {
                        data[0][0] = rs.getDouble(2);

                        double topeA = rs.getDouble(4) * HWT;
                        if (rs.getDouble(2) > topeA) {

                            data[2][0] = rs.getDouble(4) - rs.getDouble(2);

                            Object[] a11 = new Object[3];
                            a11[0] = (rs.getString(1));
                            a11[1] = (String.valueOf(rs.getDouble(3)));
                            a11[2] = ("0");
                            freeMem.add(a11);
                        } else {
                            double a = rs.getDouble(4) - topeA; // rojo
                            data[1][0] = rs.getDouble(3) - a;
                            Object[] a11 = new Object[3];
                            a11[0] = (rs.getString(1));
                            a11[1] = (rs.getDouble(3));//total libre
                            a11[2] = (rs.getDouble(3) - a); // azul
                            freeMem.add(a11);
                            data[2][0] = a;
                        }
                    }
                }
            }
            getDays(-1.0);
            return data;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Double consultaMemSGA() {
        Statement ts;
        ResultSet rs;
        Conectar();
        CallableStatement cs = null;
        try {
            String sql = "{call getMem(?)}";
            cs = conexion.prepareCall(sql);
            //cs.registerOutParameter(1, OracleTypes.FLOAT);
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.executeQuery();
            rs = (ResultSet) cs.getObject(1);
            //System.out.println(rs.getFloat(1));
            //return Double.valueOf(String.valueOf(rs.getFloat(1)));
            while (rs.next()) {
                System.out.println(rs.getString(1));
                return Double.valueOf(rs.getString(1));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 0.0;
    }

    public ArrayList<String> consulta() {
        Statement ts;
        ResultSet rs;
        Conectar();
        CallableStatement cs = null;
        try {
            //String sql = "select s.username,p.sql_text,p.first_load_time,p.sharable_mem,p.elapsed_time from v$session s, v$sqlarea p where s.username is not null and s.sql_id = p.sql_id";
            String sql = "{call getInf(?)}";
            cs = conexion.prepareCall(sql);
            //cs.registerOutParameter(1, OracleTypes.FLOAT);
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.executeQuery();
            rs = (ResultSet) cs.getObject(1);
            //rs = ((OracleCallableStatement) cs).getCursor(1);
            while (rs.next()) {
                if ("select se.username, sq.sql_text, sq.first_load_time, sq.sharable_mem, sq.elapsed_time from v$sql sq, v$session se where first_load_time=(select max(first_load_time) from v$sql) and sq.PARSING_USER_ID = se.user#;".equals(rs.getString(2))) {
                    return null;
                }
//                if(actual.equals(rs.getString(2))){
//                    return null;
//                }
                System.out.println("user:" + rs.getString(1));
                System.out.println("Query:" + rs.getString(2));
                System.out.println("date:" + rs.getString(3));
                System.out.println("Memory:" + rs.getString(4));
                System.out.println("Time:" + rs.getString(5));

                if (!(actual.equals(rs.getString(2)))) {
                    actual = rs.getString(2);
                    WriteFile writeFile = new WriteFile();
                    writeFile.write(rs.getString(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getString(5));
                }

                ArrayList<String> a1 = new ArrayList<>();
                a1.add(rs.getString(4));
                a1.add(rs.getString(5));
                return a1;
            }
            //return false;
        } catch (SQLException e) {
            System.err.print(e.getMessage());
        }
        return null;
    }

    public void consultas() {
        Statement ts;
        ResultSet rs;
        Conectar();
        CallableStatement cs = null;
        try {
            cs = conexion.prepareCall("select * from USER_ROLE_PRIVS");
            //rs = cs.executeQuery();
            System.out.print(cs.execute());
        } catch (Exception e) {
        }
    }
}
