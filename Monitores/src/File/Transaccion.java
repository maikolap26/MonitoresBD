
package File;

import java.util.Date;

public class Transaccion implements Jsonable {
   private Date fecha;
   private int cantRows;
   private int cantRowsDia;
   private String tablespace;

    public Transaccion(Date fecha, int cantRows, int cantRowsDia, String tablespace) {
        this.fecha = fecha;
        this.cantRows = cantRows;
        this.cantRowsDia = cantRowsDia;
        this.tablespace= tablespace;
    }
   
    public Date getFecha() {
        return fecha;
    }

    public int getCantRows() {
        return cantRows;
    }

    public int getCantRowsDia() {
        return cantRowsDia;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public void setCantRows(int cantRows) {
        this.cantRows = cantRows;
    }

    public void setCantRowsDia(int cantRowsDia) {
        this.cantRowsDia = cantRowsDia;
    }

    public String getTablespace() {
        return tablespace;
    }

    public void setTablespace(String tablespace) {
        this.tablespace = tablespace;
    }

    @Override
    public String toString() {
        return "Transaccion{" + "fecha=" + fecha + ", cantRows=" + cantRows + ", cantRowsDia=" + cantRowsDia + ", tablespace=" + tablespace + '}';
    }
    
}
