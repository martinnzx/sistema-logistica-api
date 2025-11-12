package ar.edu.unju.fi;

import ar.edu.unju.fi.service.VehiculoService;
import ar.edu.unju.fi.dto.VehiculoDTO;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class VehiculoServiceTest {

    @Autowired
    private VehiculoService vehiculoService;

    @BeforeEach
    void setUp() {
        VehiculoDTO vRefrigerado = VehiculoDTO.builder()
                .patente("V-REF")
                .capacidadMaxPesoKg(500.0)
                .capacidadMaxVolDm3(50.0)
                .refrigerado(true)
                .rangoTemperaturaMin(1.0)
                .rangoTemperaturaMax(10.0)
                .build();

        VehiculoDTO vPesado = VehiculoDTO.builder()
                .patente("V-PES")
                .capacidadMaxPesoKg(1000.0) // > 900
                .capacidadMaxVolDm3(50.0)
                .refrigerado(false)
                .build();

        VehiculoDTO vVoluminoso = VehiculoDTO.builder()
                .patente("V-VOL")
                .capacidadMaxPesoKg(500.0)
                .capacidadMaxVolDm3(150.0) // > 100
                .refrigerado(false)
                .build();

        VehiculoDTO vNormal = VehiculoDTO.builder()
                .patente("V-NOR")
                .capacidadMaxPesoKg(500.0)
                .capacidadMaxVolDm3(50.0)
                .refrigerado(false)
                .build();

        vehiculoService.crearVehiculo(vRefrigerado);
        vehiculoService.crearVehiculo(vPesado);
        vehiculoService.crearVehiculo(vVoluminoso);
        vehiculoService.crearVehiculo(vNormal);
    }

    @Test
    void testCrearVehiculo() {
        VehiculoDTO vehiculoDTO = VehiculoDTO.builder()
                .patente("ABC123")
                .capacidadMaxPesoKg(1000.0)
                .capacidadMaxVolDm3(10.0)
                .refrigerado(true)
                .rangoTemperaturaMin(0.0)
                .rangoTemperaturaMax(5.0)
                .build();

        VehiculoDTO resultado = vehiculoService.crearVehiculo(vehiculoDTO);

        assertNotNull(resultado);
        assertEquals("ABC123", resultado.getPatente());
        assertTrue(resultado.getRefrigerado());
    }

    @Test
    void testBuscarVehiculosRefrigerados() {
        List<VehiculoDTO> refrigerados = vehiculoService.buscarVehiculosRefrigerados(true);

        assertEquals(1, refrigerados.size());

        assertEquals("V-REF", refrigerados.getFirst().getPatente());
        assertTrue(refrigerados.getFirst().getRefrigerado());

        List<VehiculoDTO> noRefrigerados = vehiculoService.buscarVehiculosRefrigerados(false);
        assertEquals(3, noRefrigerados.size());
    }

    @Test
    void testBuscarVehiculosPorPeso() {

        List<VehiculoDTO> pesados = vehiculoService.buscarVehiculosPorPeso(900.0);

        assertEquals(1, pesados.size(), "Solo 'V-PES' debe superar los 900kg");

        assertEquals("V-PES", pesados.getFirst().getPatente());
    }

    @Test
    void testBuscarVehiculosPorVolumen() {
        List<VehiculoDTO> voluminosos = vehiculoService.buscarVehiculosPorVolumen(100.0);

        assertEquals(1, voluminosos.size(), "Solo 'V-VOL' debe superar los 100dm3");

        assertEquals("V-VOL", voluminosos.getFirst().getPatente());
    }
}
