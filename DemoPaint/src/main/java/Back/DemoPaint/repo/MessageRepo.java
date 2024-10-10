package Back.DemoPaint.repo;

import Back.DemoPaint.domain.Message;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepo extends JpaRepository<Message, Long> {

}
