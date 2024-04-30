package agile_proj_600.group_o_cma_app;

import static agile_proj_600.group_o_cma_app.CORS.getConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PayrollMonthly extends CORS {

    @GetMapping("/payroll-monthly-log")
    public ResponseEntity<List<Map<String, Object>>> getMonthlyPayrollLog(@RequestParam int employee_id, @RequestParam String monthStartDate) {
        List<Map<String, Object>> results = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM payroll_salaried WHERE employee_id = ? AND pay_date >= ? AND pay_date < DATE_ADD(?, INTERVAL 1 MONTH)")) {
            stmt.setInt(1, employee_id);
            stmt.setString(2, monthStartDate);
            stmt.setString(3, monthStartDate);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("employee_id", rs.getInt("employee_id"));
                    row.put("pay_date", rs.getString("pay_date"));
                    row.put("salary", rs.getFloat("salary"));
                    row.put("status", rs.getString("status"));
                    row.put("no_leaves", rs.getInt("no_leaves"));
                    row.put("payable_salary", rs.getFloat("payable_salary"));
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.ok(results);
    }

    @PostMapping("/payroll-monthly-log")
    public ResponseEntity<String> upsertMonthlyPayrollLog(@RequestBody Map<String, Object> logData) {
        String employee_idString = (String) logData.get("employee_id");
        int employee_id = Integer.parseInt(employee_idString);
        String monthStartDate = (String) logData.get("monthStartDate");

        // Handling potential null values
        Object unpaidLeavesObj = logData.get("no_leaves"); // Update key here
        int unpaidLeaves = 0; // Default value if the key is not present or the value is null
        if (unpaidLeavesObj != null) {
            unpaidLeaves = (int) unpaidLeavesObj;
        }

        Object salaryObj = logData.get("salary");
        float salary = 0.0f; // Default value if the key is not present or the value is null
        if (salaryObj != null) {
            salary = Float.parseFloat(salaryObj.toString());
        }

        String status = (String) logData.get("status");

        Object payableSalaryObj = logData.get("payable_salary"); // Update key here
        float payableSalary = 0.0f; // Default value if the key is not present or the value is null
        if (payableSalaryObj != null) {
            payableSalary = Float.parseFloat(payableSalaryObj.toString());
        }

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement("INSERT INTO payroll_salaried (employee_id, pay_date, no_leaves, salary, status, payable_salary) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE no_leaves = ?, salary = ?, status = ?, payable_salary = ?")) {
            stmt.setInt(1, employee_id);
            stmt.setString(2, monthStartDate);
            stmt.setInt(3, unpaidLeaves);
            stmt.setFloat(4, salary);
            stmt.setString(5, status);
            stmt.setFloat(6, payableSalary);
            stmt.setInt(7, unpaidLeaves);
            stmt.setFloat(8, salary);
            stmt.setString(9, status);
            stmt.setFloat(10, payableSalary);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                return new ResponseEntity<>("Payroll log upserted successfully", HttpStatus.OK);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to upsert payroll log", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Failed to upsert payroll log", HttpStatus.BAD_REQUEST);
    }

}
