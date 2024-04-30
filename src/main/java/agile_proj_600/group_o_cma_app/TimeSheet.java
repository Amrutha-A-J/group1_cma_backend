package agile_proj_600.group_o_cma_app;

import static agile_proj_600.group_o_cma_app.CORS.getConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TimeSheet extends CORS {

    @PostMapping("/approve-payroll-hourly")
    public ResponseEntity<String> approvePayrollHourly(@RequestBody Map<String, Object> requestData) {
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "UPDATE payroll_hourly SET status = 'Approved' WHERE employee_id = ? AND date BETWEEN ? AND ? AND status = 'pending'")) {
            stmt.setInt(1, (int)requestData.get("employee_id"));
            stmt.setString(2, (String) requestData.get("startDate"));
            stmt.setString(3, (String) requestData.get("endDate"));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                return ResponseEntity.ok("Hourly payroll logs approved successfully");
            } else {
                return ResponseEntity.ok("No hourly payroll logs found for approval within the specified date range");
            }
        } catch (SQLException e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error occurred while processing the request.");
        }
    }

    @PostMapping("/approve-payroll-monthly")
    public ResponseEntity<String> approvePayrollMonthly(@RequestBody Map<String, Object> requestData) {
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "UPDATE payroll_salaried SET status = 'Approved' WHERE employee_id = ? AND pay_date = ? AND status = 'pending'")) {
            stmt.setInt(1, (int)requestData.get("employee_id"));
            stmt.setString(2, (String) requestData.get("monthStartDate"));
            System.out.println(stmt);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                return ResponseEntity.ok("Monthly payroll log approved successfully");
            } else {
                return ResponseEntity.ok("No monthly payroll log found for approval with the specified pay day");
            }
        } catch (SQLException e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error occurred while processing the request.");
        }
    }

}
