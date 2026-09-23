package school.sptech.back_end_PI.services;

import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import school.sptech.back_end_PI.dto.auth.PrimeiroAcessoRequest;
import school.sptech.back_end_PI.dto.auth.PrimeiroAcessoResponse;
import school.sptech.back_end_PI.entity.Aluno;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.exception.BusinessRuleException;
import school.sptech.back_end_PI.exception.ConflictException;
import school.sptech.back_end_PI.repository.AlunoRepository;
import school.sptech.back_end_PI.repository.ProfessorRepository;

import java.util.Optional;

@Service
public class PrimeiroAcessoService {

    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService audit;

    public PrimeiroAcessoService(AlunoRepository alunoRepository,
                                 ProfessorRepository professorRepository,
                                 PasswordEncoder passwordEncoder,
                                 AuditService audit) {
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
        this.passwordEncoder = passwordEncoder;
        this.audit = audit;
    }

    @Transactional
    public PrimeiroAcessoResponse definirSenha(PrimeiroAcessoRequest request) {
        if (request.getConfirmacaoSenha() != null
                && !request.getConfirmacaoSenha().equals(request.getNovaSenha())) {
            throw new BusinessRuleException("As senhas não coincidem");
        }

        String email = request.getEmail().trim().toLowerCase();

        Optional<Professor> professor = professorRepository.findByEmailIgnoreCase(email);
        if (professor.isPresent()) {
            return processarProfessor(professor.get(), request, email);
        }

        Optional<Aluno> aluno = alunoRepository.findByEmailIgnoreCase(email);
        if (aluno.isPresent()) {
            return processarAluno(aluno.get(), request, email);
        }

        throw new BusinessRuleException("Email ou código de acesso inválidos");
    }

    private PrimeiroAcessoResponse processarProfessor(Professor professor, PrimeiroAcessoRequest request, String email) {
        validarCodigoAcesso(professor.getCodigoAcesso(), request.getCodigoAcesso());
        validarUsuarioSemSenha(professor.getSenha());

        professor.setSenha(passwordEncoder.encode(request.getNovaSenha()));
        professor.setCodigoAcesso(null);

        professorRepository.save(professor);
        audit.log("firstAccess", email, "professor:" + professor.getId(), "success");

        return new PrimeiroAcessoResponse("Senha configurada com sucesso", email);
    }

    private PrimeiroAcessoResponse processarAluno(Aluno aluno, PrimeiroAcessoRequest request, String email) {
        validarCodigoAcesso(aluno.getCodigoAcesso(), request.getCodigoAcesso());
        validarUsuarioSemSenha(aluno.getSenha());

        aluno.setSenha(passwordEncoder.encode(request.getNovaSenha()));
        aluno.setCodigoAcesso(null);

        alunoRepository.save(aluno);
        audit.log("firstAccess", email, "aluno:" + aluno.getId(), "success");

        return new PrimeiroAcessoResponse("Senha configurada com sucesso", email);
    }

    private void validarCodigoAcesso(String codigoCadastrado, String codigoInformado) {
        if (codigoCadastrado == null || !codigoCadastrado.equals(codigoInformado)) {
            throw new BusinessRuleException("Email ou código de acesso inválidos");
        }
    }

    private void validarUsuarioSemSenha(String senha) {
        if (senha != null && !senha.isBlank()) {
            throw new ConflictException("Este usuário já possui senha cadastrada");
        }
    }
}