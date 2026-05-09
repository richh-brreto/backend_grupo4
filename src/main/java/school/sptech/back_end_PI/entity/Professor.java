package school.sptech.back_end_PI.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "professor")
public class Professor implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_professor")
    private Integer id;

    @NotBlank
    private String nome;

    @NotBlank
    private String email;

    @NotNull
    private Integer telefone;

    @NotBlank
    private String senha;

    // Relacionamento com a tabela de tipos
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
    public String getUsername() {
        return getEmail();
    }

    @Override
    public String getPassword() {
        return getSenha();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }

    public Professor() {
    }

    public Professor(Integer id, String nome, String email, Integer telefone, String senha, TipoProfessor tipo, List<Horario> horarios) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.senha = senha;
        this.tipo = tipo;
        this.horarios = horarios;
    }

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public Integer getTelefone() {
        return telefone;
    }

    public String getSenha() {
        return senha;
    }

    public TipoProfessor getTipo() {
        return tipo;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelefone(Integer telefone) {
        this.telefone = telefone;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setTipo(TipoProfessor tipo) {
        this.tipo = tipo;
    }

    public List<Horario> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<Horario> horarios) {
        this.horarios = horarios;
    }
}