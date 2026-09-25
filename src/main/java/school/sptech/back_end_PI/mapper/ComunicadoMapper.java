package school.sptech.back_end_PI.mapper;

import school.sptech.back_end_PI.dto.comunicado.ComunicadoRequest;
import school.sptech.back_end_PI.dto.comunicado.ComunicadoResponse;
import school.sptech.back_end_PI.entity.Comunicado;

import java.util.ArrayList;
import java.util.List;

public class ComunicadoMapper {

    public static Comunicado toEntity(ComunicadoRequest request) {
        if (request == null) return null;

        Comunicado comunicado = new Comunicado();
        comunicado.setTitulo(request.getTitulo().trim());
        comunicado.setTexto(request.getTexto().trim());
        return comunicado;
    }

    public static ComunicadoResponse toResponse(Comunicado entity) {
        if (entity == null) return null;

        ComunicadoResponse response = new ComunicadoResponse();
        response.setId(entity.getId());
        response.setTitulo(entity.getTitulo());
        response.setTexto(entity.getTexto());
        response.setDataCriacao(entity.getDataCriacao());
        if (entity.getAutor() != null) {
            response.setAutorNome(entity.getAutor().getNome());
        }
        return response;
    }

    public static List<ComunicadoResponse> toResponseList(List<Comunicado> comunicados) {
        if (comunicados == null) return new ArrayList<>();
        return comunicados.stream()
                .map(ComunicadoMapper::toResponse)
                .toList();
    }
}
