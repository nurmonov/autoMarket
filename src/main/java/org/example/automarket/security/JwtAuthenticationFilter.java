// JwtAuthenticationFilter - NPE va boshqa xatolarni to'g'riladim
package org.example.automarket.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        log.debug("JWT Filter ishga tushdi: {}", request.getRequestURI());

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("Authorization header yo'q yoki Bearer emas");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        log.debug("Token olingan: {}", token);

        String username = null;
        try {
            username = jwtUtil.extractUsername(token);
            log.debug("Extracted username: {}", username);
        } catch (SignatureException e) {
            log.error("JWT imzo mos kelmaydi: {}", e.getMessage());
            response.setStatus(401);
            response.getWriter().write("Token imzosi noto'g'ri");
            return;  // 🔥 Requestni shu yerda tugatamiz
        } catch (ExpiredJwtException e) {
            log.error("JWT eskirgan: {}", e.getMessage());
            response.setStatus(401);
            response.getWriter().write("Token muddati tugagan");
            return;
        } catch (MalformedJwtException e) {
            log.error("JWT formati noto'g'ri: {}", e.getMessage());
            response.setStatus(401);
            response.getWriter().write("Token formati buzilgan");
            return;
        } catch (Exception e) {
            log.error("JWT filterda umumiy xato: {}", e.getMessage(), e);
            response.setStatus(401);
            response.getWriter().write("Token tekshirishda xato");
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            log.debug("UserDetails yuklandi: {}", userDetails.getUsername());

            if (jwtUtil.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("Authentication muvaffaqiyatli set qilindi: {}", username);
            } else {
                log.warn("Token valid emas");
                response.setStatus(401);
                response.getWriter().write("Token valid emas");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/swagger-ui/")
                || path.startsWith("/v3/api-docs/")
                || path.startsWith("/api/auth/")
                || "OPTIONS".equals(request.getMethod());
    }
}