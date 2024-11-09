package Back.DemoPaint.config;

import Back.DemoPaint.storage.UserStorage;
import Back.DemoPaint.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Collection;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserStorage userStorage;

    private static class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

        public CustomAuthenticationSuccessHandler() {
            super();
        }

        @Override
        protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals("ROLE_ADMIN")) {
                    return "/employee/add"; // Перенаправление на страницу add для админа
                } else if (authority.getAuthority().equals("ROLE_USER")) {
                    return "/index"; // Перенаправление на страницу index для пользователя
                }
            }
            return "/"; // По умолчанию, если роль не найдена
        }
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/login", "/registration", "/postgres-console/**", "/createAccount").permitAll()
                        .requestMatchers("/", "/index").hasAnyRole("USER") // Доступ для USER и ADMIN
                        .requestMatchers("/employee/**").hasRole("ADMIN") // Доступ только для ADMIN
                        .anyRequest().authenticated()
                )

                .formLogin(login -> login
                        .loginPage("/login") // Страница входа
                        .loginProcessingUrl("/login") // URL для обработки входа
                        .successHandler(new CustomAuthenticationSuccessHandler()) // Обработчик успешного входа
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                        .invalidateHttpSession(true)
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .authenticationProvider(authenticationProvider())
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                User user = userStorage.findByLogin(username);
                if (user == null) {
                    throw new UsernameNotFoundException("Пользователь не найден");
                }
                return user;
            }
        };
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}