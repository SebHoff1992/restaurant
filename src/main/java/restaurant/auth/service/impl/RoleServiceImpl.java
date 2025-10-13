package restaurant.auth.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import restaurant.auth.model.ERole;
import restaurant.auth.model.Role;
import restaurant.auth.repository.RoleRepository;
import restaurant.auth.service.RoleService;

/**
 * Provides business logic for managing roles.
 */
//@Service
public class RoleServiceImpl implements RoleService {

	private final RoleRepository roleRepository;

	public RoleServiceImpl(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

	@Override
	@Transactional
	public Role createRole(ERole name) {
		Optional<Role> existing = roleRepository.findByName(name);
		if (existing.isPresent()) {
			return existing.get();
		}

		Role role = new Role(name);
		return roleRepository.save(role);
	}

	@Override
	public Optional<Role> findByName(ERole name) {
		return roleRepository.findByName(name);
	}

	@Override
	public List<Role> findAll() {
		return roleRepository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByName(ERole name) {
		// Delegate to repository; keeps business layer consistent
		return roleRepository.existsByName(name);
	}
}
