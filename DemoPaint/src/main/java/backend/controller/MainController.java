package backend.controller;

import backend.model.Board;
import backend.repo.BoardRepo;
import backend.services.PersonService;
import backend.model.Person;
import backend.util.PersonValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class MainController {
    private final PersonService personService;
    private final PersonValidator personValidator;
    private final BoardRepo boardRepo;

    @Autowired
    public MainController(PersonService personService, PersonValidator personValidator, BoardRepo boardRepo) {
        this.personService = personService;
        this.personValidator = personValidator;
        this.boardRepo = boardRepo;
    }

    @GetMapping("/")
    public String homePage(Model model, Authentication authentication) {
        if (authentication != null) {
            Person person = personService.getPerson(authentication.getName()).orElse(null);
            if (person != null) {
                model.addAttribute("person_id", person.getId());
                model.addAttribute("username", person.getUsername());
            }
        }
        return "home";
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

    @PostMapping("/create-board")
    public String createBoard(Authentication authentication) {
        String username = authentication.getName();
        Optional<Person> optionalPerson = personService.getPerson(username);
        Person person = optionalPerson.get();
        Board board = new Board();
        board.setName("Новая доска");
        board.setText("{}"); // Инициализируем пустым JSON
        board.setUser(person);
        Board savedBoard = boardRepo.save(board);
        return "redirect:http://localhost:5173/" + savedBoard.getId();
    }
}
