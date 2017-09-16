package transacciones;



public class Transacciones {
    
    public static void main(String[] args) {
//        RuntimeTypeAdapterFactory<Jsonable> rta = RuntimeTypeAdapterFactory.of(Jsonable.class, "_class")
//                    .registerSubtype(Transaccion.class, "Transaccion");
//        Gson gson = new GsonBuilder().registerTypeAdapterFactory(rta).setDateFormat("yyyy/MM/dd").create();
        DB db = new DB(); 
        db.guardar();
    }
}
