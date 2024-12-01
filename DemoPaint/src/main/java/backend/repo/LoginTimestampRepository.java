package backend.repo;

import backend.model.Person;
import backend.model.LoginTimestamp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoginTimestampRepository extends JpaRepository<LoginTimestamp, Long> {
    List<LoginTimestamp> findByPerson(Person person);
    List<LoginTimestamp> findAllByPerson(Person person);

    List<LoginTimestamp> findByPersonId(Long personId);

    LoginTimestamp findFirstByPersonAndLogoutTimeIsNullOrderByLoginTimeDesc(Person person);

}
