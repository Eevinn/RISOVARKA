package backend.config;

import backend.services.PersonDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import backend.config.CustomLogoutSuccessHandler;
import backend.config.CustomAuthenticationSuccessHandler;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private static final String[] AUTH_WHITELIST = {
            "/", "/login", "/registration", "/process_registration"
    };
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final PersonDetailsService personDetailsService;

    @Autowired
    public SecurityConfiguration(CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler, CustomLogoutSuccessHandler customLogoutSuccessHandler, PersonDetailsService personDetailsService) {
        this.customLogoutSuccessHandler = customLogoutSuccessHandler;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
        this.personDetailsService = personDetailsService;
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(personDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/account").hasRole("USER")
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        .requestMatchers("/banned").hasAuthority("ROLE_BANNED")
                        .anyRequest().hasAnyRole("USER", "ADMIN")
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/process_login")
                        .successHandler(customAuthenticationSuccessHandler)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/?logout=true")
                        .logoutSuccessHandler(customLogoutSuccessHandler)
                        .permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}