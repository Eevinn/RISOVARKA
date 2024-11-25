package Back.Paint.services;

import Back.Paint.domain.Person;
import Back.Paint.repo.PersonRepo;
import Back.Paint.security.PersonDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonDetailsService implements UserDetailsService {
    private final PersonRepo personRepo;

    public PersonDetailsService(PersonRepo personRepo) {
        this.personRepo = personRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> person = personRepo.findByUsername(username);
        if (person.isEmpty())
            throw new UsernameNotFoundException("Пользователь не найден");
        return new PersonDetails(person.get());
    }
}