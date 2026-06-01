package main.java.xp.velocity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Sprint {
    private int id;
    private final String nombre;
    private final LocalDate fechaInicio;
    private final List<Tarea> tareas;

    public Sprint(String nombre, LocalDate fechaInicio){
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.tareas = new ArrayList<>();
    }

    public void agregarTarea(Tarea tarea){
        tareas.add(tarea);
    }

    public ResumenSprint generarResumen(){
        int completadas = 0;
        double realesTotal = 0.0;
        double estimadasTotal = 0.0;

        for (Tarea t : tareas){
            if (t.estaCompletada()){
                completadas++;
                realesTotal += t.getTiempoTotalInvertido();
                estimadasTotal += t.getHorasEstimadas();
            }
        }
        return new ResumenSprint(completadas, realesTotal, estimadasTotal);
    }

    public String getNombre()          { return nombre; }
    public LocalDate getFechaInicio()  { return fechaInicio; }
    public List<Tarea> getTareas()     { return tareas; }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
