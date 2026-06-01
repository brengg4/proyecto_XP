package main.java.xp.velocity;

import java.util.ArrayList;
import java.util.List;

public class RepositorioSprint{
    private final List<Sprint> sprints = new ArrayList<>();

    public void guardarSprint(Sprint sprint){
        sprints.add(sprint);
    }

    public Sprint buscarPorNombre(String nombre){
        return sprints.stream()
                      .filter(s -> s.getNombre().equals(nombre))
                      .findFirst()
                      .orElse(null);
    }

    public List<Sprint> getTodos(){
        return List.copyOf(sprints);
    }
}
