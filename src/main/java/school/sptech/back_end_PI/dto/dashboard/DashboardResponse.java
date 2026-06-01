package school.sptech.back_end_PI.dto.dashboard;

import java.util.List;

public class DashboardResponse {
    private Integer totalProfessores;
    private Integer totalTurmas;
    private Double totalHorasLivres;
    private Integer professoresSobrecarregados;
    private List<DashboardProfessorItem> detalhes;

    public DashboardResponse() {
    }

    public DashboardResponse(Integer totalProfessores, Integer totalTurmas, Double totalHorasLivres, Integer professoresSobrecarregados, List<DashboardProfessorItem> detalhes) {
        this.totalProfessores = totalProfessores;
        this.totalTurmas = totalTurmas;
        this.totalHorasLivres = totalHorasLivres;
        this.professoresSobrecarregados = professoresSobrecarregados;
        this.detalhes = detalhes;
    }

    public Integer getTotalProfessores() {
        return totalProfessores;
    }

    public void setTotalProfessores(Integer totalProfessores) {
        this.totalProfessores = totalProfessores;
    }

    public Integer getTotalTurmas() {
        return totalTurmas;
    }

    public void setTotalTurmas(Integer totalTurmas) {
        this.totalTurmas = totalTurmas;
    }

    public Double getTotalHorasLivres() {
        return totalHorasLivres;
    }

    public void setTotalHorasLivres(Double totalHorasLivres) {
        this.totalHorasLivres = totalHorasLivres;
    }

    public Integer getProfessoresSobrecarregados() {
        return professoresSobrecarregados;
    }

    public void setProfessoresSobrecarregados(Integer professoresSobrecarregados) {
        this.professoresSobrecarregados = professoresSobrecarregados;
    }

    public List<DashboardProfessorItem> getDetalhes() {
        return detalhes;
    }

    public void setDetalhes(List<DashboardProfessorItem> detalhes) {
        this.detalhes = detalhes;
    }
}

