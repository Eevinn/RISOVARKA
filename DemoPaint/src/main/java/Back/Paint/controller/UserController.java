package Back.Paint.controller;

import Back.Paint.domain.User;
import Back.Paint.repo.UserRepo;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserRepo userRepo;

    @Autowired
    public UserController(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    // Метод для получения всех пользователей
    @GetMapping
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    // Метод для получения пользователя по ID
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userRepo.findById(id).orElse(null);
    }

    // Метод для создания нового пользователя
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepo.save(user);
    }

    // Метод для обновления пользователя
    @PutMapping("/{id}")
    public User updateUser(
            @PathVariable("id") Long id,
            @RequestBody User updatedUser
    ) {
        User userFromDb = userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        BeanUtils.copyProperties(updatedUser, userFromDb, "id", "creationDate");
        return userRepo.save(userFromDb);
    }

    // Метод для удаления пользователя
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userRepo.deleteById(id);
    }
}
