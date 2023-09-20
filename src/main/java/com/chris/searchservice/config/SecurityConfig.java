package com.chris.searchservice.config;

import com.chris.common.config.jwt.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity(debug = false)
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Autowired
    private final JwtAuthenticationFilter jwtAuthFilter;
//    private final LogoutHandler logoutHandler;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
//                .httpBasic().disable()
                .csrf().disable()
                .cors().disable()
                .authorizeHttpRequests()
                .requestMatchers("/**").permitAll()
                .requestMatchers("/swagger*/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
//                .requestMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security", "/swagger-ui.html", "/webjars/**", "/swagger-resources/configuration/ui", "/swagger-ui.html").permitAll()
                .requestMatchers("/search/admin/**").hasAuthority("ADMIN")
                .requestMatchers(AUTH_WHITELIST).permitAll()
                .anyRequest().authenticated()
//                .anyRequest().permitAll()
                .and()
//                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint(((request, response, authException) -> {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
                }))
                .and()
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
//                .logout()
//                .logoutUrl("/api/v1/auth/logout")
//                .addLogoutHandler(logoutHandler)
//                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
        ;


        return http.build();
    }

    private static final String[] AUTH_WHITELIST = {
            "/api/v1/auth/**",
            "/v3/api-docs/**",
            "/v3/api-docs.yaml",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

}
