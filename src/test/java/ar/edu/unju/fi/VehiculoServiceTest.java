package ar.edu.unju.fi;

import ar.edu.unju.fi.Repository.VehiculoRepository;
import ar.edu.unju.fi.Service.VehiculoService;
import ar.edu.unju.fi.dto.VehiculoDTO;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
@Transactional
public class VehiculoServiceTest {

    @Autowired
    private VehiculoService vehiculoService;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @BeforeEach
    void setUp() {
        vehiculoRepository.deleteAll();
    }

    @Test
    void testCrearVehiculo() {
        VehiculoDTO vehiculoDTO = new VehiculoDTO("ABC123", 1000.0, 10.0, true);

        VehiculoDTO resultado = vehiculoService.crearVehiculo(vehiculoDTO);

        Assertions.assertNotNull(resultado);
        Assertions.assertEquals("ABC123", resultado.getPatente());
        Assertions.assertTrue(resultado.getRefrigerado());
    }

    @Test
    void testBuscarVehiculosRefrigerados() {
        VehiculoDTO v1 = new VehiculoDTO("A1", 500.0, 5.0, true);
        VehiculoDTO v2 = new VehiculoDTO("A2", 800.0, 8.0, false);

        vehiculoService.crearVehiculo(v1);
        vehiculoService.crearVehiculo(v2);

        List<VehiculoDTO> refrigerados = vehiculoService.buscarVehiculosRefrigerados(true);

        Assertions.assertEquals(1, refrigerados.size());
        Assertions.assertTrue(refrigerados.get(0).getRefrigerado());
    }

    @Test
    void testBuscarVehiculosPorPeso() {
        vehiculoService.crearVehiculo(new VehiculoDTO("A1", 400.0, 5.0, true));
        vehiculoService.crearVehiculo(new VehiculoDTO("A2", 1000.0, 10.0, false));

        List<VehiculoDTO> pesados = vehiculoService.buscarVehiculosPorPeso(900.0);

        Assertions.assertEquals(1, pesados.size());
        Assertions.assertEquals("A2", pesados.get(0).getPatente());
    }

    @Test
    void testBuscarVehiculosPorVolumen() {
        vehiculoService.crearVehiculo(new VehiculoDTO("V1", 600.0, 5.0, true));
        vehiculoService.crearVehiculo(new VehiculoDTO("V2", 600.0, 15.0, false));

        List<VehiculoDTO> voluminosos = vehiculoService.buscarVehiculosPorVolumen(10.0);

        Assertions.assertEquals(1, voluminosos.size());
        Assertions.assertEquals("V2", voluminosos.get(0).getPatente());
    }
}
