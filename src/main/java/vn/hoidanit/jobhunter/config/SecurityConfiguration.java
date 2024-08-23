package vn.hoidanit.jobhunter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http,
                        CustomAuthenticationEntryPoint customAuthenticationEntryPoint) throws Exception {

                String[] whileList = {
                                "/", "/api/v1/auth/login",
                                "/api/v1/auth/refresh", "/storage/**", "/api/v1/companies/**", "/api/v1/jobs/**"
                };
                http
                                .csrf(c -> c.disable())
                                .cors(Customizer.withDefaults())
                                .authorizeHttpRequests(
                                                authz -> authz
                                                                .requestMatchers(whileList)
                                                                .permitAll()
                                                                .anyRequest().authenticated())
                                .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults())
                                                // config for handle exception
                                                .authenticationEntryPoint(customAuthenticationEntryPoint))
                                // .exceptionHandling(
                                // exceptions -> exceptions
                                // .authenticationEntryPoint(customAuthenticationEntryPoint) // 401
                                // .accessDeniedHandler(new BearerTokenAccessDeniedHandler())) // 403
                                // disable login
                                .formLogin(f -> f.disable())
                                // config for spring rest stateful -> stateless
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
                return http.build();
        }

}
