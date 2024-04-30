package agile_proj_600.group_o_cma_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class GROUP_O_CMA_APP extends CORS {

    public static void main(String[] args) {
        SpringApplication.run(GROUP_O_CMA_APP.class, args);
    }

    @RestController
    public class LoginController {

        @PostMapping("/loginSubmit")
        public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
            // Assuming you have a valid role (e.g., "admin", "user", etc.) and id obtained from your logic
            Map<String, String> credentials = isValidCredentials(loginRequest.getUsername(), loginRequest.getPassword());

            if (credentials != null) {
                // Return the id and role in the response
                return ResponseEntity.ok(credentials);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        }

        private Map<String, String> isValidCredentials(String username, String password) {
            // Establishing database connection using CORS method
            try (Connection conn = getConnection()) {
                String sql = "SELECT id, role FROM users WHERE username = ? AND password = ?";
                try (PreparedStatement statement = conn.prepareStatement(sql)) {
                    statement.setString(1, username);
                    statement.setString(2, password);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            Map<String, String> result = new HashMap<>();
                            result.put("id", resultSet.getString("id"));
                            result.put("role", resultSet.getString("role"));
                            return result; // Return the id and role if credentials are valid
                        } else {
                            return null; // Invalid credentials
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return null; // Error occurred
            }
        }

    }

    public static class LoginRequest {

        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
