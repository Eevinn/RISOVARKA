package backend.config;

import backend.model.LoginTimestamp;
import backend.model.Person;
import backend.repo.LoginTimestampRepository;
import backend.repo.PersonRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final LoginTimestampRepository loginTimestampRepository;
    private final PersonRepo personRepo;

    @Autowired
    public CustomLogoutSuccessHandler(LoginTimestampRepository loginTimestampRepository, PersonRepo personRepo) {
        this.loginTimestampRepository = loginTimestampRepository;
        this.personRepo = personRepo;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        if (authentication != null) {
            String username = authentication.getName();
            Person user = personRepo.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            LoginTimestamp lastLogin = loginTimestampRepository.findFirstByPersonAndLogoutTimeIsNullOrderByLoginTimeDesc(user);
            if (lastLogin != null) {
                lastLogin.setLogoutTime(LocalDateTime.now());
                loginTimestampRepository.save(lastLogin);

                System.out.println("LogoutTimestamp saved for user: " + username);
            }
        }
        response.sendRedirect("/");
    }
}
