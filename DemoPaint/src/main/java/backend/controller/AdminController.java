package backend.controller;

import backend.model.LoginTimestamp;
import backend.model.Person;
import backend.repo.PersonRepo;
import backend.services.LoginTimestampService;
import backend.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import backend.repo.BoardRepo;
import backend.model.Board;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final PersonRepo personRepo;
    private final BoardRepo boardRepo;
    private PersonService personService;
    private final LoginTimestampService loginTimestampService;


    @Autowired
    public AdminController(BoardRepo boardRepo, PersonRepo personRepo, LoginTimestampService loginTimestampService, PersonService personService) {
        this.boardRepo = boardRepo;
        this.personRepo = personRepo;
        this.loginTimestampService = loginTimestampService;
        this.personService = personService;
    }

    @Autowired
    public void setStorageService(PersonService personService) {
        this.personService = personService;
    }


    @GetMapping("/adminPage")
    public String bookForm(@ModelAttribute Person person) {
        return "admin";
    }

    @GetMapping("/users")
    public String listOfUsers(Authentication authentication, Model model) {
        List<Person> persons = personService.getAllUsers();
        model.addAttribute("persons", persons);
        return "users";
    }

    @GetMapping("/users/{id}")  //просмотр профиля пользователя(вместе с его досками)
    public String viewUserProfile(@PathVariable("id") Long id, Model model, Authentication authentication) {
        Person user = personRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        List<Board> boards = boardRepo.findAllByUser(user);
        model.addAttribute("user", user);
        model.addAttribute("boards", boards);

        List<LoginTimestamp> loginTimestamps = loginTimestampService.findAllByPerson(user);
        model.addAttribute("loginTimestamps", loginTimestamps);
        Person person = personService.findByUsername(authentication.getName()).orElse(null);
        model.addAttribute("person", person);
        return "profile";
    }

    @PostMapping("/users/{id}/ban")
    public String banUser(@PathVariable("id") Long id) {
        Person user = personRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        user.setRole("ROLE_BANNED");
        personRepo.save(user);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/unban")
    public String unbanUser(@PathVariable("id") Long id) {
        Person user = personRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        user.setRole("ROLE_USER");
        personRepo.save(user);
        return "redirect:/admin/users";
    }
}
