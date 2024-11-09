package Back.DemoPaint.storage;

import Back.DemoPaint.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserStorage extends CrudRepository<User, Integer> {

    @Query("SELECT u FROM User u WHERE u.role = :role")
    List<User> findAllByRole(String role);

    User findById(int id);
    User findByLogin(String login);
    int curUser = 1;
    boolean existsByLogin(String login);

}
