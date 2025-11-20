package ar.edu.unju.fi;

import ar.edu.unju.fi.service.VehiculoService;
import ar.edu.unju.fi.dto.VehiculoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
                .patente("ABC123-TEST")
                .capacidadMaxPesoKg(1000.0)
                .capacidadMaxVolDm3(10.0)
                .refrigerado(true)
                .rangoTemperaturaMin(0.0)
                .rangoTemperaturaMax(5.0)
                .build();

        VehiculoDTO resultado = vehiculoService.crearVehiculo(vehiculoDTO);

        assertNotNull(resultado);
        assertEquals("ABC123-TEST", resultado.getPatente());
        assertTrue(resultado.getRefrigerado());
    }

    @Test
    void testBuscarVehiculosRefrigerados() {
        List<VehiculoDTO> refrigerados = vehiculoService.buscarVehiculosRefrigerados(true);

        // CORRECCION: Verificamos que la lista contenga AL MENOS uno, y que esté el nuestro
        assertFalse(refrigerados.isEmpty());
        boolean existeNuestroVehiculo = refrigerados.stream()
                .anyMatch(v -> v.getPatente().equals("V-REF"));
        assertTrue(existeNuestroVehiculo, "Debería encontrar el vehículo refrigerado 'V-REF'");

        List<VehiculoDTO> noRefrigerados = vehiculoService.buscarVehiculosRefrigerados(false);
        assertFalse(noRefrigerados.isEmpty());
    }

    @Test
    void testBuscarVehiculosPorPeso() {
        // Buscamos vehículos con más de 900kg
        List<VehiculoDTO> pesados = vehiculoService.buscarVehiculosPorPeso(900.0);

        // CORRECCION: Buscamos si existe el nuestro en la lista, sin importar los del SQL
        boolean encontrado = pesados.stream()
                .anyMatch(v -> v.getPatente().equals("V-PES"));

        assertTrue(encontrado, "La lista debería contener el vehículo 'V-PES' que supera los 900kg");
    }

    @Test
    void testBuscarVehiculosPorVolumen() {
        // Buscamos vehículos con más de 100 dm3
        List<VehiculoDTO> voluminosos = vehiculoService.buscarVehiculosPorVolumen(100.0);

        // CORRECCION: Buscamos si existe el nuestro
        boolean encontrado = voluminosos.stream()
                .anyMatch(v -> v.getPatente().equals("V-VOL"));

        assertTrue(encontrado, "La lista debería contener el vehículo 'V-VOL' que supera los 100dm3");
    }
}
