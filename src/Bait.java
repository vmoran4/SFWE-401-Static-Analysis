import java.sql.*;

public class Bait {
    public static void main(String[] args) {
        String userInput = args[0]; // Simulated user input
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/test");
            Statement stmt = conn.createStatement();
            // SQL injection vulnerability
            stmt.executeQuery("SELECT * FROM users WHERE id = " + userInput); // UNSAFE!
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
