package school.sptech.back_end_PI.dto.dashboard;

public class DashboardProfessorItem {
    private Long professorId;
    private String nome;
    private Integer turmasCount;
    private Double horasSemanais;
    private Double horasLivres;
    private String status;

    public DashboardProfessorItem() {
    }

    public DashboardProfessorItem(Long professorId, String nome, Integer turmasCount, Double horasSemanais, Double horasLivres, String status) {
        this.professorId = professorId;
        this.nome = nome;
        this.turmasCount = turmasCount;
        this.horasSemanais = horasSemanais;
        this.horasLivres = horasLivres;
        this.status = status;
    }

    public Long getProfessorId() {
        return professorId;
    }

    public void setProfessorId(Long professorId) {
        this.professorId = professorId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getTurmasCount() {
        return turmasCount;
    }

    public void setTurmasCount(Integer turmasCount) {
        this.turmasCount = turmasCount;
    }

    public Double getHorasSemanais() {
        return horasSemanais;
    }

    public void setHorasSemanais(Double horasSemanais) {
        this.horasSemanais = horasSemanais;
    }

    public Double getHorasLivres() {
        return horasLivres;
    }

    public void setHorasLivres(Double horasLivres) {
        this.horasLivres = horasLivres;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

