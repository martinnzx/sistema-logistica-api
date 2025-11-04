package ar.edu.unju.fi;


import ar.edu.unju.fi.Service.ClienteService;
import ar.edu.unju.fi.Service.EnvioService;
import ar.edu.unju.fi.dto.ClienteDTO;
import ar.edu.unju.fi.dto.EnvioDTO;
import ar.edu.unju.fi.dto.PaqueteDTO;
import ar.edu.unju.fi.Enum.EstadoEnvio;
import ar.edu.unju.fi.Enum.NivelFragilidad;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
@Transactional
public class EnvioServiceTest {

    @Autowired
    private EnvioService envioService;

    @Autowired
    private ClienteService clienteService; // <- Necesario para crear clientes

    private ClienteDTO clienteJuan;
    private ClienteDTO clienteMaria;
    private ClienteDTO clienteCarlos;
    private ClienteDTO clientePedro;
    private ClienteDTO clienteHector;
    private ClienteDTO clienteAle;

    private PaqueteDTO p1;
    private PaqueteDTO p2;

    @BeforeEach
    public void setUp() {
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

        ClienteDTO juanDTO = ClienteDTO.builder().nombreRazonSocial("Juan Perez").documentoOCuit("111").telefono("123").email("j@j.com").direccionPrincipal("Dir 1").codigoPostal("1000").build();
        ClienteDTO mariaDTO = ClienteDTO.builder().nombreRazonSocial("Maria Gomez").documentoOCuit("222").telefono("123").email("m@m.com").direccionPrincipal("Dir 2").codigoPostal("2000").build();
        ClienteDTO carlosDTO = ClienteDTO.builder().nombreRazonSocial("Carlos Luis").documentoOCuit("333").telefono("123").email("c@c.com").direccionPrincipal("Dir 3").codigoPostal("3000").build();
        ClienteDTO pedroDTO = ClienteDTO.builder().nombreRazonSocial("Pedro G").documentoOCuit("444").telefono("123").email("p@p.com").direccionPrincipal("Dir 4").codigoPostal("4000").build();
        ClienteDTO hectorDTO = ClienteDTO.builder().nombreRazonSocial("Hector").documentoOCuit("555").telefono("123").email("h@h.com").direccionPrincipal("Dir 5").codigoPostal("5000").build();
        ClienteDTO aleDTO = ClienteDTO.builder().nombreRazonSocial("Ale").documentoOCuit("666").telefono("123").email("a@a.com").direccionPrincipal("Dir 6").codigoPostal("6000").build();

        clienteJuan = clienteService.crearCliente(juanDTO);
        clienteMaria = clienteService.crearCliente(mariaDTO);
        clienteCarlos = clienteService.crearCliente(carlosDTO);
        clientePedro = clienteService.crearCliente(pedroDTO);
        clienteHector = clienteService.crearCliente(hectorDTO);
        clienteAle = clienteService.crearCliente(aleDTO);
    }

    @Test
    void crearEnvio() {
        List<PaqueteDTO> paquetesDTO = new ArrayList<>();
        paquetesDTO.add(p1);

        EnvioDTO dto = new EnvioDTO();
        dto.setRemitente(clienteJuan);
        dto.setDestinatario(clienteMaria);
        dto.setDireccionEntrega("Calle Falsa 123");
        dto.setCodigoPostal("4600");

        dto.setPaquetes(paquetesDTO);

        EnvioDTO guardado = envioService.crearEnvio(dto);

        assertNotNull(guardado.getId());
        assertNotNull(guardado.getCodigoUnico());
        assertEquals(EstadoEnvio.GENERADO, guardado.getEstado());
        assertEquals(clienteJuan.getId(), guardado.getRemitente().getId());
        assertEquals("Juan Perez", guardado.getRemitente().getNombreRazonSocial());
        assertEquals(clienteMaria.getId(), guardado.getDestinatario().getId());
        assertEquals("Maria Gomez", guardado.getDestinatario().getNombreRazonSocial());
    }

    @Test
    void listarPorRemitente() {

        EnvioDTO dto1 = EnvioDTO.builder()
                .remitente(clienteJuan)
                .destinatario(clientePedro)
                .direccionEntrega("Calle 1")
                .codigoPostal("1001")
                .paquetes(List.of(p1))
                .build();
        envioService.crearEnvio(dto1);

        EnvioDTO dto2 = EnvioDTO.builder()
                .remitente(clienteCarlos)
                .destinatario(clienteJuan)
                .direccionEntrega("Calle 2")
                .codigoPostal("1002")
                .paquetes(List.of(p2))
                .build();
        envioService.crearEnvio(dto2);

        List<EnvioDTO> resultado = envioService.listarPorRemitente("111");
        assertEquals(1, resultado.size(), "Solo debería encontrar 1 envío con remitente '111'.");
        assertEquals(clienteJuan.getId(), resultado.get(0).getRemitente().getId(), "El remitente del envío encontrado debe ser 'Juan'.");
    }

    @Test
    void listarPorDestinatario() {
        EnvioDTO dto1 = EnvioDTO.builder()
                .remitente(clienteJuan)
                .destinatario(clienteAle)
                .direccionEntrega("Calle 1")
                .codigoPostal("1001")
                .paquetes(List.of(p1))
                .build();
        envioService.crearEnvio(dto1);


        EnvioDTO dto2 = EnvioDTO.builder()
                .remitente(clienteCarlos)
                .destinatario(clienteHector)
                .direccionEntrega("Calle 2")
                .codigoPostal("1002")
                .paquetes(List.of(p2))
                .build();
        envioService.crearEnvio(dto2);

        List<EnvioDTO> resultado = envioService.listarPorDestinatario("555"); // Buscar por doc de Hector

        assertEquals(1, resultado.size(), "Solo debería encontrar 1 envío con destinatario '555'.");
        assertEquals(clienteHector.getId(), resultado.get(0).getDestinatario().getId(), "El destinatario del envío encontrado debe ser 'Hector'.");
    }
}
