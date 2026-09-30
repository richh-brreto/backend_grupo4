package school.sptech.back_end_PI.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import school.sptech.back_end_PI.entity.Permissao;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.repository.ContratoRepository;
import school.sptech.back_end_PI.repository.TurmaRepository;
import school.sptech.back_end_PI.repository.AulaRepository;
import school.sptech.back_end_PI.repository.AlunoRepository;

@Component
public class AccessGuard {

    // Chaves do catálogo de telas (tabela permissao). A permissão de uma tela é o
    // que libera o domínio inteiro: a tela X e todos os endpoints de X.
    public static final String TELA_GERAL = "TELA_GERAL";
    public static final String TELA_AGENDA = "TELA_AGENDA";
    public static final String TELA_DASHBOARD = "TELA_DASHBOARD";
    public static final String TELA_PROFESSORES = "TELA_PROFESSORES";
    public static final String TELA_TURMAS = "TELA_TURMAS";
    public static final String TELA_ALUNOS = "TELA_ALUNOS";
    public static final String TELA_CONTRATOS = "TELA_CONTRATOS";

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

    // A tela veio do professor_permissao e entrou como authority no login
    public boolean temPermissao(Authentication authentication, String tela) {
        if (authentication == null || tela == null) {
            return false;
        }
        String authority = Permissao.PREFIXO_AUTHORITY + tela;
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(authority));
    }

    public boolean podeVerGeral(Authentication authentication) {
        return temPermissao(authentication, TELA_GERAL);
    }

    public boolean podeVerAulas(Authentication authentication) {
        return temPermissao(authentication, TELA_AGENDA);
    }

    public boolean podeVerDashboard(Authentication authentication) {
        return temPermissao(authentication, TELA_DASHBOARD);
    }

    public boolean podeVerProfessores(Authentication authentication) {
        return temPermissao(authentication, TELA_PROFESSORES);
    }

    public boolean podeVerTurmas(Authentication authentication) {
        return temPermissao(authentication, TELA_TURMAS);
    }

    public boolean podeVerAlunos(Authentication authentication) {
        return temPermissao(authentication, TELA_ALUNOS);
    }

    public boolean podeVerContratos(Authentication authentication) {
        return temPermissao(authentication, TELA_CONTRATOS);
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

    // Quem tem a tela do domínio administra qualquer registro dele; quem não tem
    // só alcança o que é próprio.
    public boolean canManageProfessor(Long id, Authentication authentication) {
        if (podeVerProfessores(authentication)) {
            return true;
        }

        Professor professor = getProfessorFromAuthentication(authentication);
        Professor currentProfessor = getCurrentProfessor();
        if (professor == null || currentProfessor == null) return false;

        return professor.getId().equals(currentProfessor.getId());
    }

    public boolean canManageContrato(Long id, Authentication authentication) {
        if (podeVerContratos(authentication)) {
            return true;
        }

        Professor professor = getProfessorFromAuthentication(authentication);
        if (professor == null) return false;

        return contratoRepository.existsByIdAndProfessorId(id, professor.getId());
    }

    public boolean canManageTurma(Long id, Authentication authentication) {
        if (podeVerTurmas(authentication)) {
            return true;
        }

        Professor professor = getProfessorFromAuthentication(authentication);
        if (professor == null) return false;

        return turmaRepository.existsByIdAndProfessorId(id, professor.getId());
    }

    public boolean canManageAula(Long id, Authentication authentication) {
        if (podeVerAulas(authentication)) {
            return true;
        }

        Professor professor = getProfessorFromAuthentication(authentication);
        if (professor == null) return false;

        return aulaRepository.existsByIdAndContratoProfessorId(id, professor.getId());
    }

    public boolean canManageAluno(Long id, Authentication authentication) {
        if (podeVerAlunos(authentication)) {
            return true;
        }

        Professor professor = getProfessorFromAuthentication(authentication);
        if (professor == null) return false;

        return contratoRepository.existsByProfessorIdAndAlunoId(professor.getId(), id);
    }
}
