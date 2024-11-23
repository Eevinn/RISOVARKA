package Back.Paint.repo;

import Back.Paint.domain.UserBoardRelationship;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UBRepo extends JpaRepository<UserBoardRelationship, Long> {

}
