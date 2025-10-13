package restaurant;

import static org.assertj.core.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import restaurant.auth.model.ERole;
import restaurant.auth.model.Role;
import restaurant.auth.model.User;
import restaurant.auth.repository.RoleRepository;
import restaurant.auth.repository.UserRepository;

/**
 * Integration test that inserts a default user with ROLE_USER into MySQL.
 */
@SpringBootTest
public class InsertUserTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder encoder;

	@Test
	void insertDefaultUser() {
		String username = "Sebastian";
		String email = "seb.hoff1992@gmail.com";
		String password = "password";

		if (userRepository.existsByUsername(username)) {
			System.out.println("User already exists: " + username);
			assertThat(userRepository.findByUsername(username)).isPresent();
			return;
		}

		Role userRole = roleRepository.findByName(ERole.ROLE_USER)
				.orElseThrow(() -> new RuntimeException("Error: ROLE_USER not found. Run InsertRolesTest first."));

		Set<Role> roles = new HashSet<>();
		roles.add(userRole);

		User user = new User(username, email, encoder.encode(password));
		user.setRoles(roles);

		userRepository.save(user);
		System.out.println("✅ Inserted user: " + username);

		assertThat(userRepository.existsByUsername(username)).isTrue();
	}
	
	
}
