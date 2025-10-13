package restaurant.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@SpringBootTest
public class DatabaseResetTest {

    @Autowired
    private DataSource dataSource;

    /**
     * Resets all important database tables to a clean state.
     * This can be run manually or before integration tests.
     */
    @Test
    void resetDatabase() throws Exception {
        try (Connection con = dataSource.getConnection();
             Statement st = con.createStatement()) {

            st.execute("SET FOREIGN_KEY_CHECKS = 0");
            st.execute("TRUNCATE TABLE user_roles");
            st.execute("TRUNCATE TABLE users");
            st.execute("TRUNCATE TABLE roles");
            st.execute("SET FOREIGN_KEY_CHECKS = 1");

            System.out.println("✅ Database successfully reset for testing.");
        }
    }
}
