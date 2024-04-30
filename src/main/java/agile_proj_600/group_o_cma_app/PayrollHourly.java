package agile_proj_600.group_o_cma_app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
public class PayrollHourly extends CORS {

    @GetMapping("/payroll-hourly-log")
    public ResponseEntity<List<Map<String, Object>>> getHourlyPayrollLog(@RequestParam int employee_id, @RequestParam String startDate,
            @RequestParam String endDate) {
        List<Map<String, Object>> results = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM payroll_hourly WHERE employee_id = ? AND date BETWEEN ? AND ?")) {
            stmt.setInt(1, employee_id);
            stmt.setString(2, startDate);
            stmt.setString(3, endDate);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> payrollEntry = Map.of(
                        "employee_id", rs.getInt("employee_id"),
                        "date", rs.getString("date"),
                        "start_time", rs.getString("start_time"),
                        "end_time", rs.getString("end_time"),
                        "totalHours", rs.getFloat("totalHours"),
                        "dailyEarning", rs.getFloat("dailyEarning"),
                        "status", rs.getString("status")
                );
                results.add(payrollEntry);
            }
        } catch (SQLException e) {
            System.out.print(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        return ResponseEntity.ok(results);
    }

    @PostMapping("/payroll-hourly-log-upsert")
    public ResponseEntity<String> upsertHourlyPayrollLog(@RequestBody Map<String, Object> logData) {
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO payroll_hourly (employee_id, date, start_time, end_time, totalHours, dailyEarning, status) VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time), totalHours = VALUES(totalHours), dailyEarning = VALUES(dailyEarning), status = VALUES(status)")) {
            stmt.setInt(1, (int) logData.get("employee_id"));
            stmt.setString(2, (String) logData.get("date"));
            stmt.setString(3, (String) logData.get("start_time"));
            stmt.setString(4, (String) logData.get("end_time"));
            stmt.setFloat(5, Float.parseFloat(logData.get("totalHours").toString()));
            stmt.setFloat(6, Float.parseFloat(logData.get("dailyEarning").toString()));
            stmt.setString(7, (String) logData.get("status"));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                return ResponseEntity.ok("Upsert successful");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upsert failed");
            }
        } catch (SQLException e) {
            System.out.print(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }



}
