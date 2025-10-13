package restaurant.auth.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import restaurant.auth.model.ERole;
import restaurant.auth.model.Role;
import restaurant.auth.model.User;
import restaurant.auth.repository.RoleRepository;
import restaurant.auth.repository.UserRepository;

/**
 * Populates the database with the three default roles and one initial user.
 * Should be executed only after a full database reset.
 */
@SpringBootTest
@Transactional
@Commit
public class InsertInitialDataTest {

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * Inserts the default roles (USER, MODERATOR, ADMIN) and creates a single user
	 * "Sebastian" with ROLE_USER.
	 */
	@Test
	void insertInitialData() {
		// --- Safety check: ensure tables are empty ---
		long userCount = userRepository.count();
		long roleCount = roleRepository.count();

		if (userCount > 0 || roleCount > 0) {
			throw new IllegalStateException(
					"❌ Database not empty! Please run empty the db before executing this test.");
		}

		System.out.println("✅ Verified: Database is empty. Proceeding with initial data insertion...");

		// --- Insert roles ---
		Role roleUser = new Role(ERole.ROLE_USER);
		Role roleMod = new Role(ERole.ROLE_MODERATOR);
		Role roleAdmin = new Role(ERole.ROLE_ADMIN);

		roleRepository.save(roleUser);
		roleRepository.save(roleMod);
		roleRepository.save(roleAdmin);

		System.out.println("✅ Inserted roles: USER, MODERATOR, ADMIN");

		User user = new User("Sebastian", "seb.hoff1992@gmail.com", passwordEncoder.encode("password"));
		user.addRole(roleUser);

		userRepository.save(user);// save new user into the persistence context

		System.out.println("✅ Created user 'Sebastian' with ROLE_USER");
	}
}
