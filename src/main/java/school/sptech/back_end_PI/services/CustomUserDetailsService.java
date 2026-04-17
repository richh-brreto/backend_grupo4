package school.sptech.back_end_PI.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import school.sptech.back_end_PI.repository.ProfessorRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final ProfessorRepository professorRepository;

    public CustomUserDetailsService(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return professorRepository.findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Professor não encontrado: " + username)
                );
    }
}