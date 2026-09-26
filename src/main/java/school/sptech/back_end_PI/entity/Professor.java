package school.sptech.back_end_PI.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "professor")
@SQLDelete(sql = "UPDATE professor SET ativo = 0 WHERE id_professor = ?")
@SQLRestriction("ativo = 1")
public class Professor implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_professor")
    private Long id;

    @NotBlank
    private String nome;

    @NotBlank
    private String email;

    @NotBlank
    @Column(length = 20)
    @Size(min = 10, max = 14)
    private String telefone;

    // NULL até o usuário definir a senha no primeiro acesso
    private String senha;

    @Column(name = "codigo_acesso", unique = true)
    private String codigoAcesso;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @ManyToMany
    @JoinTable(
            name = "disponibilidade_professor",
            joinColumns = @JoinColumn(name = "professor_id_professor"),
            inverseJoinColumns = @JoinColumn(name = "horario_id_horario")
    )
    private List<Horario> horarios = new ArrayList<>();

    // Telas liberadas para este professor. É a única fonte de autorização:
    // o catálogo vem da tabela permissao, então criar uma tela nova é só
    // inserir uma linha nela - nada no código.
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "professor_permissao",
            joinColumns = @JoinColumn(name = "professor_id_professor"),
            inverseJoinColumns = @JoinColumn(name = "permissao_id_permissao")
    )
    private Set<Permissao> permissoes = new LinkedHashSet<>();

    // Só as telas: o Spring Security resolve o acesso a partir delas, sem papel fixo
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (permissoes == null) {
            return authorities;
        }

        for (Permissao permissao : permissoes) {
            String authority = permissao.authority();
            if (authority != null && !authority.isBlank()) {
                authorities.add(new SimpleGrantedAuthority(authority));
            }
        }

        return authorities;
    }

    public boolean temTela(String nomeTela) {
        if (nomeTela == null || permissoes == null) {
            return false;
        }
        return permissoes.stream()
                .map(Permissao::getNome)
                .anyMatch(nome -> nomeTela.equalsIgnoreCase(nome));
    }

    @Override
    public String getUsername() { return getEmail(); }

    @Override
    public String getPassword() { return getSenha(); }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() {
        return this.ativo; // 🔐 Se sofrer soft delete, perde o acesso ao login automaticamente
    }

    public Professor() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getCodigoAcesso() { return codigoAcesso; }
    public void setCodigoAcesso(String codigoAcesso) { this.codigoAcesso = codigoAcesso; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    public List<Horario> getHorarios() { return horarios; }
    public void setHorarios(List<Horario> horarios) { this.horarios = horarios; }
    public Set<Permissao> getPermissoes() { return permissoes; }
    public void setPermissoes(Set<Permissao> permissoes) { this.permissoes = permissoes; }

    // Nomes das telas liberadas (ex.: TELA_AGENDA), em ordem alfabética
    public List<String> nomesPermissoes() {
        if (permissoes == null || permissoes.isEmpty()) {
            return List.of();
        }
        return permissoes.stream()
                .map(Permissao::getNome)
                .filter(nome -> nome != null && !nome.isBlank())
                .sorted()
                .toList();
    }
}