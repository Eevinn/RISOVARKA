package Back.Paint.repo;

import Back.Paint.domain.UserBoardRelationshipId;
import Back.Paint.domain.UserBoardRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBoardRelationshipRepo extends JpaRepository<UserBoardRelationship, UserBoardRelationshipId> {
}