package ar.edu.unju.fi;

import ar.edu.unju.fi.Repository.EnvioRepository;
import ar.edu.unju.fi.Repository.RutaRepository;
import ar.edu.unju.fi.Repository.VehiculoRepository;
import ar.edu.unju.fi.Service.RutaService;
import ar.edu.unju.fi.Service.VehiculoService;
import ar.edu.unju.fi.dto.EnvioDTO;
import ar.edu.unju.fi.dto.RutaDTO;
import ar.edu.unju.fi.dto.VehiculoDTO;
import ar.edu.unju.fi.model.EstadoEnvio;
import ar.edu.unju.fi.model.Vehiculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
public class RutaServiceTest {
    @Autowired
    private RutaService rutaService;

    @Autowired
    private VehiculoService vehiculoService;

    @Autowired
    private RutaRepository rutaRepository;

    @Autowired
    private EnvioRepository envioRepository;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    private VehiculoDTO vehiculoDTO;

    @BeforeEach
    void setUp() {
        // 1️⃣ Crear y guardar vehículo usando VehiculoService
        vehiculoDTO = new VehiculoDTO("ABC123", 1000.0, 500.0, true);
        vehiculoDTO = vehiculoService.crearVehiculo(vehiculoDTO); // devuelve DTO con ID asignado
    }

    @Test
    void crearRuta_guardaCorrectamente() {
        // 2️⃣ Crear DTO de ruta y asignar vehículo
        RutaDTO rutaDTO = new RutaDTO();
        rutaDTO.setFecha(LocalDate.now());
        rutaDTO.setVehiculo(vehiculoDTO);

        // 3️⃣ Crear envíos
        EnvioDTO envio1 = new EnvioDTO();
        envio1.setRemitente("Juan");
        envio1.setDestinatario("Pedro");
        envio1.setEstado(EstadoEnvio.EN_ALMACEN);
        envio1.setDireccionEntrega("Calle Falsa 123");
        envio1.setPaquetes(List.of());

        EnvioDTO envio2 = new EnvioDTO();
        envio2.setRemitente("Ana");
        envio2.setDestinatario("Maria");
        envio2.setEstado(EstadoEnvio.EN_ALMACEN);
        envio2.setDireccionEntrega("Av Siempre Viva 742");
        envio2.setPaquetes(List.of());

        rutaDTO.setEnvios(List.of(envio1, envio2));

        // 4️⃣ Llamar al service
        RutaDTO guardada = rutaService.crearRuta(rutaDTO);

        // 5️⃣ Verificaciones
        assertNotNull(guardada, "La ruta guardada no debe ser nula");
        assertNotNull(guardada.getVehiculo(), "El vehículo del DTO no debe ser null");
        assertEquals("ABC123", guardada.getVehiculo().getPatente(), "El vehículo debe coincidir");
        assertEquals(LocalDate.now(), guardada.getFecha(), "La fecha debe coincidir");
        assertEquals(2, guardada.getEnvios().size(), "Debe tener 2 envíos");

        // 6️⃣ Verificar en BD
        assertEquals(1, rutaRepository.count(), "Debe haberse guardado 1 ruta");
        assertEquals(2, envioRepository.count(), "Deben haberse guardado 2 envíos");
        assertEquals(1, vehiculoRepository.count(), "Debe existir 1 vehículo en BD");
    }

    @Test
    void obtenerEnviosPorRutaYFecha_debeRetornarRutasCorrectamente() {
        // Crear DTO de ruta
        RutaDTO rutaDTO = new RutaDTO();
        rutaDTO.setFecha(LocalDate.now());
        rutaDTO.setVehiculo(vehiculoDTO);

        // Crear envíos
        EnvioDTO envio1 = new EnvioDTO();
        envio1.setRemitente("Juan");
        envio1.setDestinatario("Pedro");
        envio1.setEstado(EstadoEnvio.EN_ALMACEN);
        envio1.setDireccionEntrega("Calle Falsa 123");
        envio1.setPaquetes(List.of());

        EnvioDTO envio2 = new EnvioDTO();
        envio2.setRemitente("Ana");
        envio2.setDestinatario("Maria");
        envio2.setEstado(EstadoEnvio.EN_ALMACEN);
        envio2.setDireccionEntrega("Av Siempre Viva 742");
        envio2.setPaquetes(List.of());

        rutaDTO.setEnvios(List.of(envio1, envio2));

        RutaDTO rutaGuardada = rutaService.crearRuta(rutaDTO);

        List<RutaDTO> rutas = rutaService.obtenerEnviosPorRutaYFecha(
                rutaRepository.findAll().get(0).getId(),
                LocalDate.now()
        );

        assertNotNull(rutas, "La lista de rutas no debe ser null");
        assertEquals(1, rutas.size(), "Debe devolver 1 ruta");
        RutaDTO dto = rutas.get(0);
        assertEquals(2, dto.getEnvios().size(), "La ruta debe tener 2 envíos");
        assertEquals("ABC123", dto.getVehiculo().getPatente(), "El vehículo debe coincidir");
        assertEquals(LocalDate.now(), dto.getFecha(), "La fecha debe coincidir");
    }
}
