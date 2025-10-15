package restaurant.infrastructure.config;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class AppConfig {
	private final AppProperties props;

	public AppConfig(AppProperties props) {
		this.props = props;
	}

//	@PostConstruct
//	public void init() {
//		System.out.println("JWT Secret: " + props.getAuth().getSecurity().getJwtSecret());
//	}
}
