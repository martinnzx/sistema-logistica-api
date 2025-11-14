package ar.edu.unju.fi.service;

import ar.edu.unju.fi.dto.views.RutaViewDTO;
import ar.edu.unju.fi.enums.EstadoEnvio;
import ar.edu.unju.fi.exceptions.ResourceNotFoundException;
import ar.edu.unju.fi.repository.EnvioRepository;
import ar.edu.unju.fi.repository.RutaRepository;
import ar.edu.unju.fi.repository.VehiculoRepository;
import ar.edu.unju.fi.dto.RutaDTO;
import ar.edu.unju.fi.mapper.RutaMapper;
import ar.edu.unju.fi.model.*;
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
    private final EnvioRepository envioRepository;
    private final RutaRepository rutaRepository;
    private final VehiculoRepository vehiculoRepository;

    public RutaService(EnvioRepository envioRepository, RutaRepository rutaRepository, VehiculoRepository vehiculoRepository) {
        this.envioRepository = envioRepository;
        this.rutaRepository = rutaRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    @Transactional
    public RutaDTO crearRuta(@Valid RutaViewDTO viewDTO) {
        log.info("Iniciando creación de ruta para vehículo: {}", viewDTO.getPatenteVehiculo());

        // 1. Ensambla la entidad Ruta buscando las relaciones en la BD
        Ruta ruta = ensamblarRuta(viewDTO);

        // 2. Ejecuta validaciones de negocio (Capacidad, Refrigeración, etc.)
        ejecutarValidacionesDeRuta(ruta.getVehiculo(), ruta.getEnvios());

        // 3. Guarda la nueva ruta (esto le asigna un ID)
        Ruta rutaGuardada = rutaRepository.save(ruta);


        log.info("Ruta creada exitosamente con ID: {}", rutaGuardada.getId());

        // 5. Devuelve el DTO de respuesta completo (usando un mapper)
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

    // --- MÉTODOS PRIVADOS DE ENSAMBLAJE Y VALIDACIÓN ---


    private Ruta ensamblarRuta(RutaViewDTO dto) {
        Ruta ruta = new Ruta();
        ruta.setFecha(dto.getFecha());

        // 1. Buscar y validar Vehículo
        Vehiculo vehiculo = buscarVehiculoValidado(dto.getPatenteVehiculo());
        ruta.setVehiculo(vehiculo);

        // 2. Buscar y validar Envíos
        List<Envio> envios = buscarEnviosValidados(dto.getCodigoEnvios());
        ruta.setEnvios(envios);

        return ruta;
    }

    private Vehiculo buscarVehiculoValidado(String patente) {
        return vehiculoRepository.findByPatenteIgnoreCase(patente)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo", "patente", patente));
    }

    private List<Envio> buscarEnviosValidados(List<String> codigos) {
        if (codigos == null || codigos.isEmpty()) {
            throw new IllegalArgumentException("La ruta debe contener al menos un código de envío.");
        }


        List<Envio> envios = envioRepository.findByCodigoUnicoIn(codigos);

        if (envios.size() != codigos.size()) {
            throw new ResourceNotFoundException("No se encontraron todos los envíos solicitados. Verifique los códigos.");
        }

        for (Envio envio : envios) {
            if (envio.getEstado() != EstadoEnvio.EN_ALMACEN) {
                throw new IllegalArgumentException("El envío " + envio.getCodigoUnico() +
                        " no puede ser asignado a una ruta. Estado actual: " + envio.getEstado());
            }
        }
        return envios;
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