package backend.repo;

import backend.model.Person;
import backend.model.LoginTimestamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LoginTimestampRepository extends JpaRepository<LoginTimestamp, Long> {
    List<LoginTimestamp> findByPerson(Person person);

    List<LoginTimestamp> findAllByPerson(Person person);

    List<LoginTimestamp> findByPersonId(Long personId);

    LoginTimestamp findFirstByPersonAndLogoutTimeIsNullOrderByLoginTimeDesc(Person person);

    @Query("SELECT COUNT(lt) FROM LoginTimestamp lt WHERE lt.logoutTime IS NULL AND lt.person.role != 'ROLE_ADMIN'")
    long countByLogoutTimeIsNullAndPersonRoleNot(String role);

}
