package backend.controller;

import backend.model.LoginTimestamp;
import backend.model.Person;
import backend.repo.PersonRepo;
import backend.services.AdminService;
import backend.services.LoginTimestampService;
import backend.services.PersonService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import backend.repo.BoardRepo;
import backend.model.Board;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final PersonRepo personRepo;
    private final BoardRepo boardRepo;
    private PersonService personService;
    private final LoginTimestampService loginTimestampService;
    private final AdminService adminService;


    @Autowired
    public AdminController(BoardRepo boardRepo, PersonRepo personRepo, LoginTimestampService loginTimestampService, PersonService personService, AdminService adminService) {
        this.boardRepo = boardRepo;
        this.personRepo = personRepo;
        this.loginTimestampService = loginTimestampService;
        this.personService = personService;
        this.adminService = adminService;
    }

    @Autowired
    public void setStorageService(PersonService personService) {
        this.personService = personService;
    }

    //страница админа со статистикой
    @GetMapping("/adminPage")
    public String bookForm(Model model, Authentication authentication) {
        List<Person> allPerson = personRepo.findAll();
        long totalUsers = adminService.countRegisteredUsers();
        long totalBoards = adminService.countTotalBoards();
        long totalOnlineTime = adminService.calculateTotalOnlineTime();
        Person person = personService.findByUsername(authentication.getName()).orElse(null);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalBoards", totalBoards);
        String formattedTime = formatDuration(totalOnlineTime);
        model.addAttribute("totalOnlineTime", formattedTime);

        model.addAttribute("newUsersToday", adminService.countNewUsersToday(1));
        model.addAttribute("newUsersLastWeek", adminService.countNewUsersToday(7));
        model.addAttribute("newUsersLastMonth",  adminService.countNewUsersToday(30));

        model.addAttribute("activeUsersToday", adminService.countactiveUsersToday(1));
        model.addAttribute("activeUsersLastWeek", adminService.countactiveUsersToday(7));
        model.addAttribute("activeUsersCount", adminService.countActiveUsersNow());

        List<Map.Entry<Person, Long>> topUsersByOnlineTime = adminService.getTopUsersByOnlineTime(3);
        List<ActivityData> topUsers = topUsersByOnlineTime.stream()
                .map(entry -> new ActivityData(entry.getKey().getUsername(), entry.getValue()))
                .collect(Collectors.toList());

        model.addAttribute("topUsers", topUsers);

        return "admin";
    }

    //список пользователей
    @GetMapping("/users")
    public String listOfUsers(Authentication authentication, Model model) {
        List<Person> persons = personService.getAllUsers();
        model.addAttribute("persons", persons);
        model.addAttribute("bannedUsers", adminService.countBannedUsers());
        return "users";
    }

    //список и подсчет заблокированных пользователей
    @GetMapping("/block")
    public String listOfBlock(Authentication authentication, Model model) {
        List<Person> persons = adminService.getBannedUsers();
        model.addAttribute("persons", persons);
        model.addAttribute("bannedUsers", adminService.countBannedUsers());
        return "block";
    }

    //просмотр профиля пользователя(вместе с его досками)
    @GetMapping("/users/{id}")
    public String viewUserProfile(@PathVariable("id") int id, Model model, Authentication authentication) {
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

    //присвоение пользователю роли "забанен"
    @PostMapping("/users/{id}/ban")
    public String banUser(@PathVariable("id") int id) {
        Person user = personRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        user.setRole("ROLE_BANNED");
        personRepo.save(user);
        return "redirect:/admin/users";
    }

    //разблокировать пользователя
    @PostMapping("/users/{id}/unban")
    public String unbanUser(@PathVariable("id") int id) {
        Person user = personRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        user.setRole("ROLE_USER");
        personRepo.save(user);
        return "redirect:/admin/users";
    }

    @Data
    public static class ActivityData {
        private String username;
        private Long onlineTime;

        public ActivityData(String username, Long onlineTime) {
            this.username = username;
            this.onlineTime = onlineTime;
        }

    }

    //общее время онлайн
    @GetMapping("/totalOnlineTime")
    @ResponseBody
    public String getTotalOnlineTime() {
        long totalOnlineSeconds = adminService.calculateTotalOnlineTime();
        return formatDuration(totalOnlineSeconds);
    }

    //упорядоченный вывод времени
    private String formatDuration(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02dh:%02dm:%02ds", hours, minutes, seconds);
    }


}
