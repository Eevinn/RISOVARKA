package backend.controller;

import backend.model.Board;
import backend.model.LoginTimestamp;
import backend.model.Person;
import backend.repo.BoardRepo;
import backend.repo.LoginTimestampRepository;
import backend.services.PersonService;
import backend.util.PersonValidator;
import jakarta.validation.Valid;
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
    private final LoginTimestampRepository loginTimestampRepository;

    public MainController(PersonService personService,
                          PersonValidator personValidator,
                          BoardRepo boardRepo,
                          LoginTimestampRepository loginTimestampRepository) {
        this.personService = personService;
        this.personValidator = personValidator;
        this.boardRepo = boardRepo;
        this.loginTimestampRepository = loginTimestampRepository;
    }

    /**
     * Главная страница. Если пользователь аутентифицирован, добавляем информацию о нем в модель.
     */
    @GetMapping("/")
    public String homePage(Model model, Authentication authentication) {
        if (authentication != null) {
            personService.getPerson(authentication.getName()).ifPresent(person -> {
                model.addAttribute("person_id", person.getId());
                model.addAttribute("username", person.getUsername());
            });
        }
        return "home";
    }

    /**
     * Страница логина.
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "loginAndRegistration";
    }

    /**
     * Обработка логина. Можно добавить дополнительную логику, например логирование времени входа.
     */
    @PostMapping("/process_login")
    public String processLogin(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            // Можно добавить логику сохранения логина или статистику
            // Person client = personService.findByUsername(authentication.getName()).orElse(null);
        }
        return "loginAndRegistration";
    }

    /**
     * Страница регистрации.
     */
    @GetMapping("/registration")
    public String registration(@ModelAttribute Person person) {
        return "registrationPage";
    }

    /**
     * Обработка регистрации.
     */
    @PostMapping("/process_registration")
    public String registrationPerson(@Valid @ModelAttribute Person person,
                                     BindingResult bindingResult) {
        personValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors()) {
            return "registrationPage";
        }

        personService.save(person);
        return "redirect:/login?registration";
    }

    /**
     * Создание новой доски для аутентифицированного пользователя.
     */
    @PostMapping("/create-board")
    public String createBoard(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        Optional<Person> optionalPerson = personService.getPerson(username);

        if (optionalPerson.isEmpty()) {
            // Если пользователь не найден, можно вернуть ошибку или редирект
            return "redirect:/login";
        }

        Person person = optionalPerson.get();
        Board board = new Board();
        board.setName("Новая доска");
        board.setText("{}"); // Инициализируем пустым JSON
        board.setUser(person);

        Board savedBoard = boardRepo.save(board);

        // Тут локально захардкожен адрес "http://localhost:5173/", возможно стоит вынести в настройки
        return "redirect:http://localhost:5173/" + savedBoard.getId();
    }
}
