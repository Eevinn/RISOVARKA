package Back.Paint.repo;

import Back.Paint.domain.Message;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepo extends JpaRepository<Message, Long> {

}
