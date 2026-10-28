package school.sptech.back_end_PI.dto.aula;

import jakarta.validation.constraints.Size;

public class LinkAulaRequest {

    @Size(max = 2048, message = "O link não pode ultrapassar 2048 caracteres")
    private String linkAula;

    public String getLinkAula() {
        return linkAula;
    }

    public void setLinkAula(String linkAula) {
        this.linkAula = linkAula;
    }
}
