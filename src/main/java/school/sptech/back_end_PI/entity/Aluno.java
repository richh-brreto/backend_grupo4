package school.sptech.back_end_PI.entity;
import jakarta.persistence.*;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "aluno")
public class Aluno extends Pessoa{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return id;
    }

    public Aluno() {
    }

    public Aluno(Long id) {
        this.id = id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
    @Override
    public @Nullable String getPassword() {
        return "";
    }
    @Override
    public String getUsername() {
        return "";
    }

}
