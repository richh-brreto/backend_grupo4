package school.sptech.back_end_PI.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private static final Logger audit = LoggerFactory.getLogger("audit");

    public void log(String action, String actor, String target, String result) {
        audit.info("AUDIT action={} actor={} target={} result={}", action, actor, target, result);
    }

    public String currentActor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "anonymous";
        }
        return auth.getName();
    }
}
