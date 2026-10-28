package school.sptech.back_end_PI.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import school.sptech.back_end_PI.repository.AlunoRepository;
import school.sptech.back_end_PI.repository.ProfessorRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final ProfessorRepository professorRepository;
    private final AlunoRepository alunoRepository;

    public CustomUserDetailsService(ProfessorRepository professorRepository,
                                   AlunoRepository alunoRepository) {
        this.professorRepository = professorRepository;
        this.alunoRepository = alunoRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var professor = professorRepository.findByEmail(username);
        if (professor.isPresent()) {
            return professor.get();
        }

        var aluno = alunoRepository.findByEmail(username);
        if (aluno.isPresent()) {
            return aluno.get();
        }

        throw new UsernameNotFoundException("Credenciais inválidas");
    }
}