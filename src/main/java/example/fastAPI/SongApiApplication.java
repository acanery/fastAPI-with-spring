package example.fastAPI;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Locale;

@SpringBootApplication
public class SongApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SongApiApplication.class, args);
	}

	@PostConstruct
	public void init() {
		Locale.setDefault(Locale.ROOT);
	}


}
