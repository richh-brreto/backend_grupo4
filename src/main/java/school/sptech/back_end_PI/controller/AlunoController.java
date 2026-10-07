package school.sptech.back_end_PI.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import school.sptech.back_end_PI.dto.PageResponse;
import school.sptech.back_end_PI.dto.aluno.AlunoRequest;
import school.sptech.back_end_PI.dto.aluno.AlunoResponse;
import school.sptech.back_end_PI.entity.Aluno;
import school.sptech.back_end_PI.mapper.AlunoMapper;
import school.sptech.back_end_PI.services.AlunoService;
import school.sptech.back_end_PI.security.AccessGuard;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/alunos")
public class AlunoController {

    private final AlunoService service;
    private final AccessGuard accessGuard;

    public AlunoController(AlunoService service, AccessGuard accessGuard) {
        this.service = service;
        this.accessGuard = accessGuard;
    }
    @PostMapping
    @PreAuthorize("hasAuthority('PERM_TELA_ALUNOS')")
    public ResponseEntity<AlunoResponse> create(
            @Valid @RequestBody AlunoRequest request) {

        Aluno criado = service.create(request);

        return ResponseEntity.status(201)
                .body(AlunoMapper.toResponse(criado));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PageResponse<AlunoResponse>> getAll(
            Authentication authentication,
            @RequestParam(required = false) String nome,
            @RequestParam(defaultValue = "true") boolean ativo,
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        pageable = ordenacaoPermitida(pageable);

        Page<Aluno> alunos;
        if (accessGuard.podeVerAlunos(authentication)) {
            alunos = service.getAll(nome, ativo, pageable);
        } else {
            // Professor só enxerga alunos ativos com contrato com ele (inativos perdem os contratos)
            Long professorId = accessGuard.currentProfessorId(authentication);
            alunos = professorId == null || !ativo
                    ? Page.empty(pageable)
                    : service.getByProfessorId(professorId, nome, pageable);
        }

        return ResponseEntity.ok(PageResponse.of(alunos, AlunoMapper::toResponse));
    }

    // A busca de inativos é SQL nativo, então só aceita campos cujo nome bate com a coluna
    private static final Set<String> CAMPOS_ORDENAVEIS = Set.of("nome", "email", "nivel");

    private Pageable ordenacaoPermitida(Pageable pageable) {
        Sort sort = Sort.by(pageable.getSort().stream()
                .filter(order -> CAMPOS_ORDENAVEIS.contains(order.getProperty()))
                .toList());
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                sort.isSorted() ? sort : Sort.by("nome"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@accessGuard.canManageAluno(#id, authentication)")
    public ResponseEntity<AlunoResponse> getById(@PathVariable Long id) {
        Aluno aluno = service.getById(id);
        return ResponseEntity.ok(AlunoMapper.toResponse(aluno));
    }

    @GetMapping("/disponiveis/{id}")
    @PreAuthorize("@accessGuard.canManageAluno(#id, authentication)")
    public ResponseEntity<AlunoResponse> getAlunoComHorariosDisponiveis(@PathVariable Long id) {
        Aluno aluno = service.buscarPorIdComHorariosDisponiveis(id);
        return ResponseEntity.ok(AlunoMapper.toResponse(aluno));
    }


    @GetMapping("/turma/{id}")
    @PreAuthorize("@accessGuard.canManageTurma(#id, authentication)")
    public ResponseEntity<List<AlunoResponse>> getByTurmaId(@PathVariable Long id){
        List<Aluno> alunos = service.getByTurmaId(id);
        return ResponseEntity.ok(AlunoMapper.toResponseList(alunos));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_TELA_ALUNOS')")
    @Operation(summary = "Inativar um aluno (Soft Delete)", description = "Altera o status do aluno para inativo")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_TELA_ALUNOS')")
    public ResponseEntity<AlunoResponse> update(@PathVariable Long id, @Valid @RequestBody AlunoRequest request) {

        Aluno atualizado = service.update(id, request);
        return ResponseEntity.ok(AlunoMapper.toResponse(atualizado));
    }

    @PatchMapping("/{id}/reativar")
    @PreAuthorize("hasAuthority('PERM_TELA_ALUNOS')")
    @Operation(summary = "Reativar um aluno inativo", description = "Restaura o acesso e o status do aluno para ativo")
    public ResponseEntity<AlunoResponse> reativar(@PathVariable Long id) {
        Aluno alunoReativado = service.reativar(id);
        return ResponseEntity.ok(AlunoMapper.toResponse(alunoReativado));
    }
}