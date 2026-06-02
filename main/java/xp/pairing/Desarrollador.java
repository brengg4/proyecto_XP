package main.java.xp.pairing;

public class Desarrollador{
    private int id;
    private final String identificador;
    private RolPairing rolActivo;
    private EntornoDeEdicionConcurrente entorno;

    public Desarrollador(String identificador, RolPairing rolActivo){
        this.identificador = identificador;
        this.rolActivo = rolActivo;
    }

    public void setEntorno(EntornoDeEdicionConcurrente entorno){
        this.entorno = entorno;
    }

    public void ingresarLogica(ComponenteLogico componente, String codigo){
        componente.registrarAporteColaborativo(codigo);
        entorno.sincronizarEstadoEnTiempoReal(componente);
    }

    public void solicitarIntercambioRol(){
        entorno.gestionarTransferenciaDeControl(this);
    }

    public String observarEstado(ComponenteLogico componente){
        return componente.getEstadoActual();
    }

    public RolPairing getRolActivo(){
        return rolActivo;
    }

    public void setRolActivo(RolPairing rol){
        this.rolActivo = rol;
    }

    public String getIdentificador(){
        return identificador;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
