import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.Random;

public class MyApp {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydatabase";
    private static final String DB_USER = " ";
    private static final String DB_PASSWORD = " ";

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java MyApp <command>");
            return;
        }

        int command = Integer.parseInt(args[0]);
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            switch (command) {
                case 1:
                    createTable(conn);
                    break;
                case 2:
                    if (args.length < 4) {
                        System.out.println("Usage: java MyApp 2 <full name> <date of birth> <gender>");
                        return;
                    }
                    String fullName = args[1];
                    LocalDate dateOfBirth = LocalDate.parse(args[2]);
                    String gender = args[3];
                    createRecord(conn, fullName, dateOfBirth, gender);
                    break;
                case 3:
                    outputLines(conn);
                    break;
                case 4:
                    fillTable(conn);
                    break;
                case 5:
                    sampleTable(conn);
                    break;
                default:
                    System.out.println("Unknown command: " + command);
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTable(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS person ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "full_name VARCHAR(255),"
                    + "date_of_birth DATE,"
                    + "gender VARCHAR(10)"
                    + ")");
        }
    }

    private static void createRecord(Connection conn, String fullName, LocalDate dateOfBirth, String gender) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO person (full_name, date_of_birth, gender) VALUES (?, ?, ?)")) {
            stmt.setString(1, fullName);
            stmt.setDate(2, Date.valueOf(dateOfBirth));
            stmt.setString(3, gender);
            stmt.executeUpdate();
        }
    }

    private static void outputLines(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT full_name, date_of_birth, gender FROM person ORDER BY full_name")) {
            while (rs.next()) {
                String fullName = rs.getString("full_name");
                LocalDate dateOfBirth = rs.getDate("date_of_birth").toLocalDate();
                String gender = rs.getString("gender");
                int age = Period.between(dateOfBirth, LocalDate.now()).getYears();
                System.out.printf("%s, %s, %s, %d\n", fullName, dateOfBirth, gender, age);
            }
        }
    }

    private static void fillTable(Connection conn) throws SQLException {
        Random random = new Random();
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO person (full_name, date_of_birth, gender) VALUES (?, ?, ?)")) {
            for (int i = 0; i < 1000000; i++) {
                String fullName = generateFullName(random);
                LocalDate dateOfBirth = generateDateOfBirth(random);
               
                String gender = i % 2 == 0 ? "Male" : "Female";
                stmt.setString(1, fullName);
                stmt.setDate(2, Date.valueOf(dateOfBirth));
                stmt.setString(3, gender);
                stmt.executeUpdate();
                if (gender.equals("Male") && fullName.startsWith("F") && i < 100) {
                    System.out.printf("%s, %s, %s\n", fullName, dateOfBirth, gender);
                }
            }
        }
    }

    private static String generateFullName(Random random) {
        String[] firstNames = {"Alice", "Bob", "Charlie", "David", "Eve", "Frank", "Grace", "Henry", "Isabelle", "Jack", "Kate", "Liam", "Mia", "Noah", "Olivia", "Peter", "Quinn", "Rachel", "Sam", "Tina", "Ursula", "Victoria", "Wendy", "Xander", "Yvonne", "Zachary"};
        String[] lastNames = {"Smith", "Johnson", "Brown", "Davis", "Garcia", "Jones", "Taylor", "Williams", "White", "Green", "Hall", "Clark", "Walker", "Baker", "Carter", "Collins", "Edwards", "Evans", "Gonzalez", "Gray", "Hayes", "Hill", "Hughes", "Jackson", "Lee", "Lewis", "Long", "Martin", "Mitchell", "Moore", "Nelson", "Perez", "Price", "Ramirez", "Reed", "Roberts", "Robinson", "Scott", "Stewart", "Turner", "Ward", "Wright", "Young"};
        String firstName = firstNames[random.nextInt(firstNames.length)];
        String lastName = lastNames[random.nextInt(lastNames.length)];
        return firstName + " " + lastName;
    }
    
    private static LocalDate generateDateOfBirth(Random random) {
        int year = 1920 + random.nextInt(81);
        int month = random.nextInt(12) + 1;
        int day = random.nextInt(28) + 1;
        return LocalDate.of(year, month, day);
    }
    
    private static void sampleTable(Connection conn) throws SQLException {
        long startTime = System.currentTimeMillis();
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM person WHERE gender = 'Male' AND full_name LIKE 'F%'")) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String fullName = rs.getString("full_name");
                    LocalDate dateOfBirth = rs.getDate("date_of_birth").toLocalDate();
                    String gender = rs.getString("gender");
                    int age = Period.between(dateOfBirth, LocalDate.now()).getYears();
                    System.out.printf("%s, %s, %s, %d\n", fullName, dateOfBirth, gender, age);
                }
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.printf("Execution time: %d ms\n", endTime - startTime);
    }
}