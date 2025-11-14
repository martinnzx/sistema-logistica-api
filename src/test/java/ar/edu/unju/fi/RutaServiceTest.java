package ar.edu.unju.fi;

import ar.edu.unju.fi.dto.views.EnvioViewDTO;
import ar.edu.unju.fi.dto.views.RutaViewDTO;
import ar.edu.unju.fi.enums.EstadoEnvio;
import ar.edu.unju.fi.enums.NivelFragilidad;
import ar.edu.unju.fi.service.*;
import ar.edu.unju.fi.dto.*;
import ar.edu.unju.fi.service.EnvioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RutaServiceTest {
    @Autowired
    private EnvioService envioService;
    @Autowired
    private RutaService rutaService;
    @Autowired
    private VehiculoService vehiculoService;
    @Autowired
    private ClienteService clienteService;
    @Autowired
    private PaqueteService paqueteService;

    // --- Clientes (DTOs de respuesta) ---
    private ClienteDTO pedro;
    private ClienteDTO hector;
    private ClienteDTO ale;

    // --- Vehículos (DTOs de respuesta) ---
    private VehiculoDTO vehiculoDTO;
    private VehiculoDTO vehiculoNoRefrigerado;
    private VehiculoDTO vehiculoSinRango;

    // --- Envíos (DTOs de respuesta) ---
    private EnvioDTO envioGuardado1;
    private EnvioDTO envioGuardado2;
    private EnvioDTO envioPesado;
    private EnvioDTO envioVolumen;
    private EnvioDTO envioRefrigerado; // Refrigerado (Compatible)
    private EnvioDTO envioFueraDeRango; // Refrigerado (Incompatible)

    // --- DTOs de ENTRADA para los Tests ---
    private RutaViewDTO rutaViewDtoExito;
    private RutaViewDTO rutaViewDtoPesado;
    private RutaViewDTO rutaViewDtoVolumen;
    private RutaViewDTO rutaViewDtoNoRef;
    private RutaViewDTO rutaViewDtoSinRango;
    private RutaViewDTO rutaViewDtoFueraDeRango;


    @BeforeEach
    void setUp() {
        // 1. Clientes ---
        ClienteDTO pedroDTO = ClienteDTO.builder().nombreRazonSocial("Pedro G").documentoOCuit("20-44444444-4").telefono("123").email("p@p.com").direccionPrincipal("Dir 4").codigoPostal("4000").build();
        pedro = clienteService.crearCliente(pedroDTO);
        ClienteDTO hectorDTO = ClienteDTO.builder().nombreRazonSocial("Hector").documentoOCuit("20-55555555-5").telefono("123").email("h@h.com").direccionPrincipal("Dir 5").codigoPostal("5000").build();
        hector = clienteService.crearCliente(hectorDTO);
        ClienteDTO aleDTO = ClienteDTO.builder().nombreRazonSocial("Ale").documentoOCuit("20-66666666-6").telefono("123").email("a@a.com").direccionPrincipal("Dir 6").codigoPostal("6000").build();
        ale = clienteService.crearCliente(aleDTO);

        // 2. Vehículos ---
        vehiculoDTO = new VehiculoDTO("ABC123", 1000.0, 500.0, true, 1.0, 10.0);
        vehiculoDTO = vehiculoService.crearVehiculo(vehiculoDTO);

        vehiculoNoRefrigerado = new VehiculoDTO("ZZZ999", 1000.0, 500.0, false, null, null);
        vehiculoNoRefrigerado = vehiculoService.crearVehiculo(vehiculoNoRefrigerado);

        vehiculoSinRango = new VehiculoDTO("RNG000", 1000.0, 500.0, true, null, null);
        vehiculoSinRango = vehiculoService.crearVehiculo(vehiculoSinRango);

        PaqueteDTO p1 = PaqueteDTO.builder()
                .codigo("P-001").pesoKg(5.0).volumenDm3(10.0).tipo("Fragil")
                .nivelFragilidad(NivelFragilidad.ALTA).seguroAdicional(true).build();
        PaqueteDTO p2 = PaqueteDTO.builder()
                .codigo("P-002").pesoKg(2.0).volumenDm3(4.0).tipo("Fragil")
                .nivelFragilidad(NivelFragilidad.BAJA).seguroAdicional(false).build();
        PaqueteDTO pPesado = PaqueteDTO.builder()
                .codigo("P-999").pesoKg(1001.0).volumenDm3(10.0).tipo("Fragil")
                .nivelFragilidad(NivelFragilidad.BAJA).seguroAdicional(false).build();
        PaqueteDTO pVolumen = PaqueteDTO.builder()
                .codigo("P-888").pesoKg(10.0).volumenDm3(501.0).tipo("Fragil")
                .nivelFragilidad(NivelFragilidad.BAJA).seguroAdicional(false).build();
        PaqueteDTO pRefrigerado = PaqueteDTO.builder()
                .codigo("P-777").pesoKg(10.0).volumenDm3(10.0).tipo("Refrigerado")
                .temperaturaObjetivo(5.0).rangoMin(2.0).rangoMax(8.0).horasMaxFueraDeFrio(1).build();
        PaqueteDTO pRefFrio = PaqueteDTO.builder()
                .codigo("P-555").pesoKg(10.0).volumenDm3(10.0).tipo("Refrigerado")
                .temperaturaObjetivo(-5.0).rangoMin(-10.0).rangoMax(0.0).horasMaxFueraDeFrio(1).build();

        paqueteService.crearPaquete(p1);
        paqueteService.crearPaquete(p2);
        paqueteService.crearPaquete(pPesado);
        paqueteService.crearPaquete(pVolumen);
        paqueteService.crearPaquete(pRefrigerado);
        paqueteService.crearPaquete(pRefFrio);

        // 4. Envíos (creados en la BD usando EnvioViewDTO) ---
        // (Se crean y LUEGO se avanzan a EN_ALMACEN para que la Ruta los pueda tomar)

        // Envío 1 (Normal)
        EnvioViewDTO viewDtoEnvio1 = EnvioViewDTO.builder().cuilRemitente(pedro.getDocumentoOCuit()).cuilDestinatario(hector.getDocumentoOCuit()).direccionEntrega("Calle Falsa 123").codigoPostal("4600").paquetes(List.of("P-001")).build();
        envioGuardado1 = envioService.crearEnvio(viewDtoEnvio1);
        envioService.avanzarEstado(envioGuardado1.getCodigoUnico(), "Test: Listo para ruta"); // -> EN_ALMACEN

        // Envío 2 (Normal)
        EnvioViewDTO viewDtoEnvio2 = EnvioViewDTO.builder().cuilRemitente(hector.getDocumentoOCuit()).cuilDestinatario(ale.getDocumentoOCuit()).direccionEntrega("Av Siempre Viva 742").codigoPostal("4601").paquetes(List.of("P-002")).build();
        envioGuardado2 = envioService.crearEnvio(viewDtoEnvio2);
        envioService.avanzarEstado(envioGuardado2.getCodigoUnico(), "Test: Listo para ruta"); // -> EN_ALMACEN

        // Envío Sobrepeso
        // ⬇️ CORRECCIÓN: Usamos cuilRemitente y cuilDestinatario
        EnvioViewDTO viewDtoPesado = EnvioViewDTO.builder()
                .cuilRemitente(pedro.getDocumentoOCuit())
                .cuilDestinatario(hector.getDocumentoOCuit())
                .direccionEntrega(hector.getDireccionPrincipal())
                .codigoPostal(hector.getCodigoPostal())
                .paquetes(List.of("P-999")).build();
        envioPesado = envioService.crearEnvio(viewDtoPesado);
        envioService.avanzarEstado(envioPesado.getCodigoUnico(), "Test: Listo para ruta"); // -> EN_ALMACEN

        EnvioViewDTO viewDtoVol = EnvioViewDTO.builder()
                .cuilRemitente(pedro.getDocumentoOCuit())
                .cuilDestinatario(hector.getDocumentoOCuit())
                .direccionEntrega(hector.getDireccionPrincipal())
                .codigoPostal(hector.getCodigoPostal())
                .paquetes(List.of("P-888")).build();
        envioVolumen = envioService.crearEnvio(viewDtoVol);
        envioService.avanzarEstado(envioVolumen.getCodigoUnico(), "Test: Listo para ruta"); // -> EN_ALMACEN

        EnvioViewDTO viewDtoRef = EnvioViewDTO.builder()
                .cuilRemitente(pedro.getDocumentoOCuit())
                .cuilDestinatario(hector.getDocumentoOCuit())
                .direccionEntrega(hector.getDireccionPrincipal())
                .codigoPostal(hector.getCodigoPostal())
                .paquetes(List.of("P-777")).build();
        envioRefrigerado = envioService.crearEnvio(viewDtoRef);
        envioService.avanzarEstado(envioRefrigerado.getCodigoUnico(), "Test: Listo para ruta"); // -> EN_ALMACEN


        EnvioViewDTO viewDtoFrio = EnvioViewDTO.builder()
                .cuilRemitente(pedro.getDocumentoOCuit())
                .cuilDestinatario(hector.getDocumentoOCuit())
                .direccionEntrega(hector.getDireccionPrincipal())
                .codigoPostal(hector.getCodigoPostal())
                .paquetes(List.of("P-555")).build();
        envioFueraDeRango = envioService.crearEnvio(viewDtoFrio);
        envioService.avanzarEstado(envioFueraDeRango.getCodigoUnico(), "Test: Listo para ruta"); // -> EN_ALMACEN


        // 5. DTOs de ENTRADA de Ruta (RutaViewDTO) ---
        rutaViewDtoExito = RutaViewDTO.builder()
                .fecha(LocalDate.now())
                .patenteVehiculo(vehiculoDTO.getPatente()) // "ABC123"
                .codigoEnvios(List.of(envioGuardado1.getCodigoUnico(), envioGuardado2.getCodigoUnico()))
                .build();

        rutaViewDtoPesado = RutaViewDTO.builder()
                .fecha(LocalDate.now())
                .patenteVehiculo(vehiculoDTO.getPatente())
                .codigoEnvios(List.of(envioPesado.getCodigoUnico()))
                .build();

        rutaViewDtoVolumen = RutaViewDTO.builder()
                .fecha(LocalDate.now())
                .patenteVehiculo(vehiculoDTO.getPatente())
                .codigoEnvios(List.of(envioVolumen.getCodigoUnico()))
                .build();

        rutaViewDtoNoRef = RutaViewDTO.builder()
                .fecha(LocalDate.now())
                .patenteVehiculo(vehiculoNoRefrigerado.getPatente()) // "ZZZ999" (No Refrigerado)
                .codigoEnvios(List.of(envioRefrigerado.getCodigoUnico())) // Envío Refrigerado
                .build();

        rutaViewDtoSinRango = RutaViewDTO.builder()
                .fecha(LocalDate.now())
                .patenteVehiculo(vehiculoSinRango.getPatente()) // "RNG000" (Ref, pero sin rango)
                .codigoEnvios(List.of(envioRefrigerado.getCodigoUnico())) // Envío Refrigerado
                .build();

        rutaViewDtoFueraDeRango = RutaViewDTO.builder()
                .fecha(LocalDate.now())
                .patenteVehiculo(vehiculoDTO.getPatente()) // "ABC123" (Rango 1 a 10)
                .codigoEnvios(List.of(envioFueraDeRango.getCodigoUnico())) // Envío Frio (Rango -10 a 0)
                .build();
    }

    // ==========================================================
    // --- TESTS DE ÉXITO (Sin cambios) ---
    // ==========================================================
    @Test
    void crearRuta_debeCrearExitosamenteYActualizarEnvios() {
        RutaDTO guardada = rutaService.crearRuta(rutaViewDtoExito);

        assertNotNull(guardada, "La ruta guardada no debe ser nula");
        assertNotNull(guardada.getId(), "La ruta debe tener un ID");
        assertNotNull(guardada.getVehiculo(), "El vehículo del DTO no debe ser null");
        assertEquals("ABC123", guardada.getVehiculo().getPatente(), "El vehículo debe coincidir");
        assertEquals(LocalDate.now(), guardada.getFecha(), "La fecha debe coincidir");
        assertEquals(2, guardada.getEnvios().size(), "Debe tener 2 envíos");

        // Verificamos que los envíos se hayan actualizado a EN_RUTA
        EnvioDTO envioActualizado1 = envioService.obtenerEnvioPorCodigo(envioGuardado1.getCodigoUnico());
        EnvioDTO envioActualizado2 = envioService.obtenerEnvioPorCodigo(envioGuardado2.getCodigoUnico());

        assertEquals(EstadoEnvio.EN_ALMACEN, envioActualizado1.getEstado(), "Envío 1 debe estar EN_RUTA");
        assertEquals(EstadoEnvio.EN_ALMACEN, envioActualizado2.getEstado(), "Envío 2 debe estar EN_RUTA");
    }

    @Test
    void obtenerEnviosPorRutaYFecha_debeRetornarRutaCorrectamente() {
        RutaDTO rutaGuardada = rutaService.crearRuta(rutaViewDtoExito);
        assertNotNull(rutaGuardada.getId());

        List<RutaDTO> rutasEncontradas = rutaService.obtenerEnviosPorRutaYFecha(
                rutaGuardada.getId(),
                LocalDate.now()
        );

        assertNotNull(rutasEncontradas, "La lista de rutas no debe ser null");
        assertEquals(1, rutasEncontradas.size(), "Debe devolver 1 ruta");

        RutaDTO dto = rutasEncontradas.getFirst();
        assertEquals(rutaGuardada.getId(), dto.getId());
        assertEquals(2, dto.getEnvios().size(), "La ruta debe tener 2 envíos");
        assertEquals("ABC123", dto.getVehiculo().getPatente(), "El vehículo debe coincidir");
    }

    // ==========================================================
    // --- TESTS DE FALLA (VALIDACIONES) (Sin cambios) ---
    // ==========================================================

    @Test
    void crearRuta_debeLanzarExcepcion_siExcedePesoMaximo() {
        assertThrows(IllegalArgumentException.class, () -> {
            rutaService.crearRuta(rutaViewDtoPesado);
        });
    }

    @Test
    void crearRuta_debeLanzarExcepcion_siExcedeVolumenMaximo() {
        assertThrows(IllegalArgumentException.class, () -> {
            rutaService.crearRuta(rutaViewDtoVolumen);
        });
    }

    @Test
    void crearRuta_debeLanzarExcepcion_siPaqueteRefrigeradoEnVehiculoNoRefrigerado() {
        assertThrows(IllegalArgumentException.class, () -> {
            rutaService.crearRuta(rutaViewDtoNoRef);
        });
    }

    @Test
    void crearRuta_debeLanzarExcepcion_siVehiculoRefrigeradoNoTieneRangosTemp() {
        // ⬇️ CORRECCIÓN: Este caso SÍ debe lanzar IllegalStateException
        assertThrows(IllegalStateException.class, () -> {
            rutaService.crearRuta(rutaViewDtoSinRango);
        });
    }

    @Test
    void crearRuta_debeLanzarExcepcion_siTempPaqueteFueraDeRangoVehiculo() {
        assertThrows(IllegalStateException.class, () -> {
            rutaService.crearRuta(rutaViewDtoFueraDeRango);
        });
    }
}