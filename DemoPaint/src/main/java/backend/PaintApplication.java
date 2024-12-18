package backend;

import backend.model.Person;
import backend.repo.PersonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@SpringBootApplication
public class PaintApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaintApplication.class, args);
	}

//	@Bean
//	public CommandLineRunner init(PersonRepo personRepo, @Autowired BCryptPasswordEncoder passwordEncoder) {
//		return (args) -> {
//			String username = "admin";
//			String password = "000";
//			String encodedPassword = passwordEncoder.encode(password);
//			Person admin = new Person();
//			admin.setUsername(username);
//			admin.setPassword(encodedPassword);
//			admin.setRole("ROLE_ADMIN");
//			admin.setCreatedAt(LocalDateTime.now());
//			personRepo.save(admin);
//		};
//	}

}

