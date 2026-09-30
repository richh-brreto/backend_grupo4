package school.sptech.back_end_PI.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

// Catálogo de telas que podem ser liberadas a um professor.
// As linhas são cadastradas no banco; o código não fixa quais telas existem.
@Entity
@Table(name = "permissao")
public class Permissao {

    public static final String PREFIXO_AUTHORITY = "PERM_";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_permissao")
    private Integer id;

    @Column(name = "nome", length = 45, nullable = false, unique = true)
    private String nome;

    public Permissao() {
    }

    public Permissao(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    // "TELA_AGENDA" -> "PERM_TELA_AGENDA", usado como authority do Spring Security
    public String authority() {
        return nome == null ? null : PREFIXO_AUTHORITY + nome;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Permissao permissao)) return false;
        return nome != null && nome.equalsIgnoreCase(permissao.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome == null ? null : nome.toUpperCase());
    }
}
