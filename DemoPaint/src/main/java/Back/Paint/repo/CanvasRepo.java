package Back.Paint.repo;

import Back.Paint.domain.Canvas;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CanvasRepo extends JpaRepository<Canvas, String> {
}
