package agile_proj_600.group_o_cma_app;

import static agile_proj_600.group_o_cma_app.CORS.getConnection;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@SpringBootApplication
public class SignUp extends CORS {
}

@RestController
class SignupController {

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest signupRequest) {
        // Validate signup data (you can add more validation logic)
        if (isValidSignup(signupRequest)) {
            // Save user data to your database
            try {
                Connection conn = getConnection();
                if (conn != null) {
                    // Insert into users table
                    String insertUserQuery = "INSERT INTO users (username, password) VALUES (?, ?)";
                    PreparedStatement userStatement = conn.prepareStatement(insertUserQuery, PreparedStatement.RETURN_GENERATED_KEYS);
                    userStatement.setString(1, signupRequest.getUsername());
                    userStatement.setString(2, signupRequest.getPassword());
                    int affectedRows = userStatement.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Creating user failed, no rows affected.");
                    }
                    // Retrieve the auto-generated user ID
                    ResultSet generatedKeys = userStatement.getGeneratedKeys();
                    int userId = -1;
                    if (generatedKeys.next()) {
                        userId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Creating user failed, no ID obtained.");
                    }

                    // Insert into employee_info table
                    String insertEmployeeQuery = "INSERT INTO employee_info (employee_id, firstName, lastName, middleName, street, city, province, postalCode, phoneNumber) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement employeeStatement = conn.prepareStatement(insertEmployeeQuery);
                    employeeStatement.setInt(1, userId);
                    employeeStatement.setString(2, signupRequest.getFirstName());
                    employeeStatement.setString(3, signupRequest.getLastName());
                    employeeStatement.setString(4, signupRequest.getMiddleName());
                    employeeStatement.setString(5, signupRequest.getStreet());
                    employeeStatement.setString(6, signupRequest.getCity());
                    employeeStatement.setString(7, signupRequest.getProvince());
                    employeeStatement.setString(8, signupRequest.getPostalCode());
                    employeeStatement.setString(9, signupRequest.getPhoneNumber());
                    employeeStatement.executeUpdate();
                    String responseJson = "{ \"userId\": \"" + userId + "\" }";
                    conn.close();
                    return ResponseEntity.ok(responseJson);
                } else {
                    return ResponseEntity.badRequest().body("Failed to connect to the database");
                }
            } catch (SQLException e) {
                return ResponseEntity.badRequest().body("Error occurred: " + e.getMessage());
            }
        } else {
            return ResponseEntity.badRequest().body("Invalid signup data");
        }
    }

    private boolean isValidSignup(SignupRequest signupRequest) {
        // Implement your validation logic
        return signupRequest.getUsername() != null && signupRequest.getPassword() != null;
    }

    // Inner class for signup request data
    static class SignupRequest {

        private String firstName;
        private String lastName;
        private String middleName;
        private String street;
        private String city;
        private String province;
        private String postalCode;
        private String phoneNumber;
        private String username;
        private String password;

        // Getters
        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getMiddleName() {
            return middleName;
        }

        public String getStreet() {
            return street;
        }

        public String getCity() {
            return city;
        }

        public String getProvince() {
            return province;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        // Setters
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public void setMiddleName(String middleName) {
            this.middleName = middleName;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
