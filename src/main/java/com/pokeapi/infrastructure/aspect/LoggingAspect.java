package com.pokeapi.infrastructure.aspect;

import com.pokeapi.infrastructure.persistence.entity.RequestLog;
import com.pokeapi.infrastructure.persistence.repository.RequestLogRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Optional;

@Aspect
@Component
public class LoggingAspect {
    
    private final RequestLogRepository requestLogRepository;

    public LoggingAspect(RequestLogRepository requestLogRepository) {
        this.requestLogRepository = requestLogRepository;
    }

    @Around("@within(org.springframework.ws.server.endpoint.annotation.Endpoint)")
    public Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature sig = joinPoint.getSignature();
        String methodName = (sig != null) ? sig.getName() : "unknown";
        String ipAddress = getClientIp();
        RequestLog log = new RequestLog(ipAddress, methodName);

        // Capturar request si está disponible y no nulo
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0 && args[0] != null) {
            log.setRequest(args[0].toString());
        }

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();

            log.setDuration(System.currentTimeMillis() - startTime);
            log.setResponse(result != null ? result.toString() : "null");

            requestLogRepository.save(log);
            return result;
        } catch (Throwable t) {
            // Guardar info del error y re-lanzar
            log.setResponse("Error: " + (t == null ? "null" : t.getMessage()));
            log.setDuration(System.currentTimeMillis() - startTime);
            requestLogRepository.save(log);
            // Si es un Error o unchecked, rethrow preserving type
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            if (t instanceof Error) {
                throw (Error) t;
            }
            // checked Throwable -> wrap or rethrow as Throwable (method signature allows it)
            throw t;
        }
    }
    
    private String getClientIp() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
            .filter(ServletRequestAttributes.class::isInstance)
            .map(ServletRequestAttributes.class::cast)
            .map(ServletRequestAttributes::getRequest)
            .map(this::extractIp)
            .orElse("unknown");
    }
    
    private String extractIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isEmpty()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}