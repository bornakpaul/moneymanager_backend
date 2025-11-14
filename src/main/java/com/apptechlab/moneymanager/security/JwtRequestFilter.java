package com.apptechlab.moneymanager.security;

import com.apptechlab.moneymanager.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
          final String authHeader = request.getHeader("Authorization");
          String email = null;
          String jwt = null;
          if(authHeader != null && authHeader.startsWith("Bearer ")){
              jwt = authHeader.substring(7);
              try {
                  email = jwtUtil.extractUsername(jwt);
              }catch (ExpiredJwtException e){
                  response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                  response.setContentType("application/json");
                  String msg = "{\"error\":\"Token expired. Refresh the token to proceed further.\"}";
                  response.getOutputStream().write(msg.getBytes(StandardCharsets.UTF_8));
                  response.getOutputStream().flush();
                  return;
              } catch (Exception e) {
                  response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                  response.setContentType("application/json");
                  String msg = "{\"error\":\"Invalid authentication token.\"}";
                  response.getOutputStream().write(msg.getBytes(StandardCharsets.UTF_8));
                  response.getOutputStream().flush();
                  return;
              }
          }
          if(email != null && SecurityContextHolder.getContext().getAuthentication() == null){
             UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
             if(jwtUtil.isTokenValid(jwt,email)){
                 UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                         userDetails, null, userDetails.getAuthorities()
                 );
                 authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                 SecurityContextHolder.getContext().setAuthentication(authToken);
             }
          }
          filterChain.doFilter(request, response);
    }
}
