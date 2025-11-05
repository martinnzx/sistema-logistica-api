package ar.edu.unju.fi;

import ar.edu.unju.fi.Enum.NivelFragilidad;
import ar.edu.unju.fi.Repository.EnvioRepository;
import ar.edu.unju.fi.Repository.RutaRepository;
import ar.edu.unju.fi.Repository.VehiculoRepository;
import ar.edu.unju.fi.Service.ClienteService;
import ar.edu.unju.fi.Service.EnvioService;
import ar.edu.unju.fi.Service.RutaService;
import ar.edu.unju.fi.Service.VehiculoService;
import ar.edu.unju.fi.dto.*;
import ar.edu.unju.fi.Enum.EstadoEnvio;

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
public class RutaServiceTest {
    @Autowired
    private EnvioService envioService;

    @Autowired
    private RutaService rutaService;

    @Autowired
    private VehiculoService vehiculoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private RutaRepository rutaRepository;

    private ClienteDTO pedro;
    private ClienteDTO hector;
    private ClienteDTO ale;

    private EnvioDTO envioGuardado1;
    private EnvioDTO envioGuardado2;

    private PaqueteDTO p1;
    private PaqueteDTO p2;

    private VehiculoDTO vehiculoDTO;

    @BeforeEach
    void setUp() {
        ClienteDTO pedroDTO = ClienteDTO.builder().nombreRazonSocial("Pedro G").documentoOCuit("444").telefono("123").email("p@p.com").direccionPrincipal("Dir 4").codigoPostal("4000").build();
        ClienteDTO hectorDTO = ClienteDTO.builder().nombreRazonSocial("Hector").documentoOCuit("555").telefono("123").email("h@h.com").direccionPrincipal("Dir 5").codigoPostal("5000").build();
        ClienteDTO aleDTO = ClienteDTO.builder().nombreRazonSocial("Ale").documentoOCuit("666").telefono("123").email("a@a.com").direccionPrincipal("Dir 6").codigoPostal("6000").build();

        pedro = clienteService.crearCliente(pedroDTO);
        hector = clienteService.crearCliente(hectorDTO);
        ale = clienteService.crearCliente(aleDTO);

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

        vehiculoDTO = new VehiculoDTO("ABC123", 1000.0, 500.0,true,1.0,10.0);
        vehiculoDTO = vehiculoService.crearVehiculo(vehiculoDTO);

        EnvioDTO dtoEnvio1 = EnvioDTO.builder()
                .remitente(pedro)
                .destinatario(hector)
                .direccionEntrega("Calle Falsa 123")
                .codigoPostal("4600")
                .paquetes(List.of(p1))
                .build();
        envioGuardado1 = envioService.crearEnvio(dtoEnvio1); // Guardado, ahora tiene ID

        EnvioDTO dtoEnvio2 = EnvioDTO.builder()
                .remitente(hector)
                .destinatario(ale)
                .direccionEntrega("Av Siempre Viva 742")
                .codigoPostal("4601")
                .paquetes(List.of(p2))
                .build();
        envioGuardado2 = envioService.crearEnvio(dtoEnvio2);
    }

    @Test
    void crearRuta() {
        RutaDTO rutaDTO = new RutaDTO();
        rutaDTO.setFecha(LocalDate.now());
        rutaDTO.setVehiculo(vehiculoDTO);

        rutaDTO.setEnvios(List.of(envioGuardado1, envioGuardado2));

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
        RutaDTO rutaDTO = new RutaDTO();
        rutaDTO.setFecha(LocalDate.now());
        rutaDTO.setVehiculo(vehiculoDTO);
        rutaDTO.setEnvios(List.of(envioGuardado1, envioGuardado2));

        RutaDTO rutaGuardada = rutaService.crearRuta(rutaDTO);
        assertNotNull(rutaGuardada.getId());

        List<RutaDTO> rutasEncontradas = rutaService.obtenerEnviosPorRutaYFecha(
                rutaGuardada.getId(),
                LocalDate.now()
        );

        assertNotNull(rutasEncontradas, "La lista de rutas no debe ser null");
        assertEquals(1, rutasEncontradas.size(), "Debe devolver 1 ruta");

        RutaDTO dto = rutasEncontradas.get(0);
        assertEquals(rutaGuardada.getId(), dto.getId());
        assertEquals(2, dto.getEnvios().size(), "La ruta debe tener 2 envíos");
        assertEquals("ABC123", dto.getVehiculo().getPatente(), "El vehículo debe coincidir");
        assertEquals(LocalDate.now(), dto.getFecha(), "La fecha debe coincidir");
    }
}