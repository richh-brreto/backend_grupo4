package school.sptech.back_end_PI.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "aluno")
@SQLDelete(sql = "UPDATE aluno SET ativo = 0 WHERE id_aluno = ?")
@SQLRestriction("ativo = 1") // Filtra automaticamente tirando os inativos dos findall/findbyid
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_aluno")
    private Long id;

    @NotBlank
    private String nome;

    @NotBlank
    private String email;

    @Size(max = 11)
    private String telefone;

    @NotBlank
    private String nivel;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @ManyToMany
    @JoinTable(
            name = "disponibilidade_aluno",
            joinColumns = @JoinColumn(name = "aluno_id_aluno"),
            inverseJoinColumns = @JoinColumn(name = "horario_id_horario")
    )
    private List<Horario> horarios = new ArrayList<>();

    public Aluno() {
    }

    public Aluno(Long id, String nome, String email, String telefone, String nivel, Boolean ativo, List<Horario> horarios) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.nivel = nivel;
        this.ativo = ativo;
        this.horarios = horarios;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public List<Horario> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<Horario> horarios) {
        this.horarios = horarios;
    }
}