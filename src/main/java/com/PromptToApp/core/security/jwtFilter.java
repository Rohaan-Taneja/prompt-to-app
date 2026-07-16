package com.PromptToApp.core.security;

import com.PromptToApp.core.CustomExceptionHandling.ResourceNotFoundException;
import com.PromptToApp.core.CustomExceptionHandling.customUnauthorizedException;
import com.PromptToApp.core.Entity.User;
import com.PromptToApp.core.repository.userRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class jwtFilter extends OncePerRequestFilter {


    private final authUtilService jwt_service;

    private final userRepository user_repo;

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {

//        check for condition is req has header authentication , bearer token -> get token -> validated token -> user in context and move aage

            String auth_header = request.getHeader("Authorization");

            log.info("this is the incoming auth header {}" ,  auth_header);

//        if header is null or if it is not "Bearer f34ur43r"
            if(auth_header == null || !auth_header.startsWith("Bearer ")){

//            throw new customUnauthorizedException("invalid jwt token ");

                filterChain.doFilter(request , response);
                return ;

            }

            String token = auth_header.split("Bearer ")[1];

            UUID userId = jwt_service.decodeAccessToken(token);

            User authUser = user_repo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user not found"));

            log.info("this is the auth user {}" , authUser);

            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        authUser,
                        null,
                        authUser.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);

            }
            filterChain.doFilter(request, response);
        }
        catch (ExpiredJwtException ex){
            throw new customUnauthorizedException("jwt is expired");
        }
        catch ( JwtException ex){
            throw new customUnauthorizedException("Invalid jwt");
        }

        catch (Exception e) {
            handlerExceptionResolver.resolveException( request , response , null , e);
        }

        }

}
