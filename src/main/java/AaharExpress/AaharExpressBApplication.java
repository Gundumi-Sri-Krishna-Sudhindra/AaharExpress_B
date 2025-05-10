package AaharExpress;

import AaharExpress.model.ERole;
import AaharExpress.model.Role;
import AaharExpress.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AaharExpressBApplication {

	public static void main(String[] args) {
		SpringApplication.run(AaharExpressBApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner initDatabase(RoleRepository roleRepository) {
		return args -> {
			// Initialize roles if they don't exist
			if (roleRepository.count() == 0) {
				roleRepository.save(new Role(ERole.ROLE_USER));
				roleRepository.save(new Role(ERole.ROLE_ADMIN));
				System.out.println("Roles initialized successfully!");
			}
		};
	}
}
