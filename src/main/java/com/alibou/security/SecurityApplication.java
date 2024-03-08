package com.alibou.security;

import com.alibou.security.auth.AuthenticationService;
import com.alibou.security.auth.RegisterRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityApplication.class, args);
	}

//	@Bean
	public CommandLineRunner commandLineRunner(
			AuthenticationService service
	) {
		return args -> {
			var admin = new RegisterRequest(
					"Admin",
					"Admin",
					"admin@mail.com",
					"password",
					"ADMIN"
			);
			service.register(admin);

			var manager = new RegisterRequest(
					"Manager",
					"Manager",
					"manager@mail.com",
					"password",
					"EDITOR"
			);
			service.register(manager);
		};
	}
}
