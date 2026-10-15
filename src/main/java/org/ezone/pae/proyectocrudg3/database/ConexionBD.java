package org.ezone.pae.proyectocrudg3.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    private static final String URL = "jdbc:postgresql://localhost:5432/bdventas";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Ezone2106";

    private ConexionBD() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}