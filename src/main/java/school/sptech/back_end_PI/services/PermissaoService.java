package school.sptech.back_end_PI.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sptech.back_end_PI.dto.permissao.PermissaoResponse;
import school.sptech.back_end_PI.entity.Permissao;
import school.sptech.back_end_PI.exception.BusinessRuleException;
import school.sptech.back_end_PI.mapper.PermissaoMapper;
import school.sptech.back_end_PI.repository.PermissaoRepository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class PermissaoService {

    private final PermissaoRepository permissaoRepository;

    public PermissaoService(PermissaoRepository permissaoRepository) {
        this.permissaoRepository = permissaoRepository;
    }

    // Catálogo de telas para o front montar os checkboxes do cadastro de professor
    public List<PermissaoResponse> findAll() {
        return PermissaoMapper.toResponseList(permissaoRepository.findAllByOrderByNomeAsc());
    }

    /*
     * Traduz os nomes de tela vindos do front (ex.: "TELA_AGENDA") para as entidades.
     * Aceita diferença de caixa. Nome desconhecido vira erro em vez de sumir em
     * silêncio, para ninguém liberar menos telas sem perceber.
     */
    @Transactional(readOnly = true)
    public Set<Permissao> resolverPorNomes(List<String> nomes) {
        Set<Permissao> resolvidas = new LinkedHashSet<>();
        if (nomes == null || nomes.isEmpty()) {
            return resolvidas;
        }

        Set<String> nomesNormalizados = new LinkedHashSet<>();
        for (String nome : nomes) {
            if (nome != null && !nome.isBlank()) {
                nomesNormalizados.add(nome.trim().toUpperCase(Locale.ROOT));
            }
        }

        if (nomesNormalizados.isEmpty()) {
            return resolvidas;
        }

        List<Permissao> encontradas = permissaoRepository.findByNomeIgnoreCaseIn(nomesNormalizados);
        for (Permissao permissao : encontradas) {
            resolvidas.add(permissao);
        }

        Set<String> encontradasNormalizadas = new LinkedHashSet<>();
        for (Permissao permissao : encontradas) {
            encontradasNormalizadas.add(permissao.getNome().toUpperCase(Locale.ROOT));
        }

        List<String> invalidas = nomesNormalizados.stream()
                .filter(nome -> !encontradasNormalizadas.contains(nome))
                .toList();

        if (!invalidas.isEmpty()) {
            throw new BusinessRuleException("Permissão(ões) inexistente(s): " + String.join(", ", invalidas));
        }

        return resolvidas;
    }
}
