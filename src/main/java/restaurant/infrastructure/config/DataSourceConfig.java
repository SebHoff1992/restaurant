package restaurant.infrastructure.config;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;

@Configuration
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    private final ApplicationContext context;

    public DataSourceConfig(ApplicationContext context) {
        this.context = context;
    }

    @Bean
    public DataSource dataSource() {
        String url = "jdbc:mysql://localhost:3306/restaurantdb?useSSL=false";
        String driver = "com.mysql.cj.jdbc.Driver";
        String username = System.getenv("DB_USER");
        String password = System.getenv("DB_PASS");

        if (username == null || password == null) {
            log.error("Missing DB_USER or DB_PASS. Application will terminate.");
            terminateApp();
            return null;
        }

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setDriverClassName(driver);
        ds.setUsername(username);
        ds.setPassword(password);

        try (Connection conn = ds.getConnection()) {
            log.info("MySQL connection successful.");
        } catch (Exception e) {
            log.error("MySQL not reachable: {}", e.getMessage());
            terminateApp();
        }

        return ds;
    }

    private void terminateApp() {
        log.error("Critical startup failure: shutting down application...");
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {}
            SpringApplication.exit(context, () -> 1);
        }).start();
    }
}
