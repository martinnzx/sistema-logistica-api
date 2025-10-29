package ar.edu.unju.fi;

import ar.edu.unju.fi.Repository.PaqueteRepository;
import ar.edu.unju.fi.Service.PaqueteService;
import ar.edu.unju.fi.dto.PaqueteDTO;
import ar.edu.unju.fi.model.NivelFragilidad;
import ar.edu.unju.fi.model.PaqueteFragil;
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

    @Test
    void crearPaquete() {
        PaqueteDTO dto = new PaqueteDTO();
        dto.setPesoKg(10.0);
        dto.setVolumenDm3(20.0);
        dto.setTipo("Fragil");
        dto.setCodigo("PF-001");
        dto.setNivelFragilidad(NivelFragilidad.ALTA);
        dto.setSeguroAdicional(true);
        PaqueteDTO guardado = paqueteService.crearPaquete(dto);

        assertNotNull(guardado, "El paquete guardado no debe ser nulo");
        assertEquals("Fragil", guardado.getTipo());
        assertEquals(1, paqueteRepository.count(), "Debe haberse guardado 1 paquete");
    }
    @Test
    void listarPorPeso() {
        PaqueteFragil p1 = new PaqueteFragil();
        p1.setCodigo("PF-001");
        p1.setPesoKg(5.0);
        p1.setVolumenDm3(10.0);
        p1.setNivelFragilidad(NivelFragilidad.ALTA);
        p1.setSeguroAdicional(true);
        paqueteRepository.save(p1);

        PaqueteFragil p2 = new PaqueteFragil();
        p2.setCodigo("PF-002");
        p2.setPesoKg(15.0);
        p2.setVolumenDm3(20.0);
        p2.setNivelFragilidad(NivelFragilidad.MEDIA);
        p2.setSeguroAdicional(false);
        paqueteRepository.save(p2);

        List<PaqueteDTO> resultado = paqueteService.listarPorPeso(0.0, 10.0);

        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getPesoKg() <= 10.0);
    }

    @Test
    void listarPorVolumen() {
        PaqueteFragil p1 = new PaqueteFragil();
        p1.setCodigo("PF-001");
        p1.setPesoKg(5.0);
        p1.setVolumenDm3(10.0);
        p1.setNivelFragilidad(NivelFragilidad.ALTA);
        p1.setSeguroAdicional(true);
        paqueteRepository.save(p1);

        PaqueteFragil p2 = new PaqueteFragil();
        p2.setCodigo("PF-002");
        p2.setPesoKg(15.0);
        p2.setVolumenDm3(25.0);
        p2.setNivelFragilidad(NivelFragilidad.MEDIA);
        p2.setSeguroAdicional(false);
        paqueteRepository.save(p2);

        List<PaqueteDTO> resultado = paqueteService.listarPorVolumen(0.0, 10.0);

        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getVolumenDm3() <= 10.0);
    }
}
