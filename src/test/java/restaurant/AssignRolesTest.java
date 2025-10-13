package restaurant;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import jakarta.transaction.Transactional;
import restaurant.auth.model.ERole;
import restaurant.auth.model.Role;
import restaurant.auth.model.User;
import restaurant.auth.service.impl.RoleServiceImpl;
import restaurant.auth.service.impl.UserServiceImpl;

@SpringBootTest
public class AssignRolesTest {

	@Autowired
	private UserServiceImpl userServiceImpl;

	@Autowired
	private RoleServiceImpl roleServiceImpl;

	@Test
	@Commit
	@Transactional
	void addExtraRolesToExistingUser() {
		User user = userServiceImpl.findByUsername("Sebastian")
				.orElseThrow(() -> new RuntimeException("User not found"));
		Role roleUser = roleServiceImpl.findByName(ERole.ROLE_USER)
				.orElseThrow(() -> new RuntimeException("ROLE_USER missing"));
		Role admin = roleServiceImpl.findByName(ERole.ROLE_ADMIN)
				.orElseThrow(() -> new RuntimeException("ROLE_ADMIN missing"));
		Role mod = roleServiceImpl.findByName(ERole.ROLE_MODERATOR)
				.orElseThrow(() -> new RuntimeException("ROLE_MODERATOR missing"));

		user.addRole(roleUser);
		user.addRole(admin);
		user.addRole(mod);

		System.out.println("✅ Updated roles for user: " + user.getUsername());
		user.getRoles().forEach(r -> System.out.println(" - " + r.getName()));

		assertThat(userServiceImpl.findByUsername("Sebastian").orElseThrow().getRoles()).hasSizeGreaterThanOrEqualTo(3);
	}
}
