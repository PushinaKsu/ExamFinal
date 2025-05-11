package dbUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static utils.ConfigReader.get;

public class DatabaseUtils {
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                get("dbUrl"),
                get("dbUser"),
                get("dbPassword")
        );
    }

    public static void deleteEmployeeById(int employeeId) {
        deleteById(employeeId, "DELETE FROM employee WHERE id = ?");
    }

    public static void deleteCompanyById(int companyId) {
        deleteById(companyId, "DELETE FROM company WHERE id = ?");
    }

    private static void deleteById(int id, String statement) {
        try (
                Connection conn = DatabaseUtils.getConnection();
                PreparedStatement deleteStatement = conn.prepareStatement(statement)
        ) {
            deleteStatement.setInt(1, id);
            deleteStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}