package dev.branches.infra.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import dev.branches.entity.User;
import dev.branches.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class UserAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = recoveryToken(request);

        if (token != null) {
            try {
                String subject = jwtTokenService.validateToken(token);

                User user = userRepository.findById(subject)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

                Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }  catch (JWTVerificationException exception) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token JWT inválido ou expirado.");

                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    public String recoveryToken(HttpServletRequest request) {
        String authentication = request.getHeader("Authorization");

        if (authentication != null) return authentication.replace("Bearer ", "");

        return null;
    }
}
