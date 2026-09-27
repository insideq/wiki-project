package com.example.wiki.aspect;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.wiki.security.UserPrincipal;

@Aspect
@Component
public class ControllerLoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(ControllerLoggingAspect.class);

    private UserPrincipal getCurrentPrincipal() {
        try {
            final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() &&
                    !(authentication instanceof AnonymousAuthenticationToken)) {
                return (UserPrincipal) authentication.getPrincipal();
            }
            return UserPrincipal.anonymous();
        } catch (Exception e) {
            return UserPrincipal.anonymous();
        }
    }

    private String getRoles(UserPrincipal principal) {
        return principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "));
    }

    // (within(com.example.wiki.api..*)
    // execution(public * *(..))
    @Pointcut("(within(@org.springframework.web.bind.annotation.RestController *) || " +
            "within(@org.springframework.stereotype.Controller *)) && " +
            "execution(* *(..))")
    public void controllerMethods() {
    }

    @Before("controllerMethods()")
    public void logBeforeMethod(JoinPoint joinPoint) {
        final UserPrincipal principal = getCurrentPrincipal();
        final String roles = getRoles(principal);
        final String methodName = joinPoint.getSignature().toShortString();
        final String args = Arrays.toString(joinPoint.getArgs());
        logger.debug("User {} with role {} call {} with args {}",
                principal.getUsername(), roles, methodName, args);
    }

    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logAfterMethod(JoinPoint joinPoint, Object result) {
        final UserPrincipal principal = getCurrentPrincipal();
        final String roles = getRoles(principal);
        final String methodName = joinPoint.getSignature().toShortString();
        String resultStr = Optional.ofNullable(result).map(Object::toString).orElse("null");
        if (resultStr.length() > 200) {
            resultStr = resultStr.substring(0, 200) + "...";
        }
        logger.debug("User {} with role {} call {} and get {}",
                principal.getUsername(), roles, methodName, resultStr);
    }

    @AfterThrowing(pointcut = "controllerMethods()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        final UserPrincipal principal = getCurrentPrincipal();
        final String roles = getRoles(principal);
        final String methodName = joinPoint.getSignature().toShortString();
        logger.error("User {} with role {} call {} and get exception {}: {}",
                principal.getUsername(), roles, methodName, ex.getClass().getSimpleName(), ex.getMessage());
    }
}
