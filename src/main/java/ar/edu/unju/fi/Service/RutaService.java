package ar.edu.unju.fi.Service;

import ar.edu.unju.fi.Repository.EnvioRepository;
import ar.edu.unju.fi.Repository.RutaRepository;
import ar.edu.unju.fi.Repository.VehiculoRepository;
import ar.edu.unju.fi.dto.EnvioDTO;
import ar.edu.unju.fi.dto.RutaDTO;
import ar.edu.unju.fi.dto.VehiculoDTO;
import ar.edu.unju.fi.mapper.EnvioMapper;
import ar.edu.unju.fi.mapper.RutaMapper;
import ar.edu.unju.fi.mapper.VehiculoMapper;
import ar.edu.unju.fi.model.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RutaService {
    private EnvioRepository envioRepository;
    private RutaRepository rutaRepository;
    private VehiculoRepository vehiculoRepository;

    public RutaService(EnvioRepository envioRepository, RutaRepository rutaRepository, VehiculoRepository vehiculoRepository) {
        this.envioRepository = envioRepository;
        this.rutaRepository = rutaRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    @Transactional
    public RutaDTO crearRuta(RutaDTO dto) {
        if (dto == null) {
            log.error("Intento de crear ruta con DTO nulo.");
            return null;
        }

        log.info("Creando ruta con vehículo: {}", dto.getVehiculo() != null ? dto.getVehiculo().getPatente() : "sin vehículo");
        VehiculoDTO vehiculoDTO = dto.getVehiculo();

        if (vehiculoDTO == null || vehiculoDTO.getPatente() == null) {
            throw new IllegalArgumentException("Vehículo obligatorio");
        }
        Vehiculo vehiculo = vehiculoRepository.findByPatente(vehiculoDTO.getPatente())
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado"));

        List<Envio> enviosParaRuta = new ArrayList<>();
        if (dto.getEnvios() != null) {
            log.info("Asignando {} envíos a la ruta.", dto.getEnvios().size());
            for (EnvioDTO envioDTO : dto.getEnvios()) {
                if (envioDTO.getId() == null) {
                    throw new IllegalArgumentException("Solo se pueden agregar envíos existentes (con ID) a una ruta.");
                }
                Envio envio = envioRepository.findById(envioDTO.getId())
                        .orElseThrow(() -> new RuntimeException("Envio no encontrado con ID: " + envioDTO.getId()));

                enviosParaRuta.add(envio);
            }
        }

        validarCompatibilidad(vehiculo, enviosParaRuta);
        validarCapacidad(vehiculo, enviosParaRuta);
        validarTemperaturaPaquetes(vehiculo, enviosParaRuta);

        Ruta ruta = RutaMapper.toEntity(dto);
        ruta.setVehiculo(vehiculo);
        ruta.setEnvios(enviosParaRuta);

        Ruta guardada = rutaRepository.save(ruta);
        log.info("Ruta creada exitosamente con ID: {}", guardada.getId());

        RutaDTO rutaDTO = RutaMapper.toDto(guardada);
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

    private void validarCompatibilidad(Vehiculo vehiculo, List<Envio> envios) {
        log.debug("Validando compatibilidad de paquetes con vehículo {}", vehiculo.getPatente());
        for (Envio envio : envios) {
            for (Paquete paquete : envio.getPaquetes()) {
                if (paquete instanceof PaqueteRefrigerado && !vehiculo.getRefrigerado()) {
                    throw new IllegalArgumentException("El vehículo no tiene sistema de refrigeración y no puede transportar paquetes refrigerados");
                }
            }
        }
    }

    private void validarCapacidad(Vehiculo vehiculo, List<Envio> envios) {
        log.debug("Validando capacidad del vehículo {}", vehiculo.getPatente());
        double pesoTotal = 0.0;
        double volumenTotal = 0.0;

        for (Envio envio : envios) {
            for (Paquete paquete : envio.getPaquetes()) {
                pesoTotal += paquete.getPesoKg();
                volumenTotal += paquete.getVolumenDm3();
            }
        }
        log.debug("Peso total: {} kg, Volumen total: {} dm3", pesoTotal, volumenTotal);
        if (pesoTotal > vehiculo.getCapacidadMaxPesoKg()) {
            throw new IllegalArgumentException("El peso total (" + pesoTotal + " kg) supera la capacidad del vehículo");
        }

        if (volumenTotal > vehiculo.getCapacidadMaxVolDm3()) {
            throw new IllegalArgumentException("El volumen total (" + volumenTotal + " dm3) supera la capacidad del vehículo");
        }
    }
    private void validarTemperaturaPaquetes(Vehiculo vehiculo, List<Envio> envios) {
        log.debug("Validando temperatura de paquetes para vehículo {}", vehiculo.getPatente());

        boolean necesitaValidarTemp = envios.stream()
                .flatMap(envio -> envio.getPaquetes().stream())
                .anyMatch(paquete -> paquete instanceof PaqueteRefrigerado);

        if (!necesitaValidarTemp) {
            log.debug("No hay paquetes refrigerados, no se requiere validación de temperatura.");
            return;
        }

        log.debug("Se detectaron paquetes refrigerados. Validando vehículo.");
        if (!vehiculo.getRefrigerado()) {
            throw new IllegalArgumentException("El vehículo no es refrigerado pero intenta llevar paquetes refrigerados.");
        }

        Double rangoMinVeh = vehiculo.getRangoTemperaturaMin();
        Double rangoMaxVeh = vehiculo.getRangoTemperaturaMax();
        if (rangoMinVeh == null || rangoMaxVeh == null) {
            throw new IllegalArgumentException("El vehículo refrigerado (" + vehiculo.getPatente() + ") no tiene definido su rango de temperatura.");
        }

        for (Envio envio : envios) {
            for (Paquete paquete : envio.getPaquetes()) {
                if (paquete instanceof PaqueteRefrigerado pRef) {
                    Double tempObj = pRef.getTemperaturaObjetivo();

                    if (tempObj != null && (tempObj < rangoMinVeh || tempObj > rangoMaxVeh)) {
                        throw new IllegalArgumentException(
                                "El vehículo no puede mantener la temperatura requerida (" + tempObj + "°C). " +
                                        "Su rango es [" + rangoMinVeh + "°C - " + rangoMaxVeh + "°C]."
                        );
                    }
                }
            }
        }
    }
}
