package main.java.xp.pairing;

public class ComponenteLogico{
    private int id;
    private final String identificadorComponente;
    private String estadoActual;

    public ComponenteLogico(String identificadorComponente){
        this.identificadorComponente = identificadorComponente;
        this.estadoActual = "";
    }

    public void registrarAporteColaborativo(String codigo){
        this.estadoActual = codigo;
    }

    public void fusionarAporte(String codigo){
        if (this.estadoActual.isEmpty()) {
            this.estadoActual = codigo;
        } else {
            this.estadoActual = this.estadoActual + "\n<<<FUSION>>>\n" + codigo;
        }
    }

    public String getEstadoActual(){
        return estadoActual;
    }

    public String getIdentificadorComponente(){
        return identificadorComponente;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
