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

    public RutaDTO crearRuta(RutaDTO dto) {
        if (dto == null) return null;
        VehiculoDTO vehiculoDTO = dto.getVehiculo();
        if (vehiculoDTO == null || vehiculoDTO.getPatente() == null) {
            throw new IllegalArgumentException("Vehículo obligatorio");
        }
        Vehiculo vehiculo = vehiculoRepository.findByPatente(vehiculoDTO.getPatente())
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado"));
        List<Envio> enviosGuardados = new ArrayList<>();
        if (dto.getEnvios() != null) {
            for (EnvioDTO envioDTO : dto.getEnvios()) {
                Envio envio = EnvioMapper.toEntity(envioDTO);
                Envio envioGuardado = envioRepository.save(envio);
                enviosGuardados.add(envioGuardado);
            }
        }
        validarCompatibilidad(vehiculo, enviosGuardados);
        validarCapacidad(vehiculo, enviosGuardados);
        validarTemperaturaPaquetes(vehiculo, enviosGuardados);

        Ruta ruta = RutaMapper.toEntity(dto);
        ruta.setVehiculo(vehiculo);
        ruta.setEnvios(enviosGuardados);

        Ruta guardada = rutaRepository.save(ruta);

        RutaDTO rutaDTO = RutaMapper.toDto(guardada);
        rutaDTO.setVehiculo(VehiculoMapper.toDTO(vehiculo));
        return rutaDTO;
    }


    public List<RutaDTO> obtenerEnviosPorRutaYFecha(Long rutaId, LocalDate fecha) {
        List<Ruta> rutas = rutaRepository.findByIdAndFecha(rutaId, fecha);
        List<RutaDTO> rutasDTO = new ArrayList<>();

        for (Ruta ruta : rutas) {
            RutaDTO dto = RutaMapper.toDto(ruta);
            rutasDTO.add(dto);
        }

        return rutasDTO;
    }

    private void validarCompatibilidad(Vehiculo vehiculo, List<Envio> envios) {
        for (Envio envio : envios) {
            for (Paquete paquete : envio.getPaquetes()) {
                if (paquete instanceof PaqueteRefrigerado && !vehiculo.getRefrigerado()) {
                    throw new IllegalArgumentException("El vehículo no tiene sistema de refrigeración y no puede transportar paquetes refrigerados");
                }
            }
        }
    }

    private void validarCapacidad(Vehiculo vehiculo, List<Envio> envios) {
        double pesoTotal = 0.0;
        double volumenTotal = 0.0;

        for (Envio envio : envios) {
            for (Paquete paquete : envio.getPaquetes()) {
                pesoTotal += paquete.getPesoKg();
                volumenTotal += paquete.getVolumenDm3();
            }
        }

        if (pesoTotal > vehiculo.getCapacidadMaxPesoKg()) {
            throw new IllegalArgumentException("El peso total (" + pesoTotal + " kg) supera la capacidad del vehículo");
        }

        if (volumenTotal > vehiculo.getCapacidadMaxVolDm3()) {
            throw new IllegalArgumentException("El volumen total (" + volumenTotal + " dm3) supera la capacidad del vehículo");
        }
    }
    private void validarTemperaturaPaquetes(Vehiculo vehiculo, List<Envio> envios) {
        Double rangoMinVeh = vehiculo.getRangoTemperaturaMin();
        Double rangoMaxVeh = vehiculo.getRangoTemperaturaMax();
        if (rangoMinVeh == null || rangoMaxVeh == null) {
            throw new IllegalArgumentException("El vehículo refrigerado no tiene definido su rango de temperatura.");
        }

        for (Envio envio : envios) {
            for (Paquete paquete : envio.getPaquetes()) {
                if (paquete instanceof PaqueteRefrigerado) {
                    PaqueteRefrigerado pRef = (PaqueteRefrigerado) paquete;

                    Double tempObj = pRef.getTemperaturaObjetivo();

                    if (tempObj < rangoMinVeh || tempObj > rangoMaxVeh) {
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
