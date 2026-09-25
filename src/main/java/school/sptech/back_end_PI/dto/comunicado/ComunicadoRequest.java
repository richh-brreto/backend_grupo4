package school.sptech.back_end_PI.dto.comunicado;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ComunicadoRequest {

    @NotBlank
    @Size(max = 100)
    private String titulo;

    @NotBlank
    @Size(max = 5000)
    private String texto;

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
}
