package Back.Paint.controller;

import Back.Paint.domain.Person;
import Back.Paint.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/employee")
public class AdminController {
    private PersonService personService;

    @Autowired
    public void setStorageService(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/admin")
    public String bookForm(@ModelAttribute Person person) {
        return "admin";
    }

    @GetMapping("/users")
    public String listOfUsers(@ModelAttribute Person person, Model model) {
        List<Person> persons = personService.getAllUsers();
        model.addAttribute("persons", persons);
        return "users";
    }
}