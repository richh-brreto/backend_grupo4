package school.sptech.back_end_PI.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tipo_professor")
public class TipoProfessor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idtipo_professor")
    private Integer id;

    @Column(name = "tipo_professor", length = 45, nullable = false)
    private String nomeTipo;

    public Integer getId() {
        return id;
    }

    public String getNomeTipo() {
        return nomeTipo;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNomeTipo(String nomeTipo) {
        this.nomeTipo = nomeTipo;
    }
}