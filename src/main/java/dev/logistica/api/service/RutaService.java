package dev.logistica.api.service;

import dev.logistica.api.dto.views.RutaViewDTO;
import dev.logistica.api.repository.RutaRepository;
import dev.logistica.api.dto.RutaDTO;
import dev.logistica.api.mapper.RutaMapper;
import dev.logistica.api.model.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RutaService {
    private final RutaRepository rutaRepository;
    private final EnvioService envioService;
    private final VehiculoService vehiculoService;

    public RutaService(RutaRepository rutaRepository,
                        VehiculoService vehiculoService,EnvioService envioService) {
        this.rutaRepository = rutaRepository;
        this.vehiculoService = vehiculoService;
        this.envioService = envioService;
    }

    @Transactional
    public RutaDTO crearRuta(@Valid RutaViewDTO viewDTO) {
        log.info("Iniciando creación de ruta para vehículo: {}", viewDTO.getPatenteVehiculo());

        //  Ensambla la entidad Ruta buscando las relaciones en la BD
        Ruta ruta = ensamblarRuta(viewDTO);

        //  Ejecuta validaciones de negocio (Capacidad, Refrigeración, etc.)
        ejecutarValidacionesDeRuta(ruta.getVehiculo(), ruta.getEnvios());

        //  Guarda la nueva ruta (esto le asigna un ID)
        Ruta rutaGuardada = rutaRepository.save(ruta);

        log.info("Ruta creada exitosamente con ID: {}", rutaGuardada.getId());

        //  Devuelve el DTO de respuesta completo (usando un mapper)
        return RutaMapper.toDto(rutaGuardada);
    }

    public List<RutaDTO> obtenerEnviosPorRutaYFecha(Long rutaId, LocalDate fecha) {
        log.info("Consultando rutas para rutaId={} en fecha={}", rutaId, fecha);
        Ruta ruta = rutaRepository.findById(rutaId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro el ruta con ID: " + rutaId));

        if (!ruta.getFecha().equals(fecha)) {
            log.warn("Ruta {} encontrada, pero no coincide con la fecha {}", rutaId, fecha);
            return new ArrayList<>();
        }

        RutaDTO dto = RutaMapper.toDto(ruta);
        return List.of(dto);
    }
    @Transactional
    public List<RutaDTO> listarTodas() {
        log.info("Listando todas las rutas del sistema");
        List<Ruta> rutas = rutaRepository.findAll();

        // Convertimos la lista de Entidades a lista de DTOs
        return rutas.stream()
                .map(RutaMapper::toDto)
                .toList();
    }

    @Transactional
    public RutaDTO obtenerPorId(Long id) {
        log.info("Buscando ruta con ID: {}", id);

        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la ruta con ID: " + id));

        return RutaMapper.toDto(ruta);
    }

    // --- MÉTODOS PRIVADOS DE ENSAMBLAJE Y VALIDACIÓN ---

    private Ruta ensamblarRuta(RutaViewDTO dto) {
        Ruta ruta = new Ruta();
        ruta.setFecha(dto.getFecha());

        // 1. Buscar y validar Vehículo
        Vehiculo vehiculo = vehiculoService.buscarVehiculoPatente(dto.getPatenteVehiculo());
        ruta.setVehiculo(vehiculo);

        // 2. Buscar y validar Envíos
        List<Envio> envios = envioService.buscarEnviosValidados(dto.getCodigoEnvios());
        ruta.setEnvios(envios);

        return ruta;
    }

    private void ejecutarValidacionesDeRuta(Vehiculo vehiculo, List<Envio> envios) {
        if (envios.isEmpty()) {
            log.debug("No hay envíos, omitiendo validaciones de compatibilidad/capacidad.");
            return;
        }
        validarCargaCompleta(vehiculo, envios);
    }

    private void validarCargaCompleta(Vehiculo vehiculo, List<Envio> envios) {
        log.debug("Validando carga completa (capacidad, compatibilidad, temp) para vehículo {}", vehiculo.getPatente());

        double pesoTotal = 0.0;
        double volumenTotal = 0.0;

        boolean esVehiculoRefrigerado = vehiculo.getRefrigerado();
        Double rangoMinVeh = vehiculo.getRangoTemperaturaMin();
        Double rangoMaxVeh = vehiculo.getRangoTemperaturaMax();
        boolean vehiculoTieneRangos = rangoMinVeh != null && rangoMaxVeh != null;
        boolean algunPaqueteRefrigerado = false;

        for (Envio envio : envios) {
            // Iteramos los paquetes de CADA envío
            for (Paquete paquete : envio.getPaquetes()) {

                pesoTotal += paquete.getPesoKg();
                volumenTotal += paquete.getVolumenDm3();

                // Chequeo de refrigeración
                if (paquete instanceof PaqueteRefrigerado pRef) {
                    algunPaqueteRefrigerado = true;

                    if (!esVehiculoRefrigerado) {
                        throw new IllegalArgumentException("El vehículo no tiene sistema de refrigeración y no puede transportar el paquete " + pRef.getCodigo());
                    }
                    // Validamos la compatibilidad de rangos
                    validarPaqueteRefrigerado(pRef, rangoMinVeh, rangoMaxVeh);
                }
            }
        }
        log.debug("Carga total calculada: Peso={} kg, Volumen={} dm3", pesoTotal, volumenTotal);

        // Validación de capacidad
        if (pesoTotal > vehiculo.getCapacidadMaxPesoKg()) {
            throw new IllegalArgumentException("El peso total (" + pesoTotal + " kg) supera la capacidad del vehículo");
        }
        if (volumenTotal > vehiculo.getCapacidadMaxVolDm3()) {
            throw new IllegalArgumentException("El volumen total (" + volumenTotal + " dm3) supera la capacidad del vehículo");
        }

        // Validación de rangos
        if (algunPaqueteRefrigerado && !vehiculoTieneRangos) {
            throw new IllegalArgumentException("El vehículo refrigerado (" + vehiculo.getPatente() + ") no tiene definido su rango de temperatura.");
        }

        log.debug("Validación de carga completa superada.");
    }

    private void validarPaqueteRefrigerado(PaqueteRefrigerado pRef, Double rangoMinVeh, Double rangoMaxVeh) {
        Double tempObj = pRef.getTemperaturaObjetivo();

        if (tempObj != null && (rangoMinVeh == null || rangoMaxVeh == null)) {
            throw new IllegalStateException("El vehículo no tiene rangos definidos para validar temperatura.");
        }

        if (tempObj != null && (tempObj < rangoMinVeh || tempObj > rangoMaxVeh)) {
            throw new IllegalArgumentException(
                    "El vehículo no puede mantener la temperatura requerida (" + tempObj + "°C) para el paquete " + pRef.getCodigo() + ". " +
                            "Su rango es [" + rangoMinVeh + "C - " + rangoMaxVeh + "C]."
            );
        }
    }
}
