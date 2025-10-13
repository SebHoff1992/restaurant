package restaurant.auth.service;

import java.util.Optional;

import restaurant.auth.model.ERole;
import restaurant.auth.model.User;

/**
 * Defines operations for user management such as registration and role
 * promotion.
 */
public interface UserService {

	/**
	 * Registers a new user with the default role.
	 *
	 * @param username the username
	 * @param email    the user's email
	 * @param password the user's plaintext password
	 * @return the created user entity
	 */
	User registerUser(String username, String email, String password);

	/**
	 * Adds a specific role to an existing user and persists the change in the
	 * database. If the user or role does not exist, an exception should be thrown
	 * by the service implementation.
	 * 
	 * @param userId the ID of the user who will receive the new role
	 * @param role   the role to be assigned
	 * 
	 * 
	 */
	void addRole(Long userId, ERole role);

	Optional<User> findByUsername(String username);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);
}
