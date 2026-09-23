package school.sptech.back_end_PI;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import school.sptech.back_end_PI.dto.auth.PrimeiroAcessoRequest;
import school.sptech.back_end_PI.dto.auth.PrimeiroAcessoResponse;
import school.sptech.back_end_PI.entity.Aluno;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.exception.BusinessRuleException;
import school.sptech.back_end_PI.exception.ConflictException;
import school.sptech.back_end_PI.repository.AlunoRepository;
import school.sptech.back_end_PI.repository.ProfessorRepository;
import school.sptech.back_end_PI.services.AuditService;
import school.sptech.back_end_PI.services.PrimeiroAcessoService;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PrimeiroAcessoServiceTest {

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private ProfessorRepository professorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditService audit;

    @InjectMocks
    private PrimeiroAcessoService primeiroAcessoService;

    private PrimeiroAcessoRequest requestPadrao(String email, String codigo) {
        PrimeiroAcessoRequest request = new PrimeiroAcessoRequest();
        request.setEmail(email);
        request.setCodigoAcesso(codigo);
        request.setNovaSenha("novaSenha123");
        request.setConfirmacaoSenha("novaSenha123");
        return request;
    }

    @Nested
    public class DefinirSenhaProfessorTestes {

        @Test
        @DisplayName("Deve definir senha do professor no primeiro acesso")
        void deveDefinirSenhaDoProfessor() {
            Professor professor = new Professor();
            professor.setId(1L);
            professor.setEmail("prof@email.com");
            professor.setCodigoAcesso("ABC1234567");

            PrimeiroAcessoRequest request = requestPadrao("prof@email.com", "ABC1234567");

            Mockito.when(professorRepository.findByEmailIgnoreCase("prof@email.com"))
                    .thenReturn(Optional.of(professor));
            Mockito.when(passwordEncoder.encode("novaSenha123")).thenReturn("hash-gerado");

            PrimeiroAcessoResponse resposta = primeiroAcessoService.definirSenha(request);

            Assertions.assertEquals("Senha configurada com sucesso", resposta.getMensagem());
            Assertions.assertEquals("hash-gerado", professor.getSenha());
            Assertions.assertNull(professor.getCodigoAcesso());
            Mockito.verify(professorRepository, Mockito.times(1)).save(professor);
        }

        @Test
        @DisplayName("Deve buscar o professor ignorando diferenças de caixa no email")
        void deveBuscarProfessorIgnorandoCaixaDoEmail() {
            Professor professor = new Professor();
            professor.setId(1L);
            professor.setEmail("prof@email.com");
            professor.setCodigoAcesso("ABC1234567");

            PrimeiroAcessoRequest request = requestPadrao("PROF@EMAIL.COM", "ABC1234567");

            Mockito.when(professorRepository.findByEmailIgnoreCase("prof@email.com"))
                    .thenReturn(Optional.of(professor));
            Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("hash-gerado");

            primeiroAcessoService.definirSenha(request);

            Assertions.assertEquals("hash-gerado", professor.getSenha());
        }
    }

    @Nested
    public class DefinirSenhaAlunoTestes {

        @Test
        @DisplayName("Deve definir senha do aluno no primeiro acesso")
        void deveDefinirSenhaDoAluno() {
            Aluno aluno = new Aluno();
            aluno.setId(1L);
            aluno.setEmail("aluno@email.com");
            aluno.setCodigoAcesso("XYZ9876543");

            PrimeiroAcessoRequest request = requestPadrao("aluno@email.com", "XYZ9876543");

            Mockito.when(professorRepository.findByEmailIgnoreCase("aluno@email.com"))
                    .thenReturn(Optional.empty());
            Mockito.when(alunoRepository.findByEmailIgnoreCase("aluno@email.com"))
                    .thenReturn(Optional.of(aluno));
            Mockito.when(passwordEncoder.encode("novaSenha123")).thenReturn("hash-gerado");

            PrimeiroAcessoResponse resposta = primeiroAcessoService.definirSenha(request);

            Assertions.assertEquals("Senha configurada com sucesso", resposta.getMensagem());
            Assertions.assertEquals("hash-gerado", aluno.getSenha());
            Assertions.assertNull(aluno.getCodigoAcesso());
            Mockito.verify(alunoRepository, Mockito.times(1)).save(aluno);
        }
    }

    @Nested
    public class ValidacoesTestes {

        @Test
        @DisplayName("Deve lançar BusinessRuleException quando email não existe")
        void deveLancarExcecaoQuandoEmailNaoExiste() {
            PrimeiroAcessoRequest request = requestPadrao("naoexiste@email.com", "ABC1234567");

            Mockito.when(professorRepository.findByEmailIgnoreCase("naoexiste@email.com"))
                    .thenReturn(Optional.empty());
            Mockito.when(alunoRepository.findByEmailIgnoreCase("naoexiste@email.com"))
                    .thenReturn(Optional.empty());

            Assertions.assertThrows(BusinessRuleException.class,
                    () -> primeiroAcessoService.definirSenha(request));
        }

        @Test
        @DisplayName("Deve lançar BusinessRuleException quando código de acesso não confere")
        void deveLancarExcecaoQuandoCodigoInvalido() {
            Professor professor = new Professor();
            professor.setId(1L);
            professor.setEmail("prof@email.com");
            professor.setCodigoAcesso("ABC1234567");

            PrimeiroAcessoRequest request = requestPadrao("prof@email.com", "CODIGOERRADO");

            Mockito.when(professorRepository.findByEmailIgnoreCase("prof@email.com"))
                    .thenReturn(Optional.of(professor));

            Assertions.assertThrows(BusinessRuleException.class,
                    () -> primeiroAcessoService.definirSenha(request));
            Mockito.verify(professorRepository, Mockito.never()).save(Mockito.any(Professor.class));
        }

        @Test
        @DisplayName("Deve lançar ConflictException quando usuário já possui senha")
        void deveLancarConflitoQuandoJaPossuiSenha() {
            Professor professor = new Professor();
            professor.setId(1L);
            professor.setEmail("prof@email.com");
            professor.setSenha("ja-tem-senha");
            professor.setCodigoAcesso("ABC1234567");

            PrimeiroAcessoRequest request = requestPadrao("prof@email.com", "ABC1234567");

            Mockito.when(professorRepository.findByEmailIgnoreCase("prof@email.com"))
                    .thenReturn(Optional.of(professor));

            Assertions.assertThrows(ConflictException.class,
                    () -> primeiroAcessoService.definirSenha(request));
        }

        @Test
        @DisplayName("Deve lançar BusinessRuleException quando a confirmação não confere")
        void deveLancarExcecaoQuandoConfirmacaoDiferente() {
            PrimeiroAcessoRequest request = requestPadrao("prof@email.com", "ABC1234567");
            request.setConfirmacaoSenha("outraSenha");

            Assertions.assertThrows(BusinessRuleException.class,
                    () -> primeiroAcessoService.definirSenha(request));
        }
    }
}