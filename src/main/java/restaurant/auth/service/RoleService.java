package restaurant.auth.service;

import restaurant.auth.model.ERole;
import restaurant.auth.model.Role;

import java.util.List;
import java.util.Optional;

/**
 * Defines operations related to role management.
 */
public interface RoleService {

	/**
	 * Creates and saves a new role.
	 *
	 * @param name the enum name of the role
	 * @return the created role
	 */
	Role createRole(ERole name);

	/**
	 * Finds a role by its name.
	 *
	 * @param name the enum name of the role
	 * @return an Optional containing the found role or empty if not found
	 */
	Optional<Role> findByName(ERole name);

	/**
	 * Checks whether a role with the given enum name exists in the database.
	 *
	 * @param name the enum constant representing the role
	 * @return true if a role with the specified name exists, false otherwise
	 */
	boolean existsByName(ERole name);

	/**
	 * Returns all available roles.
	 *
	 * @return a list of all roles
	 */
	List<Role> findAll();
}
