package agile_proj_600.group_o_cma_app;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;

@RestController
public class Search extends CORS {

    @PostMapping("/search")
    public ResponseEntity<List<Employee>> search(@RequestBody Map<String, String> searchParams) {
        List<Employee> results = new ArrayList<>();

        // Establishing database connection using CORS method
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM employee_info WHERE "
                    + "employee_id LIKE ? AND "
                    + "firstName LIKE ? AND "
                    + "lastName LIKE ? AND "
                    + "phoneNumber LIKE ?";

            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, searchParams.getOrDefault("id", "%"));
                statement.setString(2, searchParams.getOrDefault("firstName", "%"));
                statement.setString(3, searchParams.getOrDefault("lastName", "%"));
                statement.setString(4, searchParams.getOrDefault("phone", "%"));

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Employee employee = new Employee();
                        employee.setId(resultSet.getInt("employee_id"));
                        employee.setFirstName(resultSet.getString("firstName"));
                        employee.setLastName(resultSet.getString("lastName"));
                        employee.setPhoneNumber(resultSet.getString("phoneNumber"));
                        employee.setRole(resultSet.getString("emp_role"));
                        employee.setStatus(resultSet.getString("status")); // Set status
                        // Add other fields as necessary
                        results.add(employee);
                    }
                }
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        return ResponseEntity.ok(results);
    }

    public class Employee {

        private int id;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String status; 
        private String emp_role;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getRole() {
            return emp_role;
        }

        public void setRole(String emp_role) {
            this.emp_role = emp_role;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

    }
}
