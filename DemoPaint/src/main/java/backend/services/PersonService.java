package backend.services;

import backend.model.Person;
import backend.repo.LoginTimestampRepository;
import backend.repo.PersonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PersonService {
    private final PersonRepo personRepo;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private LoginTimestampRepository loginTimestampRepository;

    @Autowired
    public PersonService(PersonRepo personRepo, PasswordEncoder passwordEncoder) {
        this.personRepo = personRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public Person findOne(Long id) {
        Optional<Person> foundClient = personRepo.findById(id);
        return foundClient.orElse(null);
    }

    public Optional<Person> getPerson(String username) {
        return personRepo.findByUsername(username);
    }

    public Optional<Person> findByUsername(String username) {
        return personRepo.findByUsername(username);
    }

    @Transactional
    public void save(Person person){
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        personRepo.save(person);
    }

    public List<Person> getAllUsers() {
        return personRepo.findAllUsers();
    }

}
