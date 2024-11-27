package backend;

import backend.model.Person;
import backend.repo.PersonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class PaintApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaintApplication.class, args);
	}

	//admin
	@Bean
	public CommandLineRunner init(PersonRepo personRepo, @Autowired BCryptPasswordEncoder passwordEncoder) {
		return (args) -> {
			personRepo.save(new Person(1, "admin", passwordEncoder.encode("000"), "ROLE_ADMIN"));

		};
	}
}
