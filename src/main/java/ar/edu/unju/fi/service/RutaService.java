package ar.edu.unju.fi.service;

import ar.edu.unju.fi.repository.EnvioRepository;
import ar.edu.unju.fi.repository.RutaRepository;
import ar.edu.unju.fi.repository.VehiculoRepository;
import ar.edu.unju.fi.dto.EnvioDTO;
import ar.edu.unju.fi.dto.RutaDTO;
import ar.edu.unju.fi.dto.VehiculoDTO;
import ar.edu.unju.fi.mapper.RutaMapper;
import ar.edu.unju.fi.mapper.VehiculoMapper;
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
    public RutaDTO crearRuta(@Valid RutaDTO dto) {
        if (dto == null) {
            log.error("Intento de crear ruta con DTO nulo.");
            return null;
        }
        Vehiculo vehiculo = obtenerVehiculoValidado(dto.getVehiculo());

        List<Envio> envios = obtenerEnviosValidados(dto.getEnvios());

        ejecutarValidacionesDeRuta(vehiculo, envios);

        Ruta rutaGuardada = ensamblarYGuardarRuta(dto, vehiculo, envios);

        RutaDTO rutaDTO = RutaMapper.toDto(rutaGuardada);
        rutaDTO.setVehiculo(VehiculoMapper.toDTO(vehiculo));
        return rutaDTO;
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

    private Vehiculo obtenerVehiculoValidado(VehiculoDTO vehiculoDTO) {
        log.info("Buscando vehículo: {}", vehiculoDTO != null ? vehiculoDTO.getPatente() : "DTO nulo");
        if (vehiculoDTO == null || vehiculoDTO.getPatente() == null) {
            throw new IllegalArgumentException("Vehículo obligatorio");
        }
        return vehiculoRepository.findByPatente(vehiculoDTO.getPatente())
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado: " + vehiculoDTO.getPatente()));
    }

    private List<Envio> obtenerEnviosValidados(List<EnvioDTO> enviosDTO) {
        if (enviosDTO == null || enviosDTO.isEmpty()) {
            log.info("No se especificaron envíos para la ruta.");
            return new ArrayList<>();
        }

        log.info("Asignando {} envíos a la ruta.", enviosDTO.size());

        return enviosDTO.stream()
                .map(envioDTO -> {
                    if (envioDTO.getId() == null) {
                        throw new IllegalArgumentException("Solo se pueden agregar envíos existentes (con ID) a una ruta.");
                    }
                    return envioRepository.findById(envioDTO.getId())
                            .orElseThrow(() -> new RuntimeException("Envio no encontrado con ID: " + envioDTO.getId()));
                })
                .toList();
    }

    private void ejecutarValidacionesDeRuta(Vehiculo vehiculo, List<Envio> envios) {
        if (envios.isEmpty()) {
            log.debug("No hay envíos, omitiendo validaciones de compatibilidad/capacidad.");
            return;
        }
        validarCargaCompleta(vehiculo, envios);
    }

    private Ruta ensamblarYGuardarRuta(RutaDTO dto, Vehiculo vehiculo, List<Envio> envios) {
        Ruta ruta = RutaMapper.toEntity(dto);
        ruta.setVehiculo(vehiculo);
        ruta.setEnvios(envios);

        Ruta guardada = rutaRepository.save(ruta);
        log.info("Ruta creada exitosamente con ID: {}", guardada.getId());
        return guardada;
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
            for (Paquete paquete : envio.getPaquetes()) {

                pesoTotal += paquete.getPesoKg();
                volumenTotal += paquete.getVolumenDm3();

                if (paquete instanceof PaqueteRefrigerado pRef) {
                    algunPaqueteRefrigerado = true;

                    if (!esVehiculoRefrigerado) {
                        throw new IllegalArgumentException("El vehículo no tiene sistema de refrigeración y no puede transportar paquetes refrigerados");
                    }

                    validarPaqueteRefrigerado(pRef, rangoMinVeh, rangoMaxVeh);
                }
            }
        }
        log.debug("Carga total calculada: Peso={} kg, Volumen={} dm3", pesoTotal, volumenTotal);
        if (pesoTotal > vehiculo.getCapacidadMaxPesoKg()) {
            throw new IllegalArgumentException("El peso total (" + pesoTotal + " kg) supera la capacidad del vehículo");
        }
        if (volumenTotal > vehiculo.getCapacidadMaxVolDm3()) {
            throw new IllegalArgumentException("El volumen total (" + volumenTotal + " dm3) supera la capacidad del vehículo");
        }

        if (algunPaqueteRefrigerado && !vehiculoTieneRangos) {
            throw new IllegalArgumentException("El vehículo refrigerado (" + vehiculo.getPatente() + ") no tiene definido su rango de temperatura.");
        }

        log.debug("Validación de carga completa superada.");
    }
    private void validarPaqueteRefrigerado(Paquete paquete, Double rangoMinVeh, Double rangoMaxVeh) {
        if (paquete instanceof PaqueteRefrigerado pRef) {
            Double tempObj = pRef.getTemperaturaObjetivo();

            if (tempObj != null && (rangoMinVeh == null || rangoMaxVeh == null)) {
                throw new IllegalStateException("El vehículo no tiene rangos definidos para validar temperatura.");
            }

            if (tempObj != null && (tempObj < rangoMinVeh || tempObj > rangoMaxVeh)) {
                throw new IllegalArgumentException(
                        "El vehículo no puede mantener la temperatura requerida (" + tempObj + "°C). " +
                                "Su rango es [" + rangoMinVeh + "C - " + rangoMaxVeh + "C]."
                );
            }
        }
    }
}
