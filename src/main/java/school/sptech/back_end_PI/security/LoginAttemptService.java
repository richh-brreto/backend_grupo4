package school.sptech.back_end_PI.security;

import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_MS = 10 * 60 * 1000L;

    private final ConcurrentHashMap<String, Deque<Long>> attempts = new ConcurrentHashMap<>();

    public boolean isBlocked(String key) {
        Deque<Long> times = attempts.get(key);
        if (times == null) {
            return false;
        }
        synchronized (times) {
            prune(times);
            return times.size() >= MAX_ATTEMPTS;
        }
    }

    public void registerFailure(String key) {
        Deque<Long> times = attempts.computeIfAbsent(key, k -> new ArrayDeque<>());
        synchronized (times) {
            prune(times);
            times.addLast(System.currentTimeMillis());
        }
    }

    public void reset(String key) {
        attempts.remove(key);
    }

    private void prune(Deque<Long> times) {
        long cutoff = System.currentTimeMillis() - WINDOW_MS;
        while (!times.isEmpty() && times.peekFirst() < cutoff) {
            times.pollFirst();
        }
    }
}
