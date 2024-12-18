package backend.config;

import backend.model.LoginTimestamp;
import backend.model.Person;
import backend.repo.LoginTimestampRepository;
import backend.repo.PersonRepo;
import backend.security.PersonDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;


@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final PersonRepo personRepo;
    private final LoginTimestampRepository loginTimestampRepository;
    @Autowired
    public CustomAuthenticationSuccessHandler(PersonRepo personRepo, LoginTimestampRepository loginTimestampRepository) {
        this.personRepo = personRepo;
        this.loginTimestampRepository = loginTimestampRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Person person = personDetails.getPerson();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        String username = authentication.getName();
        Person user = personRepo.findByUsername(username).orElse(null);
        if (user != null) {
            LoginTimestamp loginTimestamp = new LoginTimestamp();
            loginTimestamp.setPerson(user);
            loginTimestamp.setLoginTime(LocalDateTime.now());
            loginTimestampRepository.save(loginTimestamp);
            System.out.println("LoginTimestamp saved for user: " + username);
        }

        if (roles.contains("ROLE_BANNED")) {
            response.sendRedirect("/banned");
        } else if (roles.contains("ROLE_ADMIN")) {
            response.sendRedirect("/admin/adminPage");
        } else if (roles.contains("ROLE_USER")) {
            response.sendRedirect("/account");
        } else {
            response.sendRedirect("/login?error");
        }
    }
}
