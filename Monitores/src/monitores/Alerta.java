/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitores;

/**
 *
 * @author jimen
 */
public class Alerta {
    
    private String nombre;
    private String sql;
    private String fechaHora;
    private String memoria;
    private Double tiempo;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getMemoria() {
        return memoria;
    }

    public void setMemoria(String memoria) {
        this.memoria = memoria;
    }

    public Double getTiempo() {
        return tiempo;
    }

    public void setTiempo(Double tiempo) {
        this.tiempo = tiempo;
    }

    public Alerta(String nombre, String sql, String fechaHora, String memoria, Double tiempo) {
        this.nombre = nombre;
        this.sql = sql;
        this.fechaHora = fechaHora;
        this.memoria = memoria;
        this.tiempo = tiempo;
    }

    @Override
    public String toString() {
        return  nombre + ";" + sql + ";" + fechaHora + ";" + memoria + ";" + tiempo ;
    }
}
