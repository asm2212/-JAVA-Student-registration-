CREATE DATABASE IF NOT EXISTS student_registration;

USE student_registration;

CREATE TABLE IF NOT EXISTS students (
    uid INT AUTO_INCREMENT PRIMARY KEY,
    fullName VARCHAR(255) NOT NULL,
    department VARCHAR(255) NOT NULL,
    year INT NOT NULL,
    semester INT NOT NULL,
    previousGPA DOUBLE NOT NULL,
    country VARCHAR(255)
);




