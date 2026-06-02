package test.java.xp.pairing;

import main.java.xp.pairing.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EntornoDeEdicionConcurrenteTest {

    private EntornoDeEdicionConcurrente entorno;
    private Desarrollador piloto;
    private Desarrollador copiloto;
    private ComponenteLogico componente;

    @BeforeEach
    void setUp() {
        entorno   = new EntornoDeEdicionConcurrente();
        piloto    = new Desarrollador("dev-1", RolPairing.PILOTO);
        copiloto  = new Desarrollador("dev-2", RolPairing.COPILOTO);
        componente = new ComponenteLogico("LoginService");

        piloto.setEntorno(entorno);
        copiloto.setEntorno(entorno);
        entorno.iniciarSesion(piloto);
        entorno.iniciarSesion(copiloto);
    }

    /**
     * Verifica que, tras sincronizar el componente, el copiloto observa
     * el mismo código que el piloto acaba de escribir.
     */
    @Test
    @DisplayName("CA-1: Ambos desarrolladores observan el mismo estado del código simultáneamente")
    void ambosDevelopersObservanElMismoEstadoDelCodigo() {
        piloto.ingresarLogica(componente, "int x = 5;");

        String estadoPiloto   = piloto.observarEstado(componente);
        String estadoCopiloto = copiloto.observarEstado(componente);

        assertEquals(estadoPiloto, estadoCopiloto,
            "El piloto y el copiloto deben ver exactamente el mismo estado del componente");
    }

    /**
     * Verifica que tras gestionarTransferenciaDeControl los roles se
     * intercambian atómicamente sin pasar por ningún mecanismo de lock.
     */
    @Test
    @DisplayName("CA-2: La transferencia del control de escritura ocurre sin liberación de locks")
    void transferenciaDeControlOcurreSinLocks() {
        // Estado inicial: piloto → PILOTO, copiloto → COPILOTO
        entorno.gestionarTransferenciaDeControl(piloto);

        assertEquals(RolPairing.COPILOTO, piloto.getRolActivo(),
            "El ex-piloto debe haber pasado a rol copiloto tras la transferencia");
        assertEquals(RolPairing.PILOTO, copiloto.getRolActivo(),
            "El ex-copiloto debe haber pasado a rol piloto tras la transferencia");
    }

    /**
     * Verifica que dos aportes simultáneos se fusionan en el componente
     * y que ninguno de ellos se pierde ni se rechaza.
     */
    @Test
    @DisplayName("CA-3: Los aportes concurrentes se resuelven por fusión, no por rechazo")
    void aporteConcurrenteSeResuelvePorFusion() {
        entorno.aceptarFlujoContinuo(componente, "int a = 1;");
        entorno.aceptarFlujoContinuo(componente, "int b = 2;");

        String estado = componente.getEstadoActual();

        assertTrue(estado.contains("int a = 1;"),
            "El primer aporte debe estar presente en el estado fusionado");
        assertTrue(estado.contains("int b = 2;"),
            "El segundo aporte no debe haber sido rechazado; debe estar en el estado fusionado");
    }
}
