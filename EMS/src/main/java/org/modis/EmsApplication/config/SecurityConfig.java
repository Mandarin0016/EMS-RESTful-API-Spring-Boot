package org.modis.EmsApplication.config;

import org.modis.EmsApplication.exception.NonexistingEntityException;
import org.modis.EmsApplication.service.UserService;
import org.modis.EmsApplication.web.jwt.JwtAuthenticationEntryPoint;
import org.modis.EmsApplication.web.jwt.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.modis.EmsApplication.model.enums.Role.*;
import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Autowired
    JwtRequestFilter jwtRequestFilter;

    @Value("${environments.init-data.value}")
    public Boolean _initDB;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        if (_initDB) {
            http.csrf().disable().authorizeRequests().mvcMatchers("/**").permitAll();
            return http.build();
        }
        http.csrf().disable().authorizeRequests()
                .mvcMatchers(POST, "/api/auth/login").permitAll()
                .mvcMatchers(POST, "/api/auth/resetPassword").permitAll()
                .mvcMatchers(GET, "/api/users", "/api/users/id", "/api/users/count").hasAnyRole(PARENT.name(), STUDENT.name(), HEADMASTER.name(), TEACHER.name())
                .mvcMatchers(POST, "/api/users").hasAnyRole(HEADMASTER.name())
                .mvcMatchers(PUT, "/api/users", "/api/users/**", "/api/users/id").hasAnyRole(PARENT.name(), STUDENT.name(), HEADMASTER.name(), TEACHER.name())
                .mvcMatchers(DELETE, "/api/users/id").hasAnyRole(HEADMASTER.name())
                .mvcMatchers(GET, "/api/timetables", "/api/timetables/id", "/api/timetables/count").hasAnyRole(PARENT.name(), STUDENT.name(), HEADMASTER.name(), TEACHER.name())
                .mvcMatchers(POST, "/api/timetables").hasAnyRole(HEADMASTER.name(), TEACHER.name())
                .mvcMatchers(PUT, "/api/timetables/id").hasAnyRole(HEADMASTER.name(), TEACHER.name())
                .mvcMatchers(DELETE, "/api/timetables/id").hasAnyRole(HEADMASTER.name(), TEACHER.name())
                .mvcMatchers("/api/teachers/**").hasAnyRole(TEACHER.name(), HEADMASTER.name())
                .mvcMatchers(GET, "/api/students/**").hasAnyRole(TEACHER.name(), HEADMASTER.name(), STUDENT.name(), PARENT.name())
                .mvcMatchers(PUT, "/api/students/id/grades").hasAnyRole(TEACHER.name(), HEADMASTER.name())
                .mvcMatchers(DELETE, "/api/students/id/grades").hasAnyRole(TEACHER.name(), HEADMASTER.name())
                .mvcMatchers("/api/schoolyears/**").hasAnyRole(HEADMASTER.name(), TEACHER.name())
                .mvcMatchers("/api/parents/**").hasAnyRole(TEACHER.name(), HEADMASTER.name())
                .mvcMatchers(GET, "/api/homework", "/api/homework/count", "/api/homework/id", "/api/homework/**").hasAnyRole(STUDENT.name(), PARENT.name(), TEACHER.name(), HEADMASTER.name())
                .mvcMatchers(POST, "/api/homework/id/results/id/results/studentId").hasAnyRole(TEACHER.name(), HEADMASTER.name())
                .mvcMatchers(POST, "/api/homework/id/results").hasAnyRole(TEACHER.name(), HEADMASTER.name(), STUDENT.name())
                .mvcMatchers(PUT, "/api/homework/id").hasAnyRole(TEACHER.name(), HEADMASTER.name())
                .mvcMatchers(POST, "/api/homework").hasAnyRole(TEACHER.name(), HEADMASTER.name())
                .mvcMatchers(GET, "/api/exams/id/performers").hasAnyRole(HEADMASTER.name(), TEACHER.name())
                .mvcMatchers(GET, "/api/exams", "/api/exams/id", "/api/exams/id/results", "/api/exams/count").hasAnyRole(HEADMASTER.name(), TEACHER.name(), STUDENT.name(), PARENT.name())
                .mvcMatchers(POST, "/api/exams/id/performers").hasAnyRole(HEADMASTER.name(), TEACHER.name(), STUDENT.name())
                .mvcMatchers(POST, "/api/exams", "/api/exams/**", "/api/exams/id/results").hasAnyRole(HEADMASTER.name(), TEACHER.name())
                .mvcMatchers(DELETE, "/api/exams", "/api/exams/**", "/api/exams/id", "/api/exams/id/results/studentId").hasAnyRole(HEADMASTER.name(), TEACHER.name())
                .mvcMatchers(PUT, "/api/exams/id/results/studentId").hasAnyRole(HEADMASTER.name(), TEACHER.name())
                .mvcMatchers(GET, "/api/competitions", "/api/competitions/id", "/api/competitions/id/certificate", "/api/competitions/id/registered", "/api/competitions/id/results", "/api/competitions/id/winner", "/api/competitions/count").hasAnyRole(HEADMASTER.name(), TEACHER.name(), STUDENT.name(), PARENT.name())
                .mvcMatchers(POST, "/api/competitions/performers").hasAnyRole(HEADMASTER.name(), STUDENT.name())
                .mvcMatchers(POST, "/api/competitions", "/api/competitions/**").hasAnyRole(HEADMASTER.name())
                .mvcMatchers(PUT, "/api/competitions", "/api/competitions/**").hasAnyRole(HEADMASTER.name())
                .mvcMatchers(DELETE, "/api/competitions", "/api/competitions/**").hasAnyRole(HEADMASTER.name())
                .mvcMatchers(GET, "/api/certificates", "/api/certificates/**").hasAnyRole(PARENT.name(), STUDENT.name(), HEADMASTER.name(), TEACHER.name())
                .mvcMatchers(POST, "/api/certificates", "/api/certificates/id/owner").hasAnyRole(HEADMASTER.name(), TEACHER.name())
                .mvcMatchers(PUT, "/api/certificates/id").hasAnyRole(HEADMASTER.name(), TEACHER.name())
                .mvcMatchers(DELETE, "/api/certificates/**", "/api/certificates/id", "/api/certificates/id/owner").hasAnyRole(HEADMASTER.name(), TEACHER.name())
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint);

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http, UserDetailsService userDetailsService) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).userDetailsService(userDetailsService).passwordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder()).and().build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserService userService) {
        return (String email) -> {
            try {
                return userService.getUserByEmail(email);
            } catch (NonexistingEntityException ex) {
                throw new NonexistingEntityException(ex.getMessage());
            }
        };
    }

    public boolean isInInitDBState() {
        return _initDB;
    }

}