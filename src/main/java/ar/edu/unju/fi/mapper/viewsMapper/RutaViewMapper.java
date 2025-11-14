package ar.edu.unju.fi.mapper.viewsMapper;


import ar.edu.unju.fi.dto.views.RutaViewDTO;
import ar.edu.unju.fi.model.Ruta;

public class RutaViewMapper {
    public static Ruta toEntity(RutaViewDTO dto) {
        Ruta ruta = new Ruta();
        ruta.setFecha(dto.getFecha());
        return ruta;
    }
}
