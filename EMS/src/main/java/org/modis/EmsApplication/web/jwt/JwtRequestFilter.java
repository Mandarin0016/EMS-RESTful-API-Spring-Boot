package org.modis.EmsApplication.web.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.modis.EmsApplication.dto.ErrorResponseDTO;
import org.modis.EmsApplication.model.User;
import org.modis.EmsApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
@Order
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        String email = null;
        String jwtToken = null;
        if (authorizationHeader != null) {
            if (authorizationHeader.startsWith("Bearer ")) {
                jwtToken = authorizationHeader.substring(7);
                try {
                    email = jwtUtils.getUsernameFromToken(jwtToken);
                } catch (IllegalArgumentException ex) {
                    ErrorResponseDTO errorResponse = new ErrorResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unable to get JWT token.");
                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    response.getWriter().write(convertObjectToJson(errorResponse));
                    log.error("Unable to get JWT token.");
                    return;
                } catch (ExpiredJwtException ex) {
                    ErrorResponseDTO errorResponse = new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), "JWT token has expired.");
                    response.setStatus(HttpStatus.BAD_REQUEST.value());
                    response.getWriter().write(convertObjectToJson(errorResponse));
                    log.error("JWT token has expired.");
                    return;
                }
            } else {
                ErrorResponseDTO errorResponse = new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), "JWT token does not begin with 'Bearer ' prefix.");
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.getWriter().write(convertObjectToJson(errorResponse));
                log.error("JWT token does not begin with 'Bearer ' prefix.");
                return;
            }
        }

        if (email != null) {
            User user = userService.getUserByEmail(email);
            if (jwtUtils.validateToken(jwtToken, user)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                throw new BadCredentialsException("JWT token has expired.");
            }
        }
        filterChain.doFilter(request, response);
    }

    public String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }
}
