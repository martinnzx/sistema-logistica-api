package ar.edu.unju.fi;

import ar.edu.unju.fi.Repository.PaqueteRepository;
import ar.edu.unju.fi.Service.PaqueteService;
import ar.edu.unju.fi.dto.PaqueteDTO;
import ar.edu.unju.fi.Enum.NivelFragilidad;
import ar.edu.unju.fi.model.PaqueteFragil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class PaqueteServiceTest {

    @Autowired
    private PaqueteService paqueteService;

    @Autowired
    private PaqueteRepository paqueteRepository;

    private PaqueteDTO paqueteDtoBase;
    private PaqueteFragil paqueteFragilA;
    private PaqueteFragil paqueteFragilB;

    @BeforeEach
    void setUp() {
        paqueteDtoBase = new PaqueteDTO();
        paqueteDtoBase.setPesoKg(10.0);
        paqueteDtoBase.setVolumenDm3(20.0);
        paqueteDtoBase.setTipo("Fragil");
        paqueteDtoBase.setCodigo("PF-001");
        paqueteDtoBase.setNivelFragilidad(NivelFragilidad.ALTA);
        paqueteDtoBase.setSeguroAdicional(true);

        paqueteFragilA = new PaqueteFragil();
        paqueteFragilA.setCodigo("PF-001");
        paqueteFragilA.setPesoKg(5.0);
        paqueteFragilA.setVolumenDm3(10.0);
        paqueteFragilA.setNivelFragilidad(NivelFragilidad.ALTA);
        paqueteFragilA.setSeguroAdicional(true);

        paqueteFragilB = new PaqueteFragil();
        paqueteFragilB.setCodigo("PF-002");
        paqueteFragilB.setPesoKg(15.0);
        paqueteFragilB.setVolumenDm3(25.0);
        paqueteFragilB.setNivelFragilidad(NivelFragilidad.MEDIA);
        paqueteFragilB.setSeguroAdicional(false);
    }

    @Test
    void crearPaquete_deberiaGuardarCorrectamente() {
        PaqueteDTO guardado = paqueteService.crearPaquete(paqueteDtoBase);

        assertNotNull(guardado, "El paquete guardado no debe ser nulo");
        assertEquals("Fragil", guardado.getTipo());
        assertEquals("PF-001", guardado.getCodigo());
        assertEquals(1, paqueteRepository.count(), "Debe haberse guardado 1 paquete");
    }

    @Test
    void listarPorPeso_deberiaRetornarPaquetesDentroDelRango() {

        paqueteRepository.save(paqueteFragilA);
        paqueteRepository.save(paqueteFragilB);

        List<PaqueteDTO> resultado = paqueteService.listarPorPeso(0.0, 10.0);

        assertEquals(1, resultado.size());
        assertEquals(5.0, resultado.get(0).getPesoKg());
        assertTrue(resultado.get(0).getPesoKg() <= 10.0);
    }

    @Test
    void listarPorVolumen_deberiaRetornarPaquetesDentroDelRango() {
        paqueteRepository.save(paqueteFragilA);
        paqueteRepository.save(paqueteFragilB);

        List<PaqueteDTO> resultado = paqueteService.listarPorVolumen(0.0, 15.0);

        assertEquals(1, resultado.size());
        assertEquals(10.0, resultado.get(0).getVolumenDm3());
        assertTrue(resultado.get(0).getVolumenDm3() <= 15.0);
    }
}