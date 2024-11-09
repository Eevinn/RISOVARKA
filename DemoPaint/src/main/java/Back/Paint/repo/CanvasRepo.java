package Back.Paint.repo;

import Back.Paint.domain.Canvas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CanvasRepo extends MongoRepository<Canvas, String> {
}
