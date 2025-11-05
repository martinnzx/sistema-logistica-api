package ar.edu.unju.fi;


import ar.edu.unju.fi.Repository.EnvioRepository;
import ar.edu.unju.fi.Repository.HistorialEstadoEnvioRepository;
import ar.edu.unju.fi.Service.ClienteService;
import ar.edu.unju.fi.Service.EnvioService;
import ar.edu.unju.fi.dto.ClienteDTO;
import ar.edu.unju.fi.dto.EnvioDTO;
import ar.edu.unju.fi.dto.PaqueteDTO;
import ar.edu.unju.fi.Enum.EstadoEnvio;
import ar.edu.unju.fi.Enum.NivelFragilidad;

import ar.edu.unju.fi.model.Envio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
public class EnvioServiceTest {

    @Autowired
    private EnvioService envioService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private EnvioRepository envioRepository;

    @Autowired
    private HistorialEstadoEnvioRepository HistorialEstadoEnvioRepository;

    private ClienteDTO Juan;
    private ClienteDTO Maria;
    private ClienteDTO Carlos;
    private ClienteDTO Pedro;
    private ClienteDTO Hector;
    private ClienteDTO Ale;

    private PaqueteDTO p1;
    private PaqueteDTO p2;

    @BeforeEach
    public void setUp() {
        p1 = new PaqueteDTO();
        p1.setCodigo("P-001");
        p1.setPesoKg(5.0);
        p1.setVolumenDm3(10.0);
        p1.setNivelFragilidad(NivelFragilidad.ALTA);
        p1.setSeguroAdicional(true);
        p1.setTipo("Fragil");

        p2 = new PaqueteDTO();
        p2.setCodigo("P-002");
        p2.setPesoKg(2.0);
        p2.setVolumenDm3(4.0);
        p2.setNivelFragilidad(NivelFragilidad.BAJA);
        p2.setTipo("Fragil");
        p2.setSeguroAdicional(false);

        ClienteDTO juanDTO = ClienteDTO.builder().nombreRazonSocial("Juan Perez").documentoOCuit("111").telefono("123").email("j@j.com").direccionPrincipal("Dir 1").codigoPostal("1000").build();
        ClienteDTO mariaDTO = ClienteDTO.builder().nombreRazonSocial("Maria Gomez").documentoOCuit("222").telefono("123").email("m@m.com").direccionPrincipal("Dir 2").codigoPostal("2000").build();
        ClienteDTO carlosDTO = ClienteDTO.builder().nombreRazonSocial("Carlos Luis").documentoOCuit("333").telefono("123").email("c@c.com").direccionPrincipal("Dir 3").codigoPostal("3000").build();
        ClienteDTO pedroDTO = ClienteDTO.builder().nombreRazonSocial("Pedro G").documentoOCuit("444").telefono("123").email("p@p.com").direccionPrincipal("Dir 4").codigoPostal("4000").build();
        ClienteDTO hectorDTO = ClienteDTO.builder().nombreRazonSocial("Hector").documentoOCuit("555").telefono("123").email("h@h.com").direccionPrincipal("Dir 5").codigoPostal("5000").build();
        ClienteDTO aleDTO = ClienteDTO.builder().nombreRazonSocial("Ale").documentoOCuit("666").telefono("123").email("a@a.com").direccionPrincipal("Dir 6").codigoPostal("6000").build();

        Juan = clienteService.crearCliente(juanDTO);
        Maria = clienteService.crearCliente(mariaDTO);
        Carlos = clienteService.crearCliente(carlosDTO);
        Pedro = clienteService.crearCliente(pedroDTO);
        Hector = clienteService.crearCliente(hectorDTO);
        Ale = clienteService.crearCliente(aleDTO);
    }

    @Test
    void crearEnvio() {
        List<PaqueteDTO> paquetesDTO = new ArrayList<>();
        paquetesDTO.add(p1);

        EnvioDTO dto = new EnvioDTO();
        dto.setRemitente(Juan);
        dto.setDestinatario(Maria);
        dto.setDireccionEntrega("Calle Falsa 123");
        dto.setCodigoPostal("4600");

        dto.setPaquetes(paquetesDTO);

        EnvioDTO guardado = envioService.crearEnvio(dto);

        assertNotNull(guardado.getId());
        assertNotNull(guardado.getCodigoUnico());
        assertEquals(EstadoEnvio.GENERADO, guardado.getEstado());
        assertEquals(Juan.getId(), guardado.getRemitente().getId());
        assertEquals("Juan Perez", guardado.getRemitente().getNombreRazonSocial());
        assertEquals(Maria.getId(), guardado.getDestinatario().getId());
        assertEquals("Maria Gomez", guardado.getDestinatario().getNombreRazonSocial());
    }

