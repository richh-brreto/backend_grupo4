package school.sptech.back_end_PI.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EntityNotFound extends RuntimeException {
    public EntityNotFound(String message) {
        super(message);
    }
}