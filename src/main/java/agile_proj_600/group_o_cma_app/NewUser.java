package agile_proj_600.group_o_cma_app;

import static agile_proj_600.group_o_cma_app.CORS.getConnection;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;

@RestController
public class NewUser extends CORS {

    @GetMapping("/new-staff")
    public ResponseEntity<List<Map<String, Object>>> getPendingEmployees() {
        List<Map<String, Object>> results = new ArrayList<>();

        // Establishing database connection using CORS method
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM employee_info WHERE status = 'pending'";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        // Retrieve data from result set and add to results list
                        Map<String, Object> employeeData = new HashMap<>();
                        employeeData.put("employee_id", rs.getInt("employee_id"));
                        employeeData.put("firstName", rs.getString("firstName"));
                        employeeData.put("lastName", rs.getString("lastName"));
                        employeeData.put("middleName", rs.getString("middleName"));
                        employeeData.put("street", rs.getString("street"));
                        employeeData.put("city", rs.getString("city"));
                        employeeData.put("province", rs.getString("province"));
                        employeeData.put("postalCode", rs.getString("postalCode"));
                        employeeData.put("phoneNumber", rs.getString("phoneNumber"));
                        employeeData.put("employee_type", rs.getString("employee_type"));
                        employeeData.put("status", rs.getString("status"));
                        results.add(employeeData);
                    }
                }
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        return ResponseEntity.ok(results);
    }

    @PostMapping("/save-employee-details")
    public ResponseEntity<String> saveEmployeeDetails(@RequestBody Map<String, String> request) {
        // Extract employee_id, employee_type, emp_role, and emp_base_pay from request
        String employeeId = request.get("employee_id");
        String employeeType = request.get("employee_type");
        String empRole = request.get("emp_role");
        String empBasePay = request.get("emp_base_pay");
        String status = "Approved"; // Set the desired status value

        // Update the employee_type, emp_role, emp_base_pay, and status in the database
        try (Connection conn = getConnection()) {
            String updateSql = "UPDATE employee_info SET employee_type = ?, emp_role = ?, emp_base_pay = ?, status = ? WHERE employee_id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setString(1, employeeType);
                updateStmt.setString(2, empRole);
                updateStmt.setString(3, empBasePay);
                updateStmt.setString(4, status);
                updateStmt.setInt(5, Integer.parseInt(employeeId));
                int rowsUpdated = updateStmt.executeUpdate();
                if (rowsUpdated > 0) {
                    return ResponseEntity.ok("Employee details saved successfully");
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
                }
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
