package backend;

import Back.DemoPaint.storage.UserStorage;
import Back.DemoPaint.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class PaintApplication {

	@Bean(name = "storageServicePasswordEncoder")
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	public static void main(String[] args) {
		SpringApplication.run(PaintApplication.class, args);
	}

	@Bean
	public CommandLineRunner init(UserStorage userStorage, @Autowired BCryptPasswordEncoder passwordEncoder) {
		return (args) -> {
			userStorage.save(new User(null, "admin", passwordEncoder.encode("000"), "admin@nomiro.ru", "ROLE_ADMIN"));

			userStorage.save(new User(null, "user1", passwordEncoder.encode("000"), "post1@nomiro.ru", "ROLE_USER"));
			userStorage.save(new User(null, "user2", passwordEncoder.encode("000"), "post2@nomiro.ru", "ROLE_USER"));

		};
	}
}
