package restaurant.auth.exception;

/**
 * Thrown when a requested role does not exist in the database.
 */
public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String roleName) {
        super("Role not found: " + roleName);
    }
}
