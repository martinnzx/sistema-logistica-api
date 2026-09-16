package dev.logistica.api;

import dev.logistica.api.service.PaqueteService;
import dev.logistica.api.dto.PaqueteDTO;
import dev.logistica.api.enums.NivelFragilidad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class PaqueteServiceTest {

    @Autowired
    private PaqueteService paqueteService;

    private PaqueteDTO paqueteDtoBase;
    private PaqueteDTO paqueteDtoA;
    private PaqueteDTO paqueteDtoB;

    private PaqueteDTO dtoFueraDeRango;
    private PaqueteDTO dtoConNulos;
    @BeforeEach
    void setUp() {
        paqueteDtoBase = new PaqueteDTO();
        paqueteDtoBase.setPesoKg(10.0);
        paqueteDtoBase.setVolumenDm3(20.0);
        paqueteDtoBase.setTipo("Fragil");
        paqueteDtoBase.setCodigo("PF-001");
        paqueteDtoBase.setNivelFragilidad(NivelFragilidad.ALTA);
        paqueteDtoBase.setSeguroAdicional(true);

        paqueteDtoA = new PaqueteDTO();
        paqueteDtoA.setCodigo("PF-001");
        paqueteDtoA.setTipo("Fragil");
        paqueteDtoA.setPesoKg(5.0);
        paqueteDtoA.setVolumenDm3(10.0);
        paqueteDtoA.setNivelFragilidad(NivelFragilidad.ALTA);
        paqueteDtoA.setSeguroAdicional(true);

        paqueteDtoB = new PaqueteDTO();
        paqueteDtoB.setCodigo("PF-002");
        paqueteDtoB.setTipo("Fragil");
        paqueteDtoB.setPesoKg(15.0);
        paqueteDtoB.setVolumenDm3(25.0);
        paqueteDtoB.setNivelFragilidad(NivelFragilidad.MEDIA);
        paqueteDtoB.setSeguroAdicional(false);

        dtoFueraDeRango = new PaqueteDTO();
        dtoFueraDeRango.setTipo("Resfrigerado");
        dtoFueraDeRango.setCodigo("PR-ERR-01");
        dtoFueraDeRango.setPesoKg(5.0);
        dtoFueraDeRango.setVolumenDm3(5.0);
        dtoFueraDeRango.setTemperaturaObjetivo(10.0);
        dtoFueraDeRango.setRangoMin(2.0);
        dtoFueraDeRango.setRangoMax(8.0);

        dtoConNulos = new PaqueteDTO();
        dtoConNulos.setTipo("Resfrigerado");
        dtoConNulos.setCodigo("PR-ERR-02");
        dtoConNulos.setPesoKg(5.0);
        dtoConNulos.setVolumenDm3(5.0);
        dtoConNulos.setTemperaturaObjetivo(null);
        dtoConNulos.setRangoMax(8.0);
    }

    @Test
    void crearPaquete_deberiaGuardarCorrectamente() {
        PaqueteDTO guardado = paqueteService.crearPaquete(paqueteDtoBase);

        assertNotNull(guardado, "El paquete guardado no debe ser nulo");
        assertEquals("Fragil", guardado.getTipo());
    }

    @Test
    void crearPaqueteRefrigerado_conTemperaturaFueraDeRango_deberiaLanzarExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> paqueteService.crearPaquete(dtoFueraDeRango)
        );
    }

    @Test
    void crearPaqueteRefrigerado_conValoresDeTemperaturaNulos_deberiaLanzarExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> paqueteService.crearPaquete(dtoConNulos)
        );
    }
}
