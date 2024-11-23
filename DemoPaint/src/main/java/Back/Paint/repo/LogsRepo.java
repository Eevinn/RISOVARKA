package Back.Paint.repo;

import Back.Paint.domain.Logs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogsRepo extends JpaRepository<Logs, Long> {

}