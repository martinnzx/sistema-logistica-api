package ar.edu.unju.fi;


import ar.edu.unju.fi.Service.EnvioService;
import ar.edu.unju.fi.dto.EnvioDTO;
import ar.edu.unju.fi.dto.PaqueteDTO;
import ar.edu.unju.fi.Enum.EstadoEnvio;
import ar.edu.unju.fi.Enum.NivelFragilidad;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@Transactional
public class EnvioServiceTest {
    @Autowired
    private EnvioService envioService;


    PaqueteDTO p1 = new PaqueteDTO();
    PaqueteDTO p2 = new PaqueteDTO();
    @BeforeEach
    public void setUp()
    {
        p1.setCodigo("ABC123");
        p1.setNivelFragilidad(NivelFragilidad.ALTA);
        p1.setSeguroAdicional(true);
        p1.setPesoKg(5.0);
        p1.setVolumenDm3(10.0);
        p1.setTipo("Fragil");

        p2.setCodigo("ABC124");
        p2.setNivelFragilidad(NivelFragilidad.ALTA);
        p2.setSeguroAdicional(true);
        p2.setPesoKg(5.0);
        p2.setVolumenDm3(10.0);
        p2.setTipo("Fragil");
    }
    @Test
    void crearEnvio_guardaCorrectamente() {
        List<PaqueteDTO> paquetesDTO = new ArrayList<>();
        paquetesDTO.add(p1);

        EnvioDTO dto = new EnvioDTO();
        dto.setPaquetes(paquetesDTO);
        dto.setRemitente("Juan");
        dto.setDestinatario("Maria");
        dto.setDireccionEntrega("Calle Falsa 123");
        dto.setEstado(EstadoEnvio.GENERADO);

        EnvioDTO guardado = envioService.crearEnvio(dto);

        assertEquals("Juan", guardado.getRemitente());
    }
    @Test
    void listarPorRemitente_devuelveCorrecto() {

        List<PaqueteDTO> paquetes = new ArrayList<>();
        paquetes.add(p1);
        paquetes.add(p2);

        EnvioDTO dto1 = new EnvioDTO();
        dto1.setRemitente("Juan");
        dto1.setDestinatario("Pedro");
        dto1.setDireccionEntrega("Calle 1");
        dto1.setEstado(EstadoEnvio.GENERADO);
        dto1.setPaquetes(paquetes);
        envioService.crearEnvio(dto1);

        EnvioDTO dto2 = new EnvioDTO();
        dto2.setRemitente("Carlos");
        dto2.setDestinatario("Juan");
        dto2.setDireccionEntrega("Calle 2");
        dto2.setEstado(EstadoEnvio.EN_RUTA);
        dto2.setPaquetes(paquetes);
        envioService.crearEnvio(dto2);

        List<EnvioDTO> resultado = envioService.listarPorRemitente("Juan");

        assertEquals(1, resultado.size(), "Solo debería encontrar 1 envío con remitente 'Juan'.");
        assertEquals("Juan", resultado.get(0).getRemitente(), "El remitente del envío encontrado debe ser 'Juan'.");
    }

    @Test
    void listarPorDestinatario_devuelveCorrecto() {
        List<PaqueteDTO> paquetes = new ArrayList<>();
        paquetes.add(p1);
        paquetes.add(p2);

        EnvioDTO dto1 = new EnvioDTO();
        dto1.setRemitente("Marcos");
        dto1.setDestinatario("Ale");
        dto1.setDireccionEntrega("Calle 1");
        dto1.setEstado(EstadoEnvio.GENERADO);
        dto1.setPaquetes(paquetes);
        envioService.crearEnvio(dto1);


        EnvioDTO dto2 = new EnvioDTO();
        dto2.setRemitente("Luciano");
        dto2.setDestinatario("Hector");
        dto2.setDireccionEntrega("Calle 2");
        dto2.setEstado(EstadoEnvio.EN_RUTA);
        dto2.setPaquetes(paquetes);
        envioService.crearEnvio(dto2);

        List<EnvioDTO> resultado = envioService.listarPorDestinatario("Hector");

        assertEquals(1, resultado.size(), "Solo debería encontrar 1 envío con destinatario 'Hector'.");
        assertEquals("Hector", resultado.get(0).getDestinatario(), "El destinatario del envío encontrado debe ser 'Hector'.");
    }

    @Test
    void listarPorEstado_devuelveCorrecto() {
        EnvioDTO dto1 = new EnvioDTO();
        dto1.setRemitente("Ana");
        dto1.setDestinatario("Luis");
        dto1.setDireccionEntrega("Calle 1");
        dto1.setEstado(EstadoEnvio.GENERADO);
        dto1.setPaquetes(List.of(p1));
        envioService.crearEnvio(dto1);

        EnvioDTO dto2 = new EnvioDTO();
        dto2.setRemitente("Mario");
        dto2.setDestinatario("Laura");
        dto2.setDireccionEntrega("Calle 2");
        dto2.setEstado(EstadoEnvio.EN_RUTA);
        dto2.setPaquetes(List.of(p2));
        envioService.crearEnvio(dto2);

        EnvioDTO dto3 = new EnvioDTO();
        dto3.setRemitente("Juan");
        dto3.setDestinatario("Pedro");
        dto3.setDireccionEntrega("Calle 3");
        dto3.setEstado(EstadoEnvio.GENERADO);
        dto3.setPaquetes(List.of(p1));
        envioService.crearEnvio(dto3);

        List<EnvioDTO> resultado = envioService.listarPorEstado(EstadoEnvio.GENERADO);

        assertEquals(2, resultado.size(), "Debería encontrar 2 envíos con estado GENERADO");

        for (EnvioDTO envio : resultado) {
            assertEquals(EstadoEnvio.GENERADO, envio.getEstado(), "El estado debe ser GENERADO");
        }
    }
}
