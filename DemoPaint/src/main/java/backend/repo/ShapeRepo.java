package backend.repo;

import backend.model.Board;
import backend.model.Shape;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShapeRepo extends JpaRepository<Shape, Integer> {
    List<Shape> findAllByBoard(Board board);
}