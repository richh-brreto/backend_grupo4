package school.sptech.back_end_PI.mapper;

import school.sptech.back_end_PI.dto.permissao.PermissaoResponse;
import school.sptech.back_end_PI.entity.Permissao;

import java.util.List;

public class PermissaoMapper {

    public static PermissaoResponse toResponse(Permissao permissao) {
        if (permissao == null) return null;
        return new PermissaoResponse(permissao.getId(), permissao.getNome());
    }

    public static List<PermissaoResponse> toResponseList(List<Permissao> permissoes) {
        if (permissoes == null) return List.of();
        return permissoes.stream().map(PermissaoMapper::toResponse).toList();
    }
}
