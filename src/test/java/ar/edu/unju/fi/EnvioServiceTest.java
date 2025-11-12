package ar.edu.unju.fi;

import ar.edu.unju.fi.model.HistorialEstadoEnvio;
import ar.edu.unju.fi.service.ClienteService;
import ar.edu.unju.fi.service.EnvioService;
import ar.edu.unju.fi.dto.ClienteDTO;
import ar.edu.unju.fi.dto.EnvioDTO;
import ar.edu.unju.fi.dto.PaqueteDTO;
import ar.edu.unju.fi.enums.EstadoEnvio;
import ar.edu.unju.fi.enums.NivelFragilidad;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class EnvioServiceTest {

    @Autowired
    private EnvioService envioService;

    @Autowired
    private ClienteService clienteService;

    private PaqueteDTO p1;
    private PaqueteDTO p2;
    private PaqueteDTO pRefrigerado;

    private EnvioDTO envioDtoJuanAMaria;
    private EnvioDTO envioDtoCarlosAJuan;
    private EnvioDTO envioDtoJuanAAle;
    private EnvioDTO envioDtoCarlosAHector;
    private EnvioDTO envioRefrigerado;
    private EnvioDTO envioMixto;

    private ClienteDTO juan;
    private ClienteDTO maria;
    private ClienteDTO carlos;
    private ClienteDTO hector;
    private ClienteDTO ale;

    @BeforeEach
    void setUp() {
        //  Paquetes ---
        p1 = PaqueteDTO.builder()
                .codigo("P-001")
                .pesoKg(5.0)
                .volumenDm3(10.0)
                .nivelFragilidad(NivelFragilidad.ALTA)
                .seguroAdicional(true)
                .tipo("Fragil")
                .build();

        p2 = PaqueteDTO.builder()
                .codigo("P-002")
                .pesoKg(2.0)
                .volumenDm3(4.0)
                .nivelFragilidad(NivelFragilidad.BAJA)
                .tipo("Fragil")
                .seguroAdicional(false)
                .build();
        pRefrigerado = PaqueteDTO.builder()
                .codigo("P-REF-01")
                .pesoKg(3.0)
                .volumenDm3(5.0)
                .tipo("Refrigerado")
                .temperaturaObjetivo(5.0)
                .rangoMin(2.0)
                .rangoMax(8.0)
                .horasMaxFueraDeFrio(2)
                .build();
        // --- DTOs de Clientes---
        ClienteDTO juanDTO = ClienteDTO.builder().nombreRazonSocial("Juan Perez").documentoOCuit("111").telefono("123").email("j@j.com").direccionPrincipal("Dir 1").codigoPostal("1000").build();
        ClienteDTO mariaDTO = ClienteDTO.builder().nombreRazonSocial("Maria Gomez").documentoOCuit("222").telefono("123").email("m@m.com").direccionPrincipal("Dir 2").codigoPostal("2000").build();
        ClienteDTO carlosDTO = ClienteDTO.builder().nombreRazonSocial("Carlos Luis").documentoOCuit("333").telefono("123").email("c@c.com").direccionPrincipal("Dir 3").codigoPostal("3000").build();
        ClienteDTO pedroDTO = ClienteDTO.builder().nombreRazonSocial("Pedro G").documentoOCuit("444").telefono("123").email("p@p.com").direccionPrincipal("Dir 4").codigoPostal("4000").build();
        ClienteDTO hectorDTO = ClienteDTO.builder().nombreRazonSocial("Hector").documentoOCuit("555").telefono("123").email("h@h.com").direccionPrincipal("Dir 5").codigoPostal("5000").build();
        ClienteDTO aleDTO = ClienteDTO.builder().nombreRazonSocial("Ale").documentoOCuit("666").telefono("123").email("a@a.com").direccionPrincipal("Dir 6").codigoPostal("6000").build();

        juan = clienteService.crearCliente(juanDTO);
        maria = clienteService.crearCliente(mariaDTO);
        carlos = clienteService.crearCliente(carlosDTO);
        clienteService.crearCliente(pedroDTO);
        hector = clienteService.crearCliente(hectorDTO);
        ale = clienteService.crearCliente(aleDTO);

        // --- Setup DTOs de Envío ---
        envioDtoJuanAMaria = EnvioDTO.builder()
                .remitente(juan)
                .destinatario(maria)
                .direccionEntrega("Calle Falsa 123")
                .codigoPostal("4600")
                .paquetes(List.of(p1))
                .build();

        envioDtoCarlosAJuan = EnvioDTO.builder()
                .remitente(carlos)
                .destinatario(juan)
                .direccionEntrega("Calle 2")
                .codigoPostal("1002")
                .paquetes(List.of(p2))
                .build();

        envioDtoJuanAAle = EnvioDTO.builder()
                .remitente(juan)
                .destinatario(ale)
                .direccionEntrega("Calle 1")
                .codigoPostal("1001")
                .paquetes(List.of(p1))
                .build();

        envioDtoCarlosAHector = EnvioDTO.builder()
                .remitente(carlos)
                .destinatario(hector)
                .direccionEntrega("Calle 2")
                .codigoPostal("1002")
                .paquetes(List.of(p2))
                .build();
        envioRefrigerado = EnvioDTO.builder()
                .remitente(juan)
                .destinatario(maria)
                .direccionEntrega("Calle Fria 456")
                .codigoPostal("4600")
                .paquetes(List.of(pRefrigerado))
                .build();

        envioMixto = EnvioDTO.builder()
                .remitente(carlos)
                .destinatario(ale)
                .direccionEntrega("Calle Mixta 789")
                .codigoPostal("3000")
                .paquetes(List.of(p1, pRefrigerado))
                .build();
    }

    @Test
    void crearEnvio() {
        EnvioDTO guardado = envioService.crearEnvio(envioDtoJuanAMaria);

        assertNotNull(guardado.getId());
        assertNotNull(guardado.getCodigoUnico());
        assertEquals(EstadoEnvio.GENERADO, guardado.getEstado());
        assertEquals(juan.getDocumentoOCuit(), guardado.getRemitente().getDocumentoOCuit());
        assertEquals(maria.getDocumentoOCuit(), guardado.getDestinatario().getDocumentoOCuit());
    }

    @Test
    void listarPorRemitente() {
        envioService.crearEnvio(envioDtoJuanAMaria);
        envioService.crearEnvio(envioDtoCarlosAJuan);

        List<EnvioDTO> resultado = envioService.listarPorRemitente("111"); // Documento de Juan

        assertEquals(1, resultado.size(), "Solo debería encontrar 1 envío con remitente '111'.");
        assertEquals(juan.getId(), resultado.getFirst().getRemitente().getId(), "El remitente del envío encontrado debe ser 'Juan'.");
    }

    @Test
    void listarPorDestinatario() {
        envioService.crearEnvio(envioDtoJuanAAle);
        envioService.crearEnvio(envioDtoCarlosAHector);

        List<EnvioDTO> resultado = envioService.listarPorDestinatario("555"); // Buscar por doc de Hector

        assertEquals(1, resultado.size(), "Solo debería encontrar 1 envío con destinatario '555'.");
        assertEquals(hector.getId(), resultado.getFirst().getDestinatario().getId(), "El destinatario del envío encontrado debe ser 'Hector'.");
    }

    private EnvioDTO crearEnvioBaseDto() {
        return envioService.crearEnvio(envioDtoJuanAMaria);
    }

    @Test
    void avanzarEstado_deberiaCambiarDeGeneradoAEnRutaYRegistrarHistorial() {
        EnvioDTO envioCreado = crearEnvioBaseDto();
        Long envioId = envioCreado.getId();
        String codigoUnico = envioCreado.getCodigoUnico();
        String observacion = "Sale a reparto con vehículo X.";

        envioService.avanzarEstado(envioId, observacion);
        EnvioDTO envioActualizado = envioService.obtenerEnvioPorCodigo(codigoUnico);
        assertEquals(EstadoEnvio.EN_ALMACEN, envioActualizado.getEstado(), "El estado debe cambiar a EN_ALMACEN.");

        List<HistorialEstadoEnvio> historial = envioService.obtenerHistorialPorCodigo(codigoUnico);
        assertEquals(2, historial.size(), "Deben haber 2 registros en el historial: Generado y En Almacén.");

        envioService.avanzarEstado(envioId, observacion);
        EnvioDTO envioActualizado2 = envioService.obtenerEnvioPorCodigo(codigoUnico);
        assertEquals(EstadoEnvio.EN_RUTA, envioActualizado2.getEstado(), "El estado debe cambiar a EN_RUTA.");
    }

    @Test
    void cancelarEnvio_deberiaCambiarAEstadoCanceladoYRegistrarHistorial() {
        EnvioDTO envioCreado = crearEnvioBaseDto();
        Long envioId = envioCreado.getId();
        String codigoUnico = envioCreado.getCodigoUnico();
        String observacion = "Cancelación solicitada por el remitente.";

        envioService.cancelarEnvio(envioId, observacion);

        EnvioDTO envioActualizado = envioService.obtenerEnvioPorCodigo(codigoUnico);
        assertEquals(EstadoEnvio.CANCELADO, envioActualizado.getEstado(), "El estado debe cambiar a CANCELADO.");

        List<HistorialEstadoEnvio> historial = envioService.obtenerHistorialPorCodigo(codigoUnico);
        assertEquals(2, historial.size(), "Deben haber 2 registros en el historial: Generado y Cancelado.");
    }

    @Test
    void devolverEnvio_deberiaLanzarExcepcionDesdeEstadoGenerado() {
        EnvioDTO envioCreado = crearEnvioBaseDto();
        Long envioId = envioCreado.getId();
        String codigoUnico = envioCreado.getCodigoUnico();
        String observacion = "Intento de devolución prematura.";

        assertThrows(RuntimeException.class,
                () -> envioService.devolverEnvio(envioId, observacion),
                "Devolver un envío en estado GENERADO debe lanzar una excepción."
        );

        EnvioDTO envioSinCambios = envioService.obtenerEnvioPorCodigo(codigoUnico);
        assertEquals(EstadoEnvio.GENERADO, envioSinCambios.getEstado(), "El estado debe permanecer en GENERADO.");

        List<HistorialEstadoEnvio> historial = envioService.obtenerHistorialPorCodigo(codigoUnico);
        assertEquals(1, historial.size(), "Solo debe haber 1 registro (GENERADO) en el historial.");
    }
    @Test
    void crearEnvio_conPaquetesRefrigerados_debeMarcarRequerirFrio() {
        EnvioDTO guardadoRef = envioService.crearEnvio(envioRefrigerado);

        assertNotNull(guardadoRef);
        assertTrue(guardadoRef.getRequiereFrio(), "El envío solo con paquetes refrigerados debe marcarse como 'requiereFrio'");
        assertEquals(1, guardadoRef.getPaquetes().size());
        assertEquals("Refrigerado", guardadoRef.getPaquetes().getFirst().getTipo());


        EnvioDTO guardadoMixto = envioService.crearEnvio(envioMixto);

        assertNotNull(guardadoMixto);
        assertTrue(guardadoMixto.getRequiereFrio(), "Un envío mixto (frágil + refrigerado) debe marcarse como 'requiereFrio'");
        assertEquals(2, guardadoMixto.getPaquetes().size());
    }

    @Test
    void crearEnvio_sinPaquetesRefrigerados_noDebeMarcarRequerirFrio() {
        EnvioDTO guardado = envioService.crearEnvio(envioDtoJuanAMaria);

        assertNotNull(guardado);
        assertFalse(guardado.getRequiereFrio(), "El envío solo con paquetes frágiles NO debe marcarse como 'requiereFrio'");
    }
}
