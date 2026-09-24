package school.sptech.back_end_PI.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "comunicado")
public class Comunicado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comunicado")
    private Long id;

    @NotBlank
    @Column(length = 100, nullable = false)
    private String titulo;

    @NotBlank
    @Column(columnDefinition = "TEXT", nullable = false)
    private String texto;

    // Definida pelo servidor, nunca pelo cliente
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    // Coordenador que cadastrou o comunicado
    @ManyToOne
    @JoinColumn(name = "professor_id_professor")
    private Professor autor;

    public Comunicado() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public Professor getAutor() { return autor; }
    public void setAutor(Professor autor) { this.autor = autor; }
}
