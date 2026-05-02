package school.sptech.back_end_PI.dto;

public class ProfessorResponse {
    private Integer id;
    private String nome;
    private String email;
    private Integer telefone;

    // O Professor tem um Tipo
    private TipoProfessorResponse tipo;

    public ProfessorResponse() {}

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getTelefone() { return telefone; }
    public void setTelefone(Integer telefone) { this.telefone = telefone; }

    public TipoProfessorResponse getTipo() { return tipo; }
    public void setTipo(TipoProfessorResponse tipo) { this.tipo = tipo; }
}