package restaurant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import restaurant.auth.model.ERole;
import restaurant.auth.model.Role;
import restaurant.auth.repository.RoleRepository;

/**
 * Demonstrates how @Transactional influences persistence behavior.
 */
@SpringBootTest
public class TransactionalBehaviorTest {

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Example 1: WITHOUT @Transactional — save() is required.
     */
    @Test
    void testWithoutTransactional() {
        Role role = new Role(ERole.ROLE_USER);

        // Without save(), this object only lives in memory
        // and will NOT be persisted to the database.
        roleRepository.save(role);

        System.out.println("✅ Role saved explicitly without @Transactional");
    }

    /**
     * Example 2: WITH @Transactional — automatic persistence on commit.
     * 
     * The entity is "managed" and JPA automatically flushes the change
     * at transaction commit time — no explicit save() required.
     */
    @Test
    @Transactional
    @Commit // ensures data is actually written to DB (for demonstration)
    void testWithTransactional() {
        Role role = new Role(ERole.ROLE_MODERATOR);

        // The Role is now "managed" by the Persistence Context.
        // When the method finishes and the transaction commits,
        // Hibernate will automatically insert it.
        System.out.println("✅ Role will be auto-saved at transaction commit");
    }

    /**
     * Example 3: WITH @Transactional — modifying a managed entity.
     */
    @Test
    @Transactional
    @Commit
    void testModifyExistingRole() {
        // Load an existing entity (it becomes "managed")
        Role role = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));

        // Modify the entity
        role.setName(ERole.ROLE_ADMIN); // not realistic but illustrates update

        // No save() needed — dirty checking detects the change
        System.out.println("✅ ROLE_USER renamed to ROLE_ADMIN automatically at commit");
    }
}
