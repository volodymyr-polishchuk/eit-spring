package com.volodymyrpo.eit.security;

import com.volodymyrpo.eit.student.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)  // Replaces @EnableGlobalMethodSecurity
public class WebSecurityConfig {

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.route.authentication.path}")
    private String authenticationPath;

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final StudentService studentService;
    private final JwtAuthorizationTokenFilter jwtAuthorizationTokenFilter;

    @Autowired
    public WebSecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                             StudentService studentService,
                             JwtAuthorizationTokenFilter jwtAuthorizationTokenFilter) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.studentService = studentService;
        this.jwtAuthorizationTokenFilter = jwtAuthorizationTokenFilter;
    }

    /**
     * In Spring Security 6, we define the filter chain via a bean
     * instead of overriding configure(HttpSecurity).
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // If you want CORS enabled:
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                // Use your custom AuthenticationEntryPoint:
                .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                // Stateless session policy:
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Request authorization:
                .authorizeHttpRequests(auth -> auth
                        // Any endpoints you want to permit:
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, authenticationPath).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/",
                                "/*.html",
                                "/favicon.ico",
                                "/**/*.html",
                                "/**/*.css",
                                "/**/*.js").permitAll()
                        // Then secure everything else:
                        .anyRequest().authenticated()
                );

        // Add the JWT filter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthorizationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        // Optionally configure headers:
        http.headers(headers -> {
            headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin); // if you need H2 console frames, for example
            headers.cacheControl(Customizer.withDefaults());   // disable caching
        });

        return http.build();
    }

    /**
     * Create an AuthenticationManager bean the "new" way:
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        // Configure the builder with our StudentService:
        AuthenticationManagerBuilder builder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        builder
                .userDetailsService(studentService);
        // .passwordEncoder(...); // if you need a PasswordEncoder

        // Return the fully built authentication manager
        return builder.build();
    }
}
