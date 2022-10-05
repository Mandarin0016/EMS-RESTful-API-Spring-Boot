package org.modis.EmsApplication.web.jwt;

import org.modis.EmsApplication.dto.ErrorResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "message: Unauthorized actions");
    }

    public ResponseEntity<ErrorResponseDTO> badCredentialsExceptionHandler(HttpServletRequest request, HttpServletResponse response, BadCredentialsException ex) throws IOException {
        return ResponseEntity.status(BAD_REQUEST).body(new ErrorResponseDTO(BAD_REQUEST.value(), ex.getMessage()));
    }
}
