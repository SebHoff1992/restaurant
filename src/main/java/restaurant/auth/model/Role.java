package restaurant.auth.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entity representing a security role (e.g., ADMIN, USER). Each role is unique
 * by its enum value (ERole).
 */
@Entity
@Table(name = "roles")
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Enumerated(EnumType.STRING)
	@Column(length = 20, nullable = false, unique = true)
	private ERole name;

	/**
	 * Protected constructor to prevent direct instantiation. JPA uses this when
	 * loading entities from the database.
	 */
	protected Role() {
	}

	public Role(ERole name) {
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ERole getName() {
		return name;
	}

	public void setName(ERole name) {
		this.name = name;
	}

	/**
	 * Equality is based solely on the enum name, not on database ID. This prevents
	 * duplicate Role objects with the same logical meaning.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Role other))
			return false;
		return name == other.name;
	}

	/**
	 * Hash code also uses only the enum name for consistency with equals().
	 */
	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return "Role{" + "id=" + id + ", name=" + name + '}';
	}

}