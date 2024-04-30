/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package agile_proj_600.group_o_cma_app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class Employee_Info extends CORS {

    @GetMapping("/employee_info")
    public Map<String, String> getEmployeeInfo(@RequestParam int id) {
        Map<String, String> employeeInfo = new HashMap<>();
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM employee_info WHERE employee_id = ?";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        employeeInfo.put("id", resultSet.getString("employee_id"));
                        employeeInfo.put("firstName", resultSet.getString("firstName"));
                        employeeInfo.put("lastName", resultSet.getString("lastName"));
                        employeeInfo.put("middleName", resultSet.getString("middleName"));
                        employeeInfo.put("street", resultSet.getString("street"));
                        employeeInfo.put("city", resultSet.getString("city"));
                        employeeInfo.put("province", resultSet.getString("province"));
                        employeeInfo.put("postalCode", resultSet.getString("postalCode"));
                        employeeInfo.put("phoneNumber", resultSet.getString("phoneNumber"));
                        employeeInfo.put("emp_role", resultSet.getString("emp_role"));
                        employeeInfo.put("emp_base_pay", resultSet.getString("emp_base_pay"));
                        employeeInfo.put("employee_type", resultSet.getString("employee_type"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeeInfo;
    }

    @PutMapping("/employee_info")
    public ResponseEntity<String> updateEmployeeInfo(@RequestParam int id, @RequestBody Map<String, String> updates) {
        try (Connection conn = getConnection()) {
            for (Map.Entry<String, String> entry : updates.entrySet()) {
                String sql = "UPDATE employee_info SET " + entry.getKey() + " = ? WHERE employee_id = ?";
                try (PreparedStatement statement = conn.prepareStatement(sql)) {
                    statement.setString(1, entry.getValue());
                    statement.setInt(2, id);
                    statement.executeUpdate();
                }
            }
            return ResponseEntity.ok("Employee info updated successfully!");
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
        }
    }
}
