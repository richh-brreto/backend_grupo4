package school.sptech.back_end_PI.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sptech.back_end_PI.dto.comunicado.ComunicadoRequest;
import school.sptech.back_end_PI.entity.Comunicado;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.exception.EntityNotFound;
import school.sptech.back_end_PI.mapper.ComunicadoMapper;
import school.sptech.back_end_PI.repository.ComunicadoRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ComunicadoService {

    private final ComunicadoRepository comunicadoRepository;
    private final AuditService audit;

    public ComunicadoService(ComunicadoRepository comunicadoRepository, AuditService audit) {
        this.comunicadoRepository = comunicadoRepository;
        this.audit = audit;
    }

    @Transactional(readOnly = true)
    public List<Comunicado> listarTodos() {
        return comunicadoRepository.findAllByOrderByDataCriacaoDescIdDesc();
    }

    @Transactional
    public Comunicado salvar(ComunicadoRequest request, Professor autor) {
        Comunicado comunicado = ComunicadoMapper.toEntity(request);
        // Mesma precisão da coluna DATETIME, para a resposta bater com o que foi gravado
        comunicado.setDataCriacao(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        comunicado.setAutor(autor);

        Comunicado salvo = comunicadoRepository.save(comunicado);
        audit.log("comunicado.create", audit.currentActor(), "comunicado:" + salvo.getId(), "success");
        return salvo;
    }

    @Transactional
    public Comunicado atualizar(Long id, ComunicadoRequest request) {
        Comunicado comunicado = buscarPorId(id);
        comunicado.setTitulo(request.getTitulo().trim());
        comunicado.setTexto(request.getTexto().trim());

        Comunicado atualizado = comunicadoRepository.save(comunicado);
        audit.log("comunicado.update", audit.currentActor(), "comunicado:" + id, "success");
        return atualizado;
    }

    @Transactional
    public void deletar(Long id) {
        Comunicado comunicado = buscarPorId(id);
        comunicadoRepository.delete(comunicado);
        audit.log("comunicado.delete", audit.currentActor(), "comunicado:" + id, "success");
    }

    public Comunicado buscarPorId(Long id) {
        return comunicadoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Comunicado não encontrado com ID: " + id));
    }
}
