package br.com.raizes_do_nordeste.infra.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityBeansConfig {

    private final AuthService authService;
    private final SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Auth pública
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        // Swagger
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()

                        // Bootstrap e auto-cadastro
                        .requestMatchers(HttpMethod.POST, "/bootstrap/gerente").permitAll()
                        .requestMatchers(HttpMethod.POST, "/unidades/cadastro").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuarios/clientes/cadastro").permitAll()
                        .requestMatchers(HttpMethod.GET, "/usuarios/clientes/me/pontos").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/usuarios/clientes/me/pontos/extrato").hasRole("CLIENTE")

                        // Gestão de equipe/produto/estoque por gerente
                        .requestMatchers(HttpMethod.POST, "/usuarios/equipes/cadastro").hasRole("GERENTE")
                        .requestMatchers(HttpMethod.POST, "/produtos/cadastro").hasRole("GERENTE")
                        .requestMatchers(HttpMethod.POST, "/estoques/cadastro").hasRole("GERENTE")
                        .requestMatchers(HttpMethod.POST, "/totens/cadastro").hasRole("GERENTE")

                        // Operação de pedidos e pagamentos (interno + cliente)
                        .requestMatchers(HttpMethod.POST, "/pedidos").hasAnyRole("GERENTE", "ATENDENTE", "CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/pedidos/meus-produtos").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/pedidos/**").hasAnyRole("GERENTE", "ATENDENTE")
                        .requestMatchers("/pagamentos/**").hasAnyRole("GERENTE", "ATENDENTE", "CLIENTE")
                        .requestMatchers("/totens/**").hasAnyRole("GERENTE", "ATENDENTE", "CLIENTE")

                        // Alteração de senha para qualquer usuário autenticado
                        .requestMatchers(HttpMethod.PATCH, "/auth/password").authenticated()

                        // Demais rotas exigem autenticação
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(authService);
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setHideUserNotFoundExceptions(false);
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
