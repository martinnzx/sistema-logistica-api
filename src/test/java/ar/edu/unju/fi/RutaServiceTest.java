package ar.edu.unju.fi;

import ar.edu.unju.fi.enums.NivelFragilidad;
import ar.edu.unju.fi.service.*;
import ar.edu.unju.fi.dto.*;

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

    private ClienteDTO pedro;
    private ClienteDTO hector;
    private ClienteDTO ale;

    // Vehículos
    private VehiculoDTO vehiculoDTO;
    private VehiculoDTO vehiculoNoRefrigerado;
    private VehiculoDTO vehiculoSinRango;
    private EnvioDTO envioGuardado1;
    private EnvioDTO envioGuardado2;
    private EnvioDTO envioPesado;
    private EnvioDTO envioVolumen;
    private EnvioDTO envioRefrigerado; // Refrigerado (Compatible)
    private EnvioDTO envioFueraDeRango; // Refrigerado (Incompatible)

    private RutaDTO rutaDTO;

    @BeforeEach
    void setUp() {
        // Clientes ---
        ClienteDTO pedroDTO = ClienteDTO.builder().nombreRazonSocial("Pedro G").documentoOCuit("444").telefono("123").email("p@p.com").direccionPrincipal("Dir 4").codigoPostal("4000").build();
        pedro = clienteService.crearCliente(pedroDTO);
        ClienteDTO hectorDTO = ClienteDTO.builder().nombreRazonSocial("Hector").documentoOCuit("555").telefono("123").email("h@h.com").direccionPrincipal("Dir 5").codigoPostal("5000").build();
        hector = clienteService.crearCliente(hectorDTO);
        ClienteDTO aleDTO = ClienteDTO.builder().nombreRazonSocial("Ale").documentoOCuit("666").telefono("123").email("a@a.com").direccionPrincipal("Dir 6").codigoPostal("6000").build();
        ale = clienteService.crearCliente(aleDTO);

        // Vehículos ---
        vehiculoDTO = new VehiculoDTO("ABC123", 1000.0, 500.0, true, 1.0, 10.0);
        vehiculoDTO = vehiculoService.crearVehiculo(vehiculoDTO);

        vehiculoNoRefrigerado = new VehiculoDTO("ZZZ999", 1000.0, 500.0, false, null, null);
        vehiculoNoRefrigerado = vehiculoService.crearVehiculo(vehiculoNoRefrigerado);

        vehiculoSinRango = new VehiculoDTO("RNG000", 1000.0, 500.0, true, null, null);
        vehiculoSinRango = vehiculoService.crearVehiculo(vehiculoSinRango);

        // Envíos y Paquetes ---
        PaqueteDTO p1 = new PaqueteDTO();
        p1.setCodigo("P-001");
        p1.setPesoKg(5.0);
        p1.setVolumenDm3(10.0);
        p1.setTipo("Fragil");
        p1.setNivelFragilidad(NivelFragilidad.ALTA);
        p1.setSeguroAdicional(true);
        EnvioDTO dtoEnvio1 = EnvioDTO.builder().remitente(pedro).destinatario(hector).direccionEntrega("Calle Falsa 123").codigoPostal("4600").paquetes(List.of(p1)).build();
        envioGuardado1 = envioService.crearEnvio(dtoEnvio1);

        PaqueteDTO p2 = new PaqueteDTO();
        p2.setCodigo("P-002");
        p2.setPesoKg(2.0);
        p2.setVolumenDm3(4.0);
        p2.setTipo("Fragil");
        p2.setNivelFragilidad(NivelFragilidad.BAJA);
        p2.setSeguroAdicional(false);
        EnvioDTO dtoEnvio2 = EnvioDTO.builder().remitente(hector).destinatario(ale).direccionEntrega("Av Siempre Viva 742").codigoPostal("4601").paquetes(List.of(p2)).build();
        envioGuardado2 = envioService.crearEnvio(dtoEnvio2);

        // Envío Sobrepeso (Frágil)
        PaqueteDTO pPesado = new PaqueteDTO();
        pPesado.setCodigo("P-999");
        pPesado.setPesoKg(1001.0); // Sobrepeso
        pPesado.setVolumenDm3(10.0);
        pPesado.setTipo("Fragil");
        pPesado.setNivelFragilidad(NivelFragilidad.BAJA);
        pPesado.setSeguroAdicional(false);
        EnvioDTO dtoEnvioPesado = EnvioDTO.builder().remitente(pedro).destinatario(hector).direccionEntrega(hector.getDireccionPrincipal()).codigoPostal(hector.getCodigoPostal()).paquetes(List.of(pPesado)).build();
        envioPesado = envioService.crearEnvio(dtoEnvioPesado);

        // Envío Sobre-volumen (Frágil)
        PaqueteDTO pVolumen = new PaqueteDTO();
        pVolumen.setCodigo("P-888");
        pVolumen.setPesoKg(10.0);
        pVolumen.setVolumenDm3(501.0); // Sobre-volumen
        pVolumen.setTipo("Fragil");
        pVolumen.setNivelFragilidad(NivelFragilidad.BAJA);
        pVolumen.setSeguroAdicional(false);
        EnvioDTO dtoEnvioVol = EnvioDTO.builder().remitente(pedro).destinatario(hector).direccionEntrega(hector.getDireccionPrincipal()).codigoPostal(hector.getCodigoPostal()).paquetes(List.of(pVolumen)).build();
        envioVolumen = envioService.crearEnvio(dtoEnvioVol);

        // Envío Refrigerado
        PaqueteDTO pRefrigerado = new PaqueteDTO();
        pRefrigerado.setCodigo("P-777");
        pRefrigerado.setPesoKg(10.0);
        pRefrigerado.setVolumenDm3(10.0);
        pRefrigerado.setTipo("Refrigerado");
        pRefrigerado.setTemperaturaObjetivo(5.0);
        pRefrigerado.setRangoMin(2.0);
        pRefrigerado.setRangoMax(8.0);
        pRefrigerado.setHorasMaxFueraDeFrio(1);
        EnvioDTO dtoEnvioRef = EnvioDTO.builder().remitente(pedro).destinatario(hector).direccionEntrega(hector.getDireccionPrincipal()).codigoPostal(hector.getCodigoPostal()).paquetes(List.of(pRefrigerado)).build();
        envioRefrigerado = envioService.crearEnvio(dtoEnvioRef);

        // Envío Fuera de Rango (Refrigerado)
        PaqueteDTO pRefFrio = new PaqueteDTO();
        pRefFrio.setCodigo("P-555");
        pRefFrio.setPesoKg(10.0);
        pRefFrio.setVolumenDm3(10.0);
        pRefFrio.setTipo("Refrigerado");
        pRefFrio.setTemperaturaObjetivo(-5.0);
        pRefFrio.setRangoMin(-10.0);
        pRefFrio.setRangoMax(0.0);
        pRefFrio.setHorasMaxFueraDeFrio(1);
        EnvioDTO dtoEnvioFrio = EnvioDTO.builder().remitente(pedro).destinatario(hector).direccionEntrega(hector.getDireccionPrincipal()).codigoPostal(hector.getCodigoPostal()).paquetes(List.of(pRefFrio)).build();
        envioFueraDeRango = envioService.crearEnvio(dtoEnvioFrio);

        rutaDTO = new RutaDTO();
        rutaDTO.setFecha(LocalDate.now());
        rutaDTO.setVehiculo(vehiculoDTO);
        rutaDTO.setEnvios(List.of(envioGuardado1, envioGuardado2));
    }

    // ==========================================================
    // --- TESTS DE ÉXITO ---
    // ==========================================================
    @Test
    void crearRuta_debeCrearExitosamente() {
        RutaDTO guardada = rutaService.crearRuta(rutaDTO);
        assertNotNull(guardada, "La ruta guardada no debe ser nula");
        assertNotNull(guardada.getId(), "La ruta debe tener un ID");
        assertNotNull(guardada.getVehiculo(), "El vehículo del DTO no debe ser null");
        assertEquals("ABC123", guardada.getVehiculo().getPatente(), "El vehículo debe coincidir");
        assertEquals(LocalDate.now(), guardada.getFecha(), "La fecha debe coincidir");
        assertEquals(2, guardada.getEnvios().size(), "Debe tener 2 envíos");
    }

    @Test
    void obtenerEnviosPorRutaYFecha_debeRetornarRutaCorrectamente() {
        RutaDTO rutaGuardada = rutaService.crearRuta(rutaDTO);
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
        assertEquals(LocalDate.now(), dto.getFecha(), "La fecha debe coincidir");
    }

    // ==========================================================
    // --- TESTS DE FALLA (VALIDACIONES) ---
    // ==========================================================

    @Test
    void crearRuta_debeLanzarExcepcion_siExcedePesoMaximo() {
        RutaDTO rutaPesada = new RutaDTO();
        rutaPesada.setFecha(LocalDate.now());
        rutaPesada.setVehiculo(vehiculoDTO);
        rutaPesada.setEnvios(List.of(envioPesado));

        assertThrows(IllegalArgumentException.class, () -> {
            rutaService.crearRuta(rutaPesada);
        });
    }

    @Test
    void crearRuta_debeLanzarExcepcion_siExcedeVolumenMaximo() {
        RutaDTO rutaVolumen = new RutaDTO();
        rutaVolumen.setFecha(LocalDate.now());
        rutaVolumen.setVehiculo(vehiculoDTO);
        rutaVolumen.setEnvios(List.of(envioVolumen));

        assertThrows(IllegalArgumentException.class, () -> {
            rutaService.crearRuta(rutaVolumen);
        });
    }

    @Test
    void crearRuta_debeLanzarExcepcion_siPaqueteRefrigeradoEnVehiculoNoRefrigerado() {
        RutaDTO rutaIncompatible = new RutaDTO();
        rutaIncompatible.setFecha(LocalDate.now());
        rutaIncompatible.setVehiculo(vehiculoNoRefrigerado); // Vehículo de falla
        rutaIncompatible.setEnvios(List.of(envioRefrigerado)); // Envío refrigerado

        assertThrows(IllegalArgumentException.class, () -> {
            rutaService.crearRuta(rutaIncompatible);
        });
    }

    @Test
    void crearRuta_debeLanzarExcepcion_siVehiculoRefrigeradoNoTieneRangosTemp() {
        RutaDTO rutaSinRango = new RutaDTO();
        rutaSinRango.setFecha(LocalDate.now());
        rutaSinRango.setVehiculo(vehiculoSinRango); // Vehículo de falla
        rutaSinRango.setEnvios(List.of(envioRefrigerado)); // Envío refrigerado

        assertThrows(IllegalStateException.class, () -> {
            rutaService.crearRuta(rutaSinRango);
        });
    }

    @Test
    void crearRuta_debeLanzarExcepcion_siTempPaqueteFueraDeRangoVehiculo() {
        RutaDTO rutaFria = new RutaDTO();
        rutaFria.setFecha(LocalDate.now());
        rutaFria.setVehiculo(vehiculoDTO); // Vehículo feliz [1.0 - 10.0]
        rutaFria.setEnvios(List.of(envioFueraDeRango)); // Envío de falla (a -5.0°C)

        assertThrows(IllegalStateException.class, () -> {
            rutaService.crearRuta(rutaFria);
        });
    }
}