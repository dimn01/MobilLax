package MobilLax.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RestController
@RequestMapping("/health")
public class HealthCheckController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/db")
    public String checkDbConnection() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(1) ? "DB 연결 성공!" : "DB 연결 실패!";
        } catch (SQLException e) {
            return "DB 연결 오류: " + e.getMessage();
        }
    }
}

