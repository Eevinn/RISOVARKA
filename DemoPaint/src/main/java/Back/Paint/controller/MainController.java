package Back.Paint.controller;

import Back.Paint.services.PersonService;
import Back.Paint.domain.Person;
import Back.Paint.util.PersonValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class MainController {
    private final PersonService personService;
    private final PersonValidator personValidator;

    @Autowired
    public MainController(PersonService personService, PersonValidator personValidator) {
        this.personService = personService;
        this.personValidator = personValidator;
    }

    @GetMapping("/")
    public String homePage(Model model, Authentication authentication) {
        if (authentication != null) {
            model.addAttribute("person_id", personService.getPerson(authentication.getName()).get().getId());
        }
        return "home";
    }
    @GetMapping("/account")
    public String showCatalog(Model model,  Authentication authentication) {
        if (authentication != null) {
            model.addAttribute("person_id", personService.getPerson(authentication.getName()).get().getId());
        }
        return "account";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "loginAndRegistration";
    }

    @GetMapping("/registration")
    public String registration(@ModelAttribute Person person){
        return "registrationPage";
    }

    @PostMapping("/process_registration")
    public String registrationPerson(@Valid @ModelAttribute Person person, BindingResult bindingResult) {
        personValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors())
            return "registrationPage";

        personService.save(person);
        return "redirect:/login?registration";
    }

}
