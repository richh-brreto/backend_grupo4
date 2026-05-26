package school.sptech.back_end_PI.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    @NotNull
    private Integer telefone;

    @NotBlank
    private String senha;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @ManyToOne
    @JoinColumn(name = "tipo_professor_id_tipo_professor")
    private TipoProfessor tipo;

    @ManyToMany
    @JoinTable(
            name = "disponibilidade_professor",
            joinColumns = @JoinColumn(name = "professor_id_professor"),
            inverseJoinColumns = @JoinColumn(name = "horario_id_horario")
    )
    private List<Horario> horarios = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_PROFESSOR"));
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

    public Professor(Long id, String nome, String email, Integer telefone, String senha, Boolean ativo, TipoProfessor tipo, List<Horario> horarios) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.senha = senha;
        this.ativo = ativo;
        this.tipo = tipo;
        this.horarios = horarios;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getTelefone() { return telefone; }
    public void setTelefone(Integer telefone) { this.telefone = telefone; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    public TipoProfessor getTipo() { return tipo; }
    public void setTipo(TipoProfessor tipo) { this.tipo = tipo; }
    public List<Horario> getHorarios() { return horarios; }
    public void setHorarios(List<Horario> horarios) { this.horarios = horarios; }
}