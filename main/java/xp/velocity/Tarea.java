package main.java.xp.velocity;

public class Tarea {
    private int id;
    private final String nombre;
    private final double horasEstimadas;
    private EstadoCronometro estadoCronometro;
    private double tiempoTotalInvertido;
    private boolean completada;
    private String desarrolladorAsignado;

    public Tarea(String nombre, double horasEstimadas){
        this.nombre = nombre;
        this.horasEstimadas = horasEstimadas;
        this.estadoCronometro = EstadoCronometro.PENDIENTE;
        this.tiempoTotalInvertido = 0.0;
        this.completada = false;
    }

    public void iniciarCronometro(){
        this.estadoCronometro = EstadoCronometro.EN_CURSO;
    }

    public void pausarCronometro(){
        this.estadoCronometro = EstadoCronometro.PAUSADO;
    }

    public void detenerCronometro(){
        this.estadoCronometro = EstadoCronometro.DETENIDO;
    }

    public void registrarTiempoTranscurrido(double horas){
        this.tiempoTotalInvertido += horas;
    }

    public double calcularDiferencia(){
        return horasEstimadas - tiempoTotalInvertido;
    }

    public void cerrar(){
        this.completada = true;
        this.estadoCronometro = EstadoCronometro.DETENIDO;
    }

    public String getNombre()                        { return nombre; }
    public double getHorasEstimadas()                { return horasEstimadas; }
    public EstadoCronometro getEstadoCronometro()    { return estadoCronometro; }
    public double getTiempoTotalInvertido()          { return tiempoTotalInvertido; }
    public boolean estaCompletada()                  { return completada; }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDesarrolladorAsignado() {
        return desarrolladorAsignado;
    }

    public void setDesarrolladorAsignado(String desarrolladorAsignado) {
        this.desarrolladorAsignado = desarrolladorAsignado;
    }
}
