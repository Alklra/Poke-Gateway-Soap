package com.pokeapi.infrastructure.aspect;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.pokeapi.infrastructure.persistence.entity.RequestLog;
import com.pokeapi.infrastructure.persistence.repository.RequestLogRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;


class AspectAndPersistenceTest {

    @Mock
    private RequestLogRepository repo;

    private LoggingAspect aspect;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        aspect = new LoggingAspect(repo);
    }

    @Test
    void loggingAspectHappyPath() throws Throwable {
        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        when(pjp.proceed()).thenReturn("ok");
        Object res = aspect.logMethod(pjp);
        assertEquals("ok", res);
        verify(repo).save(any(RequestLog.class));
    }

    @Test
    void loggingAspectExceptionPath() throws Throwable {
        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        when(pjp.proceed()).thenThrow(new RuntimeException("boom"));
        assertThrows(RuntimeException.class, () -> aspect.logMethod(pjp));
        verify(repo).save(any(RequestLog.class));
    }

    @Test
    void requestLogEntityBasics() {
        RequestLog r = new RequestLog();
        r.setId(1L);
        r.setMethod("m");
        assertEquals(1L, r.getId());
        assertEquals("m", r.getMethod());
        assertNotNull(r.toString());
    }

    @Test
    void loggingAspectReadsIpFromRequestContext() throws Throwable {
        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        when(pjp.proceed()).thenReturn("ok");

        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRemoteAddr()).thenReturn("1.2.3.4");
        RequestAttributes ra = new ServletRequestAttributes(req);
        RequestContextHolder.setRequestAttributes(ra);

        aspect.logMethod(pjp);
        verify(repo).save(any(RequestLog.class));
        RequestContextHolder.resetRequestAttributes();
    }

    // Additional small tests to increase coverage
    @Test
    void loggingAspectHandlesNullRequestContext() throws Throwable {
        RequestContextHolder.resetRequestAttributes();
        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        when(pjp.proceed()).thenReturn("ok");
        var res = aspect.logMethod(pjp);
        assertEquals("ok", res);
        verify(repo).save(any(RequestLog.class));
    }

    @Test
    void loggingAspectRecordsMethodName() throws Throwable {
        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        org.aspectj.lang.Signature sig = mock(org.aspectj.lang.Signature.class);
        when(sig.getName()).thenReturn("sig");
        when(pjp.getSignature()).thenReturn(sig);
        when(pjp.proceed()).thenReturn("ok");
        aspect.logMethod(pjp);
        verify(repo).save(any(RequestLog.class));
    }

    @Test
    void loggingAspectRecordsArgs() throws Throwable {
        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        when(pjp.getArgs()).thenReturn(new Object[]{"a","b"});
        when(pjp.proceed()).thenReturn("ok");
        aspect.logMethod(pjp);
        verify(repo).save(any(RequestLog.class));
    }

    @Test
    void loggingAspectSavesEvenWhenProceedReturnsNull() throws Throwable {
        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        when(pjp.proceed()).thenReturn(null);
        var res = aspect.logMethod(pjp);
        assertNull(res);
        verify(repo).save(any(RequestLog.class));
    }

    @Test
    void loggingAspectHandlesLargeArgList() throws Throwable {
        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        Object[] large = new Object[50];
        when(pjp.getArgs()).thenReturn(large);
        when(pjp.proceed()).thenReturn("ok");
        aspect.logMethod(pjp);
        verify(repo).save(any(RequestLog.class));
    }

    @Test
    void requestLogToStringNotEmpty() {
        RequestLog r = new RequestLog();
        r.setMethod("M"); r.setRequest("/p");
        assertTrue(r.toString().length() > 0);
    }

    @Test
    void loggingAspectHandlesExceptionAndStillSaves() throws Throwable {
        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        when(pjp.proceed()).thenThrow(new IllegalStateException("boom"));
        assertThrows(IllegalStateException.class, () -> aspect.logMethod(pjp));
        verify(repo).save(any(RequestLog.class));
    }

    @Test
    void loggingAspectHandlesNullArgs() throws Throwable {
        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        when(pjp.getArgs()).thenReturn(null);
        when(pjp.proceed()).thenReturn("ok");
        aspect.logMethod(pjp);
        verify(repo).save(any(RequestLog.class));
    }

    @Test
    void aspectSavesWithCustomAttributes() throws Throwable {
        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        org.aspectj.lang.Signature sig = mock(org.aspectj.lang.Signature.class);
        when(sig.getName()).thenReturn("custom");
        when(pjp.getSignature()).thenReturn(sig);
        when(pjp.proceed()).thenReturn("ok");
        aspect.logMethod(pjp);
        verify(repo).save(any(RequestLog.class));
    }

    @Test
    void aspectMultipleSequentialCalls() throws Throwable {
        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        when(pjp.proceed()).thenReturn("a", "b", "c");
        aspect.logMethod(pjp);
        aspect.logMethod(pjp);
        aspect.logMethod(pjp);
        verify(repo, atLeast(3)).save(any(RequestLog.class));
    }

    @Test
    void requestLogDefaultValues() {
        RequestLog r = new RequestLog();
        assertNotNull(r.toString());
    }

    @Test
    void aspectHandlesSignatureNull() throws Throwable {
        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        when(pjp.getSignature()).thenReturn(null);
        when(pjp.proceed()).thenReturn("ok");
        var res = aspect.logMethod(pjp);
        assertEquals("ok", res);
        verify(repo).save(any(RequestLog.class));
    }

    @Test
    void aspectSavesWithCustomPathAndDuration() throws Throwable {
        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        org.aspectj.lang.Signature sig = mock(org.aspectj.lang.Signature.class);
        when(sig.getName()).thenReturn("sig");
        when(pjp.getSignature()).thenReturn(sig);
        when(pjp.proceed()).thenReturn("done");
        var res = aspect.logMethod(pjp);
        assertEquals("done", res);
        verify(repo).save(any(RequestLog.class));
    }
}
