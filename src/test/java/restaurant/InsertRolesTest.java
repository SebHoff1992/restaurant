package restaurant;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import restaurant.auth.model.ERole;
import restaurant.auth.model.Role;
import restaurant.auth.repository.RoleRepository;

@SpringBootTest
public class InsertRolesTest {

	@Autowired
	private RoleRepository roleRepository;

	/**
	 * Inserts the default application roles (USER, MODERATOR, ADMIN) into the
	 * database if they do not exist yet.
	 */
	@Test
	void insertUserRoles() {
		createIfNotExists(ERole.ROLE_USER);
		createIfNotExists(ERole.ROLE_MODERATOR);
		createIfNotExists(ERole.ROLE_ADMIN);

		// --- Assert and log result ---
		long count = roleRepository.count();
		System.out.println("✅ Roles currently in DB: " + count);
		assertThat(count).as("Database should contain at least 3 default roles").isGreaterThanOrEqualTo(3);
	}

	/**
	 * Creates a new role if it does not already exist. Uses repository lookup to
	 * avoid duplicate role entries.
	 */
	private void createIfNotExists(ERole roleName) {
		roleRepository.findByName(roleName)
				.ifPresentOrElse(existing -> System.out.println("Role already exists: " + existing.getName()), () -> {
					Role r = new Role(roleName); // use constructor that sets the name
					roleRepository.save(r);
					System.out.println("Inserted new role: " + roleName);
				});
	}
}
