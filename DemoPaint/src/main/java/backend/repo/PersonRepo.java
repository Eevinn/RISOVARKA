package backend.repo;

import backend.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepo extends JpaRepository<Person, Integer> {
    Optional<Person> findByUsername(String username);


    @Query("SELECT u FROM Person u WHERE u.role = :role")
    List<Person> findAllByRole(String role); //список всех пользователей
}