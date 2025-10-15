package restaurant.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Component
@ConfigurationProperties(prefix = "app")
@Validated
public class AppProperties {

	private Simulation simulation = new Simulation();
	private Auth auth = new Auth();

	public static class Simulation {
		/**
		 * Whether the simulation runs automatically on the start.
		 */
		private boolean enabled;

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
	}

	public static class Auth {
		private Security security = new Security();

		public static class Security {
			/**
			 * Whether security features (e.g. JWT auth) are enabled.
			 */
			private Boolean enabled;

			/**
			 * Secret key used to sign and verify JWT tokens. Must be a non-empty string for
			 * security reasons.
			 */
			@NotBlank(message = "app.auth.security.jwtSecret must not be blank")
			private String jwtSecret;

			/**
			 * The cookie name used to store the JWT token on the client.
			 */
			@NotBlank(message = "app.auth.security.jwtCookieName must not be blank")
			private String jwtCookieName;

			/**
			 * Expiration time for JWT tokens, in milliseconds. Must be a positive number.
			 */
			@Positive(message = "app.auth.security.jwtExpirationMs must be greater than 0")
			private long jwtExpirationMs = 86400000; // default = 24 hours

			public boolean isEnabled() {
				return enabled;
			}

			public void setEnabled(boolean enabled) {
				this.enabled = enabled;
			}

			public String getJwtSecret() {
				return jwtSecret;
			}

			public void setJwtSecret(String jwtSecret) {
				this.jwtSecret = jwtSecret;
			}

			public String getJwtCookieName() {
				return jwtCookieName;
			}

			public void setJwtCookieName(String jwtCookieName) {
				this.jwtCookieName = jwtCookieName;
			}

			public long getJwtExpirationMs() {
				return jwtExpirationMs;
			}

			public void setJwtExpirationMs(long jwtExpirationMs) {
				this.jwtExpirationMs = jwtExpirationMs;
			}
		}

		public Security getSecurity() {
			return security;
		}
	}

	public Auth getAuth() {
		return auth;
	}

	public Simulation getSimulation() {
		return simulation;
	}
}
