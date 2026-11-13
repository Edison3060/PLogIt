package io.muzoo.ssc.plogit.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;

@Component
public class AuthRateLimitInterceptor implements HandlerInterceptor {

    private static final int MAX_TRACKED_CLIENTS = 10000;
    private static final long LOGIN_WINDOW_MILLIS = 60_000;
    private static final long REGISTER_WINDOW_MILLIS = 600_000;

    private final Map<String, Window> attempts = new HashMap<>();
    private final int loginLimit;
    private final int registerLimit;

    public AuthRateLimitInterceptor(
        @Value("${plogit.auth.rate-limit.login-attempts:20}") int loginLimit,
        @Value("${plogit.auth.rate-limit.registration-attempts:5}") int registerLimit
    ) {
        if (loginLimit < 1 || registerLimit < 1) {
            throw new IllegalArgumentException("Authentication rate limits must be positive");
        }
        this.loginLimit = loginLimit;
        this.registerLimit = registerLimit;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!"POST".equals(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();
        if ("/api/auth/login".equals(path)) {
            check("login", request.getRemoteAddr(), loginLimit, LOGIN_WINDOW_MILLIS);
        } else if ("/api/auth/register".equals(path)) {
            check("register", request.getRemoteAddr(), registerLimit, REGISTER_WINDOW_MILLIS);
        }
        return true;
    }

    private synchronized void check(String action, String address, int limit, long windowMillis) {
        long now = System.currentTimeMillis();
        String key = action + ":" + address;
        Window previous = attempts.get(key);

        if (previous == null || previous.expiresAt() <= now) {
            if (previous == null && attempts.size() >= MAX_TRACKED_CLIENTS) {
                attempts.entrySet().removeIf(entry -> entry.getValue().expiresAt() <= now);
                if (attempts.size() >= MAX_TRACKED_CLIENTS) {
                    throw new RateLimitExceededException(60);
                }
            }
            attempts.put(key, new Window(1, now + windowMillis));
            return;
        }

        if (previous.count() >= limit) {
            long retryAfter = Math.max(1, (previous.expiresAt() - now + 999) / 1000);
            throw new RateLimitExceededException(retryAfter);
        }
        attempts.put(key, new Window(previous.count() + 1, previous.expiresAt()));
    }

    private record Window(int count, long expiresAt) {
    }

    public static class RateLimitExceededException extends RuntimeException {
        private final long retryAfterSeconds;

        public RateLimitExceededException(long retryAfterSeconds) {
            super("Too many authentication requests. Try again later");
            this.retryAfterSeconds = retryAfterSeconds;
        }

        public long getRetryAfterSeconds() {
            return retryAfterSeconds;
        }
    }
}
