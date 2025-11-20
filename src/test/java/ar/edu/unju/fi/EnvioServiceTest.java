package ar.edu.unju.fi;

import ar.edu.unju.fi.dto.views.EnvioViewDTO;
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

    private ClienteDTO juan, maria;

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
        ClienteDTO pedroDTO = ClienteDTO.builder().nombreRazonSocial("Pedro G").documentoOCuit("20-44444444-4").telefono("123").email("p@p.com").direccionPrincipal("Dir 4").codigoPostal("4000").build();
        juan = clienteService.crearCliente(juanDTO);
        maria = clienteService.crearCliente(mariaDTO);
        clienteService.crearCliente(pedroDTO);

        // 3. --- Setup EnvioViewDTOs (DTOs de entrada) ---
        envioViewDtoJuanAMaria = EnvioViewDTO.builder()
                .cuilRemitente("20-11111111-1") // Juan
                .cuilDestinatario("27-22222222-2") // Maria
                .direccionEntrega("Calle Falsa 123")
                .codigoPostal("4600")
                .paquetes(List.of("P-001")) // Código de p1
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
    void crearEnvio_sinPaquetesRefrigerados_noDebeMarcarRequerirFrio() {
        EnvioDTO guardado = envioService.crearEnvio(envioViewDtoJuanAMaria); // Usa P-001 (Fragil)
        assertNotNull(guardado);
        assertFalse(guardado.getRequiereFrio(), "El envío solo con paquetes frágiles NO debe marcarse como 'requiereFrio'");
    }
}
