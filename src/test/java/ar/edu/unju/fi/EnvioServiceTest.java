package ar.edu.unju.fi;

import ar.edu.unju.fi.dto.views.EnvioViewDTO;
import ar.edu.unju.fi.dto.views.EnvioViewDestinatarioDTO;
import ar.edu.unju.fi.dto.views.EnvioViewRemitenteDTO;
import ar.edu.unju.fi.model.HistorialEstadoEnvio;
import ar.edu.unju.fi.service.ClienteService;
import ar.edu.unju.fi.service.EnvioService;
import ar.edu.unju.fi.dto.ClienteDTO;
import ar.edu.unju.fi.dto.EnvioDTO;
import ar.edu.unju.fi.dto.PaqueteDTO;
import ar.edu.unju.fi.enums.EstadoEnvio;
import ar.edu.unju.fi.enums.NivelFragilidad;

import ar.edu.unju.fi.service.PaqueteService;
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

    @Autowired
    private PaqueteService paqueteService;

    private PaqueteDTO p1, p2, pRefrigerado1, pRefrigerado2;
    private EnvioViewDTO envioViewDtoJuanAMaria;
    private EnvioViewDTO envioViewDtoCarlosAJuan;
    private EnvioViewDTO envioViewDtoJuanAAle;
    private EnvioViewDTO envioViewDtoCarlosAHector;
    private EnvioViewDTO envioViewRefrigerado;
    private EnvioViewDTO envioViewMixto;

    private ClienteDTO juan, maria, carlos, hector, ale;

    @BeforeEach
    void setUp() {
        //  1. --- Paquetes (DTOs) ---
        p1 = PaqueteDTO.builder()
                .codigo("P-001")
                .pesoKg(5.0)
                .volumenDm3(10.0)
                .tipo("Fragil")
                .nivelFragilidad(NivelFragilidad.ALTA)
                .seguroAdicional(true)
                .build();

        p2 = PaqueteDTO.builder()
                .codigo("P-002")
                .pesoKg(2.0)
                .volumenDm3(4.0)
                .tipo("Fragil")
                .nivelFragilidad(NivelFragilidad.BAJA)
                .seguroAdicional(false)
                .build();

        pRefrigerado1 = PaqueteDTO.builder()
                .codigo("P-REF-01")
                .pesoKg(3.0)
                .volumenDm3(5.0)
                .tipo("Refrigerado")
                .temperaturaObjetivo(5.0)
                .rangoMin(2.0)
                .rangoMax(8.0)
                .horasMaxFueraDeFrio(2)
                .build();

        pRefrigerado2 = PaqueteDTO.builder()
                .codigo("P-REF-02")
                .pesoKg(4.0)
                .volumenDm3(6.0)
                .tipo("Refrigerado")
                .temperaturaObjetivo(4.0)
                .rangoMin(1.0)
                .rangoMax(5.0)
                .horasMaxFueraDeFrio(1)
                .build();

        paqueteService.crearPaquete(p1);
        paqueteService.crearPaquete(p2);
        paqueteService.crearPaquete(pRefrigerado1);
        paqueteService.crearPaquete(pRefrigerado2);

        // 2. --- Clientes (DTOs y creación) ---
        ClienteDTO juanDTO = ClienteDTO.builder().nombreRazonSocial("Juan Perez").documentoOCuit("20-11111111-1").telefono("123").email("j@j.com").direccionPrincipal("Dir 1").codigoPostal("1000").build();
        ClienteDTO mariaDTO = ClienteDTO.builder().nombreRazonSocial("Maria Gomez").documentoOCuit("27-22222222-2").telefono("123").email("m@m.com").direccionPrincipal("Dir 2").codigoPostal("2000").build();
        ClienteDTO carlosDTO = ClienteDTO.builder().nombreRazonSocial("Carlos Luis").documentoOCuit("20-33333333-3").telefono("123").email("c@c.com").direccionPrincipal("Dir 3").codigoPostal("3000").build();
        ClienteDTO pedroDTO = ClienteDTO.builder().nombreRazonSocial("Pedro G").documentoOCuit("20-44444444-4").telefono("123").email("p@p.com").direccionPrincipal("Dir 4").codigoPostal("4000").build();
        ClienteDTO hectorDTO = ClienteDTO.builder().nombreRazonSocial("Hector").documentoOCuit("20-55555555-5").telefono("123").email("h@h.com").direccionPrincipal("Dir 5").codigoPostal("5000").build();
        ClienteDTO aleDTO = ClienteDTO.builder().nombreRazonSocial("Ale").documentoOCuit("20-66666666-6").telefono("123").email("a@a.com").direccionPrincipal("Dir 6").codigoPostal("6000").build();

        juan = clienteService.crearCliente(juanDTO);
        maria = clienteService.crearCliente(mariaDTO);
        carlos = clienteService.crearCliente(carlosDTO);
        clienteService.crearCliente(pedroDTO);
        hector = clienteService.crearCliente(hectorDTO);
        ale = clienteService.crearCliente(aleDTO);

        // 3. --- Setup EnvioViewDTOs (DTOs de entrada) ---
        envioViewDtoJuanAMaria = EnvioViewDTO.builder()
                .cuilRemitente("20-11111111-1") // Juan
                .cuilDestinatario("27-22222222-2") // Maria
                .direccionEntrega("Calle Falsa 123")
                .codigoPostal("4600")
                .paquetes(List.of("P-001")) // Código de p1
                .build();

        envioViewDtoCarlosAJuan = EnvioViewDTO.builder()
                .cuilRemitente("20-33333333-3") // Carlos
                .cuilDestinatario("20-11111111-1") // Juan
                .direccionEntrega("Calle 2")
                .codigoPostal("1002")
                .paquetes(List.of("P-002")) // Código de p2
                .build();

        envioViewDtoJuanAAle = EnvioViewDTO.builder()
                .cuilRemitente("20-11111111-1") // Juan
                .cuilDestinatario("20-66666666-6") // Ale
                .direccionEntrega("Calle 1")
                .codigoPostal("1001")
                .paquetes(List.of("P-001")) // Usa P-001
                .build();

        envioViewDtoCarlosAHector = EnvioViewDTO.builder()
                .cuilRemitente("20-33333333-3") // Carlos
                .cuilDestinatario("20-55555555-5") // Hector
                .direccionEntrega("Calle 2")
                .codigoPostal("1002")
                .paquetes(List.of("P-002")) // Usa P-002
                .build();

        envioViewRefrigerado = EnvioViewDTO.builder()
                .cuilRemitente("20-11111111-1") // Juan
                .cuilDestinatario("27-22222222-2") // Maria
                .direccionEntrega("Calle Fria 456")
                .codigoPostal("4600")
                .paquetes(List.of("P-REF-01")) // Código refrigerado 1
                .build();

        envioViewMixto = EnvioViewDTO.builder()
                .cuilRemitente("20-33333333-3") // Carlos
                .cuilDestinatario("20-66666666-6") // Ale
                .direccionEntrega("Calle Mixta 789")
                .codigoPostal("3000")
                .paquetes(List.of("P-001", "P-REF-02")) // Usa P-001 y refrigerado 2
                .build();
    }

    @Test
    void crearEnvio() {
        EnvioDTO guardado = envioService.crearEnvio(envioViewDtoJuanAMaria);
        assertNotNull(guardado.getId());
        assertNotNull(guardado.getCodigoUnico());
        assertNotEquals("TEMP-001", guardado.getCodigoUnico());
        assertEquals(EstadoEnvio.GENERADO, guardado.getEstado());
        assertEquals(juan.getDocumentoOCuit(), guardado.getRemitente().getDocumentoOCuit());
        assertEquals(maria.getDocumentoOCuit(), guardado.getDestinatario().getDocumentoOCuit());
    }

    @Test
    void listarPorRemitente() {
        envioService.crearEnvio(envioViewDtoJuanAMaria);
        envioService.crearEnvio(envioViewDtoCarlosAJuan);
        List<EnvioViewRemitenteDTO> resultado = envioService.listarPorRemitente("20-11111111-1"); // Documento de Juan
        assertEquals(1, resultado.size());
        assertEquals(juan.getNombreRazonSocial(), resultado.getFirst().getRemitenteNombre());
    }

    @Test
    void listarPorDestinatario() {
        envioService.crearEnvio(envioViewDtoJuanAAle);
        envioService.crearEnvio(envioViewDtoCarlosAHector);
        List<EnvioViewDestinatarioDTO> resultado = envioService.listarPorDestinatario("20-55555555-5"); // Buscar por doc de Hector
        assertEquals(1, resultado.size());
        assertEquals(hector.getNombreRazonSocial(), resultado.getFirst().getDestinatarioNombre());
    }

    // Helper actualizado para usar el DTO definido en setUp
    private EnvioDTO crearEnvioBaseDto() {
        return envioService.crearEnvio(envioViewDtoJuanAMaria);
    }

    // --- Tests de Cambio de Estado (Actualizados) ---

    @Test
    void avanzarEstado_deberiaCambiarDeGeneradoAEnRutaYRegistrarHistorial() {
        EnvioDTO envioCreado = crearEnvioBaseDto();
        // ⬇️ CAMBIO: Ya no usamos el Long id, usamos el String codigoUnico
        String codigoUnico = envioCreado.getCodigoUnico();
        String observacion = "Sale a reparto con vehículo X.";

        // ⬇️ CAMBIO: Pasamos el 'codigoUnico' (String)
        envioService.avanzarEstado(codigoUnico, observacion);

        EnvioDTO envioActualizado = envioService.obtenerEnvioPorCodigo(codigoUnico);
        assertEquals(EstadoEnvio.EN_ALMACEN, envioActualizado.getEstado(), "El estado debe cambiar a EN_ALMACEN.");

        List<HistorialEstadoEnvio> historial = envioService.obtenerHistorialPorCodigo(codigoUnico);
        assertEquals(2, historial.size(), "Deben haber 2 registros en el historial: Generado y En Almacén.");

        // ⬇️ CAMBIO: Pasamos el 'codigoUnico' (String)
        envioService.avanzarEstado(codigoUnico, observacion);

        EnvioDTO envioActualizado2 = envioService.obtenerEnvioPorCodigo(codigoUnico);
        assertEquals(EstadoEnvio.EN_RUTA, envioActualizado2.getEstado(), "El estado debe cambiar a EN_RUTA.");
    }

    @Test
    void cancelarEnvio_deberiaCambiarAEstadoCanceladoYRegistrarHistorial() {
        EnvioDTO envioCreado = crearEnvioBaseDto();
        // ⬇️ CAMBIO: Ya no usamos el Long id, usamos el String codigoUnico
        String codigoUnico = envioCreado.getCodigoUnico();
        String observacion = "Cancelación solicitada por el remitente.";

        // ⬇️ CAMBIO: Pasamos el 'codigoUnico' (String)
        envioService.cancelarEnvio(codigoUnico, observacion);

        EnvioDTO envioActualizado = envioService.obtenerEnvioPorCodigo(codigoUnico);
        assertEquals(EstadoEnvio.CANCELADO, envioActualizado.getEstado(), "El estado debe cambiar a CANCELADO.");

        List<HistorialEstadoEnvio> historial = envioService.obtenerHistorialPorCodigo(codigoUnico);
        assertEquals(2, historial.size(), "Deben haber 2 registros en el historial: Generado y Cancelado.");
    }

    @Test
    void devolverEnvio_deberiaLanzarExcepcionDesdeEstadoGenerado() {
        EnvioDTO envioCreado = crearEnvioBaseDto();
        // ⬇️ CAMBIO: Ya no usamos el Long id, usamos el String codigoUnico
        String codigoUnico = envioCreado.getCodigoUnico();
        String observacion = "Intento de devolución prematura.";

        // ⬇️ CAMBIO: Pasamos el 'codigoUnico' (String) al lambda
        assertThrows(IllegalStateException.class,
                () -> envioService.devolverEnvio(codigoUnico, observacion),
                "Devolver un envío en estado GENERADO debe lanzar una excepción."
        );
    }

    // --- Tests de Lógica de Refrigerado (Sin cambios) ---

    @Test
    void crearEnvio_conPaquetesRefrigerados_debeMarcarRequerirFrio() {
        EnvioDTO guardadoRef = envioService.crearEnvio(envioViewRefrigerado);
        assertNotNull(guardadoRef);
        assertTrue(guardadoRef.getRequiereFrio(), "El envío solo con paquetes refrigerados debe marcarse como 'requiereFrio'");
        assertEquals(1, guardadoRef.getPaquetes().size());
        assertEquals("Refrigerado", guardadoRef.getPaquetes().getFirst().getTipo());

        EnvioDTO guardadoMixto = envioService.crearEnvio(envioViewMixto);
        assertNotNull(guardadoMixto);
        assertTrue(guardadoMixto.getRequiereFrio(), "Un envío mixto (frágil + refrigerado) debe marcarse como 'requiereFrio'");
        assertEquals(2, guardadoMixto.getPaquetes().size());
    }

    @Test
    void crearEnvio_sinPaquetesRefrigerados_noDebeMarcarRequerirFrio() {
        EnvioDTO guardado = envioService.crearEnvio(envioViewDtoJuanAMaria); // Usa P-001 (Fragil)
        assertNotNull(guardado);
        assertFalse(guardado.getRequiereFrio(), "El envío solo con paquetes frágiles NO debe marcarse como 'requiereFrio'");
    }
}
