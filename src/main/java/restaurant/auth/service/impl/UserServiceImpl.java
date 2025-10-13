package restaurant.auth.service.impl;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import restaurant.auth.model.ERole;
import restaurant.auth.model.Role;
import restaurant.auth.model.User;
import restaurant.auth.repository.RoleRepository;
import restaurant.auth.repository.UserRepository;
import restaurant.auth.service.UserService;

/**
 * Implements user-related business operations such as registration and role
 * promotion. This class serves as the business layer between controller and
 * repository.
 */
//@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;

	public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
			PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional
	public User registerUser(String username, String email, String rawPassword) {
		if (userRepository.existsByUsername(username)) {
			throw new IllegalStateException("User already exists: " + username);
		}

		Role userRole = roleRepository.findByName(ERole.ROLE_USER)
				.orElseThrow(() -> new RuntimeException(ERole.ROLE_USER + " not found"));

		User user = new User(username, email, passwordEncoder.encode(rawPassword));
		user.addRole(userRole);

		return userRepository.save(user);
	}

	@Override
	@Transactional
	public void addRole(Long userId, ERole role) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("UserId not found: " + userId));
		Role userRole = roleRepository.findByName(role)
				.orElseThrow(() -> new RuntimeException("Role not found: " + role));
		user.addRole(userRole);
		userRepository.save(user);
	}

	@Override
	public Optional<User> findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	public boolean existsByUsername(String username) {
		return userRepository.existsByUsername(username);
	}

	@Override
	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}
}
