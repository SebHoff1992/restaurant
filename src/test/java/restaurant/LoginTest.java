package restaurant;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoginTest {

	@LocalServerPort
	private int port;

	private WebTestClient webClient;

	@Test
	void testLogin() {
		webClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();

		// Login Request
		String loginJson = "{\"username\":\"Sebastian\",\"password\":\"password\"}";

		webClient.post().uri("/api/auth/signin").contentType(MediaType.APPLICATION_JSON).bodyValue(loginJson).exchange()
				.expectStatus().isOk().expectBody().jsonPath("$.id").exists().jsonPath("$.username")
				.isEqualTo("Sebastian").jsonPath("$.email").isEqualTo("seb.hoff1992@gmail.com").jsonPath("$.roles")
				.isArray();
	}
}
