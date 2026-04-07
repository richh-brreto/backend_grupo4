package school.sptech.back_end_PI.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "Professor")
public class Professor extends Pessoa {
    public Professor() {
    }

    public Professor(Long id) {
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
