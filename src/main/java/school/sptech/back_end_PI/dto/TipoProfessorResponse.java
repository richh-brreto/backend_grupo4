package school.sptech.back_end_PI.dto;

public class TipoProfessorResponse {
    private Integer id;
    private String tipoProfessor; // nome do campo que aparecerá no JSON

    public TipoProfessorResponse() {}

    public TipoProfessorResponse(Integer id, String tipoProfessor) {
        this.id = id;
        this.tipoProfessor = tipoProfessor;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTipoProfessor() { return tipoProfessor; }
    public void setTipoProfessor(String tipoProfessor) { this.tipoProfessor = tipoProfessor; }
}