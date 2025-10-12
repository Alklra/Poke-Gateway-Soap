package com.pokeapi.infrastructure.aspect;

import com.pokeapi.infrastructure.persistence.entity.RequestLog;
import com.pokeapi.infrastructure.persistence.repository.RequestLogRepository;
import org.aspectj.lang.ProceedingJoinPoint;
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
        String methodName = joinPoint.getSignature().getName();
        String ipAddress = getClientIp();
        RequestLog log = new RequestLog(ipAddress, methodName);
        
        // Capturar request si está disponible
        if (joinPoint.getArgs().length > 0) {
            log.setRequest(joinPoint.getArgs()[0].toString());
        }
        
        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            
            log.setDuration(System.currentTimeMillis() - startTime);
            log.setResponse(result.toString());
            
            requestLogRepository.save(log);
            return result;
        } catch (Exception e) {
            log.setResponse("Error: " + e.getMessage());
            log.setDuration(System.currentTimeMillis() - startTime);
            requestLogRepository.save(log);
            throw e;
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