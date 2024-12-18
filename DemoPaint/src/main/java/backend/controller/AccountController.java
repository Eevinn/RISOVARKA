package backend.controller;

import backend.model.Board;
import backend.model.Person;
import backend.repo.BoardRepo;
import backend.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class AccountController {

    @Autowired
    private BoardRepo boardRepo;

    @Autowired
    private PersonService personService;

    @GetMapping("/account")
    public String getAccountPage(Authentication authentication, Model model) {
        String username = authentication.getName();
        Optional<Person> optionalPerson = personService.getPerson(username);
        if (optionalPerson.isEmpty()) {
            return "redirect:/login";
        }
        Person person = optionalPerson.get();
        List<Board> boards = boardRepo.findAllByUser(person);
        model.addAttribute("boards", boards);
        model.addAttribute("person", person);
        return "account"; // Возвращает Thymeleaf шаблон account.html
    }

    @GetMapping("/banned")
    public String getBannedPage(Authentication authentication, Model model) {
        String username = authentication.getName();
        Optional<Person> optionalPerson = personService.getPerson(username);
        if (optionalPerson.isEmpty()) {
            return "redirect:/login";
        }
        return "banned";
    }

}