    @Test
    void listarPorRemitente() {

        EnvioDTO dto1 = EnvioDTO.builder()
                .remitente(Juan)
                .destinatario(Pedro)
                .direccionEntrega("Calle 1")
                .codigoPostal("1001")
                .paquetes(List.of(p1))
                .build();
        envioService.crearEnvio(dto1);

        EnvioDTO dto2 = EnvioDTO.builder()
                .remitente(Carlos)
                .destinatario(Juan)
                .direccionEntrega("Calle 2")
                .codigoPostal("1002")
                .paquetes(List.of(p2))
                .build();
        envioService.crearEnvio(dto2);

        List<EnvioDTO> resultado = envioService.listarPorRemitente("111");
        assertEquals(1, resultado.size(), "Solo debería encontrar 1 envío con remitente '111'.");
        assertEquals(Juan.getId(), resultado.get(0).getRemitente().getId(), "El remitente del envío encontrado debe ser 'Juan'.");
    }

    @Test
    void listarPorDestinatario() {
        EnvioDTO dto1 = EnvioDTO.builder()
                .remitente(Juan)
                .destinatario(Ale)
                .direccionEntrega("Calle 1")
                .codigoPostal("1001")
                .paquetes(List.of(p1))
                .build();
        envioService.crearEnvio(dto1);


        EnvioDTO dto2 = EnvioDTO.builder()
                .remitente(Carlos)
                .destinatario(Hector)
                .direccionEntrega("Calle 2")
                .codigoPostal("1002")
                .paquetes(List.of(p2))
                .build();
        envioService.crearEnvio(dto2);

        List<EnvioDTO> resultado = envioService.listarPorDestinatario("555"); // Buscar por doc de Hector

        assertEquals(1, resultado.size(), "Solo debería encontrar 1 envío con destinatario '555'.");
        assertEquals(Hector.getId(), resultado.get(0).getDestinatario().getId(), "El destinatario del envío encontrado debe ser 'Hector'.");
    }
    private Envio crearEnvioBase() {
        EnvioDTO dto = EnvioDTO.builder()
                .remitente(Juan)
                .destinatario(Maria)
                .direccionEntrega("Calle Falsa 123")
                .codigoPostal("4600")
                .paquetes(List.of(p1))
                .build();

        EnvioDTO guardadoDTO = envioService.crearEnvio(dto);

        return envioRepository.findById(guardadoDTO.getId()).orElseThrow();
    }
    @Test
    void avanzarEstado_deberiaCambiarDeGeneradoAEnRutaYRegistrarHistorial() {
        Envio envio = crearEnvioBase();
        Long envioId = envio.getId();
        String observacion = "Sale a reparto con vehículo X.";

        envioService.avanzarEstado(envioId, observacion);

        Envio envioActualizado = envioRepository.findById(envioId).orElseThrow();
        assertEquals(EstadoEnvio.EN_ALMACEN, envioActualizado.getEstado(), "El estado debe cambiar a EN_RUTA.");
        assertEquals(2, HistorialEstadoEnvioRepository.count(), "Deben haber 2 registros en el historial: Generado y En Ruta.");

        envioService.avanzarEstado(envioId, observacion);
        assertEquals(EstadoEnvio.EN_RUTA, envioActualizado.getEstado(), "El estado debe cambiar a EN_RUTA.");

    }

    @Test
    void cancelarEnvio_deberiaCambiarAEstadoCanceladoYRegistrarHistorial() {
        Envio envio = crearEnvioBase();
        Long envioId = envio.getId();
        String observacion = "Cancelación solicitada por el remitente.";

        envioService.cancelarEnvio(envioId, observacion);

        Envio envioActualizado = envioRepository.findById(envioId).orElseThrow();
        assertEquals(EstadoEnvio.CANCELADO, envioActualizado.getEstado(), "El estado debe cambiar a CANCELADO.");

        assertEquals(2, HistorialEstadoEnvioRepository.count(), "Deben haber 2 registros en el historial: Generado y Cancelado.");
    }

    @Test
    void devolverEnvio_deberiaLanzarExcepcionDesdeEstadoGenerado() {
        Envio envio = crearEnvioBase();
        Long envioId = envio.getId();
        String observacion = "Intento de devolución prematura.";

        assertThrows(RuntimeException.class, () -> {
            envioService.devolverEnvio(envioId, observacion);
        }, "Devolver un envío en estado GENERADO debe lanzar una excepción.");

        Envio envioSinCambios = envioRepository.findById(envioId).orElseThrow();
        assertEquals(EstadoEnvio.GENERADO, envioSinCambios.getEstado(), "El estado debe permanecer en GENERADO.");

        assertEquals(1, HistorialEstadoEnvioRepository.count(), "Solo debe haber 1 registro (GENERADO) en el historial.");
    }
}
