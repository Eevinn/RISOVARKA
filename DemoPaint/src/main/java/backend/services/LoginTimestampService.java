package backend.services;

import backend.model.Person;
import backend.model.LoginTimestamp;
import backend.repo.LoginTimestampRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LoginTimestampService {

    private final LoginTimestampRepository loginTimestampRepository;


    public List<LoginTimestamp> findAllByPerson(Person person) {
        return loginTimestampRepository.findAllByPerson(person);
    }

    @Autowired
    public LoginTimestampService(LoginTimestampRepository loginTimestampRepository) {
        this.loginTimestampRepository = loginTimestampRepository;
    }

}
