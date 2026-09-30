package school.sptech.back_end_PI.dto.permissao;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "nome"})
public class PermissaoResponse {

    private Integer id;
    private String nome;

    public PermissaoResponse() {}

    public PermissaoResponse(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
