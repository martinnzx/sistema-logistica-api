package dev.logistica.api;

import dev.logistica.api.dto.views.EnvioViewDTO;
import dev.logistica.api.dto.views.RutaViewDTO;
import dev.logistica.api.enums.NivelFragilidad;
import dev.logistica.api.service.*;
import dev.logistica.api.dto.*;
import dev.logistica.api.service.EnvioService;
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
    @Autowired private EnvioService envioService;
    @Autowired private RutaService rutaService;
    @Autowired private VehiculoService vehiculoService;
    @Autowired private ClienteService clienteService;
    @Autowired private PaqueteService paqueteService;

    private RutaViewDTO rutaViewDto;

    @BeforeEach
    void setUp() {
        ClienteDTO cliente = ClienteDTO.builder()
                .nombreRazonSocial("Cliente Test Unico")
                .documentoOCuit("99-99999999-9")
                .telefono("555555")
                .email("test_unico@mail.com")
                .direccionPrincipal("Calle Test")
                .codigoPostal("9999")
                .build();
        cliente = clienteService.crearCliente(cliente);

        // 2. Crear un Vehículo de prueba
        VehiculoDTO vehiculo = new VehiculoDTO("TEST-999", 5000.0, 5000.0, false, null, null);
        vehiculo = vehiculoService.crearVehiculo(vehiculo);

        // 3. Crear un Paquete simple
        PaqueteDTO paquete = PaqueteDTO.builder()
                .codigo("PKG-TEST-999")
                .pesoKg(10.0)
                .volumenDm3(10.0)
                .tipo("Fragil")
                .nivelFragilidad(NivelFragilidad.BAJA)
                .seguroAdicional(false)
                .build();
        paqueteService.crearPaquete(paquete);

        // 4. Crear un Envío (Usamos el mismo cliente como remitente y destinatario para simplificar)
        EnvioViewDTO envioView = EnvioViewDTO.builder()
                .cuilRemitente(cliente.getDocumentoOCuit())
                .cuilDestinatario(cliente.getDocumentoOCuit())
                .direccionEntrega("Direccion Test")
                .codigoPostal("9999")
                .paquetes(List.of(paquete.getCodigo()))
                .build();

        EnvioDTO envio = envioService.crearEnvio(envioView);
        // Importante: Avanzamos el estado a "EN_ALMACEN" (o equivalente) para que se pueda poner en ruta
        // Usamos un string genérico en la observación
        envioService.avanzarEstado(envio.getCodigoUnico(), "Preparando para test");

        // 5. Preparar el objeto Ruta para el test
        rutaViewDto = RutaViewDTO.builder()
                .fecha(LocalDate.now()) // Fecha de hoy
                .patenteVehiculo(vehiculo.getPatente())
                .codigoEnvios(List.of(envio.getCodigoUnico()))
                .build();
    }

    @Test
    void crearRuta_debeFuncionarCorrectamente() {
        RutaDTO resultado = rutaService.crearRuta(rutaViewDto);

        // Verificaciones básicas
        assertNotNull(resultado, "La ruta creada no debería ser nula");
        assertNotNull(resultado.getId(), "La ruta debería tener un ID asignado");
        assertEquals(1, resultado.getEnvios().size(), "La ruta debería tener 1 envío asociado");
        assertEquals("TEST-999", resultado.getVehiculo().getPatente(), "La patente debe coincidir");

        System.out.println(">>> ¡TEST DE CREAR RUTA PASÓ EXITOSAMENTE! <<<");
    }
}
