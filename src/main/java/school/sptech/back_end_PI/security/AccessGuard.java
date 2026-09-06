package school.sptech.back_end_PI.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.repository.ContratoRepository;
import school.sptech.back_end_PI.repository.TurmaRepository;
import school.sptech.back_end_PI.repository.AulaRepository;
import school.sptech.back_end_PI.repository.AlunoRepository;

@Component
public class AccessGuard {

    private final ContratoRepository contratoRepository;
    private final TurmaRepository turmaRepository;
    private final AulaRepository aulaRepository;
    private final AlunoRepository alunoRepository;

    public AccessGuard(ContratoRepository contratoRepository,
                       TurmaRepository turmaRepository,
                       AulaRepository aulaRepository,
                       AlunoRepository alunoRepository) {
        this.contratoRepository = contratoRepository;
        this.turmaRepository = turmaRepository;
        this.aulaRepository = aulaRepository;
        this.alunoRepository = alunoRepository;
    }

    private Professor getCurrentProfessor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof Professor) {
            return (Professor) principal;
        }
        return null;
    }

    public boolean isCoordenador(Authentication authentication) {
        return authentication != null && authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_COORDENADOR"));
    }

    public Long currentProfessorId(Authentication authentication) {
        Professor professor = getProfessorFromAuthentication(authentication);
        return professor != null ? professor.getId() : null;
    }

    private Professor getProfessorFromAuthentication(Authentication authentication) {
        if (authentication == null) return null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof Professor) {
            return (Professor) principal;
        }
        return null;
    }

    public boolean canManageProfessor(Long id, Authentication authentication) {
        Professor professor = getProfessorFromAuthentication(authentication);
        if (professor == null) return false;

        Professor currentProfessor = getCurrentProfessor();
        if (currentProfessor == null) return false;

        if (isCoordenador(authentication)) {
            return true;
        }

        return professor.getId().equals(currentProfessor.getId());
    }

    public boolean canManageContrato(Long id, Authentication authentication) {
        if (isCoordenador(authentication)) {
            return true;
        }

        Professor professor = getProfessorFromAuthentication(authentication);
        if (professor == null) return false;

        return contratoRepository.existsByIdAndProfessorId(id, professor.getId());
    }

    public boolean canManageTurma(Long id, Authentication authentication) {
        if (isCoordenador(authentication)) {
            return true;
        }

        Professor professor = getProfessorFromAuthentication(authentication);
        if (professor == null) return false;

        return turmaRepository.existsByIdAndProfessorId(id, professor.getId());
    }

    public boolean canManageAula(Long id, Authentication authentication) {
        if (isCoordenador(authentication)) {
            return true;
        }

        Professor professor = getProfessorFromAuthentication(authentication);
        if (professor == null) return false;

        return aulaRepository.existsByIdAndContratoProfessorId(id, professor.getId());
    }

    public boolean canManageAluno(Long id, Authentication authentication) {
        if (isCoordenador(authentication)) {
            return true;
        }

        Professor professor = getProfessorFromAuthentication(authentication);
        if (professor == null) return false;

        return contratoRepository.existsByProfessorIdAndAlunoId(professor.getId(), id);
    }
}