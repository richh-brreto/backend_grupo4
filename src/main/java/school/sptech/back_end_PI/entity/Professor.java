package school.sptech.back_end_PI.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "professor")
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "professor_id")
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
    @JoinColumn(name = "tipo_professor_idtipo_professor")
    private TipoProfessor tipo;

    public Professor() {
    }

    public Professor(Integer id, String nome, String email, Integer telefone, String senha, TipoProfessor tipo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.senha = senha;
        this.tipo = tipo;
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
}