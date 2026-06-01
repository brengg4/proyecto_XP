package test.java.xp.velocity;

import main.java.xp.velocity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para la User Story 2:
 * "Medir el tiempo real por tarea para controlar la velocity del sprint"
 * REGLA: 1 test por cada Criterio de Aceptación (total = 6).
 */
class TareaVelocityTest {

    private Tarea tarea;
    private Sprint sprint;

    @BeforeEach
    void setUp() {
        tarea  = new Tarea("Implementar login", 3.0);
        sprint = new Sprint("Sprint 1", LocalDate.of(2026, 5, 27));
        sprint.agregarTarea(tarea);
    }

    /**
     * CA-1: El sistema permite crear tareas con un nombre y una
     * estimación de tiempo en horas.
     * Verifica que la Tarea almacena correctamente el nombre y las horas
     * estimadas suministradas en la construcción.
     */
    @Test
    @DisplayName("CA-1: Una tarea se crea con nombre y estimación de horas")
    void tareaSeCreaConNombreYEstimacion() {
        assertEquals("Implementar login", tarea.getNombre());
        assertEquals(3.0, tarea.getHorasEstimadas(), 0.001);
    }

    /**
     * CA-2: Cada tarea tiene un cronómetro que puede iniciarse,
     * pausarse y detenerse.
     * Verifica que el estado del cronómetro transiciona correctamente
     * tras cada operación.
     */
    @Test
    @DisplayName("CA-2: El cronómetro de la tarea puede iniciarse, pausarse y detenerse")
    void cronometroDebeIniciarPausarYDetener() {
        tarea.iniciarCronometro();
        assertEquals(EstadoCronometro.EN_CURSO, tarea.getEstadoCronometro());

        tarea.pausarCronometro();
        assertEquals(EstadoCronometro.PAUSADO, tarea.getEstadoCronometro());

        tarea.detenerCronometro();
        assertEquals(EstadoCronometro.DETENIDO, tarea.getEstadoCronometro());
    }

    /**
     * CA-3: El sistema muestra en tiempo real la diferencia entre el
     * tiempo estimado y el tiempo real transcurrido por tarea.
     * Verifica que calcularDiferencia() retorna (estimado − real).
     */
    @Test
    @DisplayName("CA-3: La diferencia entre tiempo estimado y real está disponible")
    void diferenciaEntreEstimadoYRealEstaDisponible() {
        tarea.iniciarCronometro();
        tarea.registrarTiempoTranscurrido(1.5); // simula 1.5 h reales

        double diferencia = tarea.calcularDiferencia();

        assertEquals(1.5, diferencia, 0.001,
            "3.0 estimado − 1.5 real debe dar 1.5 de diferencia");
    }

    /**
     * CA-4: Al cerrar una tarea, el sistema la marca como completada y
     * registra el tiempo total invertido.
     * Verifica el flag de completitud y el acumulado de horas reales
     * tras llamar a cerrar().
     */
    @Test
    @DisplayName("CA-4: Al cerrar la tarea queda marcada como completada con el tiempo total")
    void alCerrarTareaSeMarcaCompletadaYGuardaTiempoTotal() {
        tarea.iniciarCronometro();
        tarea.registrarTiempoTranscurrido(2.0);
        tarea.cerrar();

        assertTrue(tarea.estaCompletada());
        assertEquals(2.0, tarea.getTiempoTotalInvertido(), 0.001);
    }

    /**
     * CA-5: Al finalizar el sprint, el sistema genera un resumen con la
     * velocity real: total de tareas completadas y horas invertidas vs estimadas.
     * Verifica que ResumenSprint refleja los contadores correctos.
     */
    @Test
    @DisplayName("CA-5: Al finalizar el sprint se genera un resumen con la velocity real")
    void alFinalizarSprintSeGeneraResumenConVelocityReal() {
        tarea.iniciarCronometro();
        tarea.registrarTiempoTranscurrido(4.0);
        tarea.cerrar();

        ResumenSprint resumen = sprint.generarResumen();

        assertEquals(1,   resumen.getTareasCompletadas());
        assertEquals(4.0, resumen.getHorasRealesTotal(),    0.001);
        assertEquals(3.0, resumen.getHorasEstimadasTotal(), 0.001);
    }

    /**
     * CA-6: Los datos del sprint anterior quedan guardados y son
     * consultables para mejorar las estimaciones del siguiente sprint.
     * Verifica que RepositorioSprint persiste el sprint y lo retorna
     * con sus tareas intactas.
     */
    @Test
    @DisplayName("CA-6: Los datos del sprint se guardan y pueden consultarse en sprints futuros")
    void datosDelSprintSeGuardanYPuedenConsultarse() {
        tarea.iniciarCronometro();
        tarea.registrarTiempoTranscurrido(2.5);
        tarea.cerrar();

        RepositorioSprint repo = new RepositorioSprint();
        repo.guardarSprint(sprint);

        Sprint recuperado = repo.buscarPorNombre("Sprint 1");

        assertNotNull(recuperado, "El sprint debe poder recuperarse del repositorio");
        assertEquals(1, recuperado.getTareas().size());
        assertEquals(2.5,
            recuperado.getTareas().get(0).getTiempoTotalInvertido(), 0.001);
    }
}
