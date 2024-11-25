package backend.repo;

import backend.model.Board;
import backend.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepo extends JpaRepository<Board, Integer> {
    List<Board> findAllByUser(Person user);
}