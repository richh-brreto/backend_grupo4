package school.sptech.back_end_PI.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import school.sptech.back_end_PI.entity.Professor;

@Component
public class AccessGuard {

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

    public boolean canManageProfessor(Long id, Authentication authentication) {
        Professor professor = getProfessorFromAuthentication(authentication);
        if (professor == null) return false;

        Professor currentProfessor = getCurrentProfessor();
        if (currentProfessor == null) return false;

        if (authentication != null && authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_COORDENADOR"))) {
            return true;
        }

        return professor.getId().equals(currentProfessor.getId());
    }

    public boolean canManageContrato(Long id, Authentication authentication) {
        if (authentication != null && authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_COORDENADOR"))) {
            return true;
        }
        return canManageProfessor(id, authentication);
    }

    public boolean canManageAula(Long id, Authentication authentication) {
        if (authentication != null && authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_COORDENADOR"))) {
            return true;
        }
        Professor professor = getProfessorFromAuthentication(authentication);
        if (professor == null) return false;
        Professor currentProfessor = getCurrentProfessor();
        if (currentProfessor == null) return false;
        return professor.getId().equals(currentProfessor.getId());
    }

    public boolean canManageTurma(Long id, Authentication authentication) {
        if (authentication != null && authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_COORDENADOR"))) {
            return true;
        }
        return canManageProfessor(id, authentication);
    }

    public boolean canManageAluno(Long id, Authentication authentication) {
        if (authentication != null && authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_COORDENADOR"))) {
            return true;
        }
        Professor professor = getProfessorFromAuthentication(authentication);
        if (professor == null) return false;
        Professor currentProfessor = getCurrentProfessor();
        if (currentProfessor == null) return false;
        return professor.getId().equals(currentProfessor.getId());
    }

    private Professor getProfessorFromAuthentication(Authentication authentication) {
        if (authentication == null) return null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof Professor) {
            return (Professor) principal;
        }
        return null;
    }
}