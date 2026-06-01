package main.java.xp.velocity;

public class ResumenSprint{
    private final int tareasCompletadas;
    private final double horasRealesTotal;
    private final double horasEstimadasTotal;

    public ResumenSprint(int tareasCompletadas,
                         double horasRealesTotal,
                         double horasEstimadasTotal){
        this.tareasCompletadas = tareasCompletadas;
        this.horasRealesTotal = horasRealesTotal;
        this.horasEstimadasTotal = horasEstimadasTotal;
    }

    public int getTareasCompletadas()        { return tareasCompletadas; }
    public double getHorasRealesTotal()      { return horasRealesTotal; }
    public double getHorasEstimadasTotal()   { return horasEstimadasTotal; }

    public double calcularVelocity(){
        return horasRealesTotal > 0 ? horasEstimadasTotal / horasRealesTotal : 0;
    }
}
