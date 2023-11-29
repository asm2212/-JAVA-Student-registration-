package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

class Student {
    int uid;
    String fullName;
    String department;
    int year;
    int semester;
    double previousGPA;

    public Student(int uid, String fullName, String department, int year, int semester, double previousGPA) {
        this.uid = uid;
        this.fullName = fullName;
        this.department = department;
        this.year = year;
        this.semester = semester;
        this.previousGPA = previousGPA;
    }

    @Override
    public String toString() {
        return "UID: " + uid + "\nFull Name: " + fullName + "\nDepartment: " + department + "\nYear: " + year +
                "\nSemester: " + semester + "\nPrevious GPA: " + previousGPA;
    }

    public String getAdditionalInformation() {
       return "Regular student";
    }
}

class InternationalStudent extends Student {
    String country;

    public InternationalStudent(int uid, String fullName, String department, int year, int semester, double previousGPA, String country) {
        super(uid, fullName, department, year, semester, previousGPA);
        this.country = country;
    }

    @Override
    public String toString() {
        return super.toString() + "\nCountry: " + country;
    }

    @Override
    public String getAdditionalInformation() {
        return "International student";
    }
}

public class StudentRegistrationSystem {
    private static List<Student> students = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static Connection connection;

    public static void main(String[] args) {

        connectToMySQL();


        createStudentsTable();

        while (true) {
            System.out.println("1. Register Student");
            System.out.println("2. Exit");

            System.out.print("Enter your choice: ");
            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    registerStudent();
                    break;
                case 2:
                    System.out.println("Exiting the program. Goodbye!");
                    closeConnection();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }

    private static void connectToMySQL() {
        String jdbcUrl = "jdbc:mysql://localhost:3306/student_registration";
        String user = "root";
        String password = "ASM127862";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcUrl, user, password);
            System.out.println("Connected to MySQL successfully.");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error connecting to MySQL: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void createStudentsTable() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS students ("
                + "uid INT AUTO_INCREMENT PRIMARY KEY,"
                + "fullName VARCHAR(255) NOT NULL,"
                + "department VARCHAR(255) NOT NULL,"
                + "year INT NOT NULL,"
                + "semester INT NOT NULL,"
                + "previousGPA DOUBLE NOT NULL,"
                + "country VARCHAR(255)"
                + ")";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableQuery);
        } catch (SQLException e) {
            System.err.println("Error creating students table: " + e.getMessage());
            System.exit(1);
        }
    }

    private static int getUserChoice() {
        int choice = -1;
        while (choice == -1) {
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
        return choice;
    }

    private static void registerStudent() {
        System.out.println("Select student type:");
        System.out.println("1. Regular Student");
        System.out.println("2. International Student");

        int studentType = getUserChoice();

        if (studentType == 1) {
            registerRegularStudent();
        } else {
            registerInternationalStudent();
        }
    }

    private static void registerRegularStudent() {
        String fullName = getNonNumericInput("Enter student full name: ");
        String department = getNonNumericInput("Enter student department: ");
        int year = getUserInputInRange("Enter student year (1-7): ", 1, 7);
        int semester = getUserInputInRange("Enter student semester (1-2): ", 1, 2);

        double previousGPA = 0.0;

        if (year != 1 || semester != 1) {

            previousGPA = getGPAInputInRange("Enter previous semester GPA (0-4): ", 0, 4);
        }

        Student regularStudent = new Student(0, fullName, department, year, semester, previousGPA);
        students.add(regularStudent);


        storeStudentInMySQL(regularStudent);

        System.out.println("Regular Student registered successfully!\n");
        printRegisteredSlip(regularStudent);
    }

    private static void registerInternationalStudent() {
        String fullName = getNonNumericInput("Enter international student full name: ");
        String department = getNonNumericInput("Enter international student department: ");
        int year = getUserInputInRange("Enter international student year (1-7): ", 1, 7);
        int semester = getUserInputInRange("Enter international student semester (1-2): ", 1, 2);
        double previousGPA = getGPAInputInRange("Enter previous semester GPA (0-4): ", 0, 4);
        String country = getNonNumericInput("Enter international student country: ");

        InternationalStudent internationalStudent = new InternationalStudent(0, fullName, department, year, semester, previousGPA, country);
        students.add(internationalStudent);


        storeStudentInMySQL(internationalStudent);

        System.out.println("International Student registered successfully!\n");
        printRegisteredSlip(internationalStudent);
    }

    private static String getNonNumericInput(String prompt) {
        String value = "";
        while (value.isEmpty() || value.matches(".*\\d.*")) {
            System.out.print(prompt);
            value = scanner.nextLine().trim();

            if (value.isEmpty() || value.matches(".*\\d.*")) {
                System.out.println("Invalid input. Please enter a non-numeric value.");
            }
        }
        return value;
    }

    private static int getUserInputInRange(String prompt, int min, int max) {
        int value = -1;
        while (value == -1) {
            try {
                System.out.print(prompt);
                value = scanner.nextInt();
                scanner.nextLine();

                if (value < min || value > max) {
                    System.out.println("Invalid input. Please enter a number between " + min + " and " + max + ".");
                    value = -1;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine();
            }
        }
        return value;
    }

    private static double getGPAInputInRange(String prompt, double min, double max) {
        double value = -1.0;
        while (value == -1.0) {
            try {
                System.out.print(prompt);
                value = scanner.nextDouble();
                scanner.nextLine();

                if (value < min || value > max) {
                    System.out.println("Invalid input. Please enter a GPA between " + min + " and " + max + ".");
                    value = -1.0;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid GPA.");
                scanner.nextLine();
            }
        }
        return value;
    }

    private static void storeStudentInMySQL(Student student) {
        String insertQuery = "INSERT INTO students (fullName, department, year, semester, previousGPA, country) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, student.fullName);
            preparedStatement.setString(2, student.department);
            preparedStatement.setInt(3, student.year);
            preparedStatement.setInt(4, student.semester);
            preparedStatement.setDouble(5, student.previousGPA);

            if (student instanceof InternationalStudent) {
                preparedStatement.setString(6, ((InternationalStudent) student).country);
            } else {
                preparedStatement.setNull(6, java.sql.Types.VARCHAR);
            }

            preparedStatement.executeUpdate();

            // Retrieve the generated UID (auto-incremented) from the executed statement
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int uid = generatedKeys.getInt(1);
                student.uid = uid;
            }

            System.out.println("Student information stored in MySQL.");
        } catch (SQLException e) {
            System.err.println("Error storing student information: " + e.getMessage());
        }
    }

    private static void printRegisteredSlip(Student student) {
        System.out.println("\n------ Registered Student Slip ------");
        System.out.println(student);
        System.out.println("Student Type: " + student.getAdditionalInformation());
        System.out.println("-------------------------------------\n");
    }

    private static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection to MySQL closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
