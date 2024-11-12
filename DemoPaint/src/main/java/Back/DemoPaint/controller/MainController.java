package Back.DemoPaint.controller;

import Back.DemoPaint.service.StorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Setter
@Controller
@RequestMapping("/")
public class MainController {
    @Autowired
    private StorageService storageService;
    private AuthenticationManager authenticationManager;

    @GetMapping
    public String home() {
        return "home";
    }

    @GetMapping("/account")
    public String index() {
        return "account.html";
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request) {
        return "login";
    }

    @GetMapping("/registration")
    public String register() {
        return "registration";
    }

    @PostMapping("/createAccount")
    public String createAccount(@RequestParam("username") String username,
                                @RequestParam("password") String password,
                                @RequestParam("email") String email) {
        storageService.addUser(username, password, email);
        try {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "redirect:/account";
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            return "redirect:/registration";
        }
    }


    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

}
