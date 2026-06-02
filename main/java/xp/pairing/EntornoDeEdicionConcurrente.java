package main.java.xp.pairing;

public class EntornoDeEdicionConcurrente{
    private int id;
    private String fechaSesion;
    private String estadoSincronizacion;
    private int participantesActivos;
    private Desarrollador piloto;
    private Desarrollador copiloto;
    private ComponenteLogico componenteActual;

    public EntornoDeEdicionConcurrente(){
        this.estadoSincronizacion = "INACTIVO";
        this.participantesActivos = 0;
    }

    public void iniciarSesion(Desarrollador dev){
        dev.setEntorno(this);
        if (RolPairing.PILOTO.equals(dev.getRolActivo())){
            this.piloto = dev;
        } else {
            this.copiloto = dev;
        }
        participantesActivos++;
        estadoSincronizacion = "ACTIVO";
    }

    public void sincronizarEstadoEnTiempoReal(ComponenteLogico componente){
        this.componenteActual = componente;
    }

    public void gestionarTransferenciaDeControl(Desarrollador solicitante){
        if (piloto == null || copiloto == null) return;
        RolPairing rolPiloto = piloto.getRolActivo();
        RolPairing rolCopiloto = copiloto.getRolActivo();
        piloto.setRolActivo(rolCopiloto);
        copiloto.setRolActivo(rolPiloto);
    }

    public void aceptarFlujoContinuo(ComponenteLogico componente, String aporte){
        componente.fusionarAporte(aporte);
        sincronizarEstadoEnTiempoReal(componente);
    }

    public String getEstadoSincronizacion() {
        return estadoSincronizacion;
    }

    public int getParticipantesActivos() {
        return participantesActivos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFechaSesion() {
        return fechaSesion;
    }

    public void setFechaSesion(String fechaSesion) {
        this.fechaSesion = fechaSesion;
    }
}
