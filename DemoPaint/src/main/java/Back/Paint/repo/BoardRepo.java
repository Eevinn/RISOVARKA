package Back.Paint.repo;

import Back.Paint.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepo extends JpaRepository<Board, Long> {

}