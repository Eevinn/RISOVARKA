package backend.config;

import backend.model.Person;
import backend.services.PersonService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;


@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private PersonService personService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String referer = request.getParameter("referer");
        Person person = personService.getPerson(authentication.getName()).orElse(null);
        if (person == null) {
            response.sendRedirect("/login?error");
            return;
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        if (roles.contains("ROLE_ADMIN")) {
            response.sendRedirect("/employee/admin");
        } else if (roles.contains("ROLE_USER")) {
            response.sendRedirect("/account");
        } else {
            response.sendRedirect("/login?error");
        }
    }
}

