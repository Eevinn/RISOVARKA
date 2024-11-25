package Back.Paint.controller;

import Back.Paint.domain.Person;
import Back.Paint.repo.PersonRepo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final PersonRepo personRepo;

    @Autowired
    public UserController(PersonRepo personRepo) {
        this.personRepo = personRepo;
    }

    // Метод для получения всех пользователей
    @GetMapping
    public List<Person> getAllUsers() {
        return personRepo.findAll();
    }

    // Метод для получения пользователя по ID
    @GetMapping("/{id}")
    public Person getUserById(@PathVariable int id) {
        return personRepo.findById(id).orElse(null);
    }

    // Метод для создания нового пользователя
    @PostMapping
    public Person createUser(@RequestBody Person person) {
        return personRepo.save(person);
    }

    // Метод для обновления пользователя
    @PutMapping("/{id}")
    public Person updateUser(
            @PathVariable("id") int id,
            @RequestBody Person updatedPerson
    ) {
        Person personFromDb = personRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        BeanUtils.copyProperties(updatedPerson, personFromDb, "id", "creationDate");
        return personRepo.save(personFromDb);
    }

    // Метод для удаления пользователя
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        personRepo.deleteById(id);
    }
}
