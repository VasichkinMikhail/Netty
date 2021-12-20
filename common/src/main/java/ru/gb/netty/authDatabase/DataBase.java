package ru.gb.netty.authDatabase;

import java.io.IOException;
import java.sql.*;

public class DataBase  {
    private static Connection connection;
    private static Statement statement;


    public boolean connect() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:java.db");
            System.out.println("База данных подключенна");
            statement = connection.createStatement();
            createUserTable();

        } catch (SQLException e) {
            e.printStackTrace();

        }
        return false;
    }

    private static void createUserTable() throws SQLException {
        statement.executeUpdate("create table if not exists userBase (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "clientName VARCHAR(32) UNIQUE NOT NULL," +
                "login  VARCHAR(32) UNIQUE NOT NULL," +
                "password VARCHAR(32) UNIQUE NOT NULL" +
                ");"
        );
    }

    public String getClients(AuthClient client) throws SQLException, IOException {
        String clientName = null;
        if (connection != null) {
            try (PreparedStatement ps = connection.prepareStatement("SELECT clientName FROM userBase WHERE login = ? AND password = ?;")) {
                ps.setString(1, client.getLog());
                ps.setString(2, client.getPass());
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    clientName = rs.getString(1);
                }rs.close();
            }
        }
        return clientName;
    }

    public void getNewClients(RegClient client) throws SQLException, IOException {
        if (connection != null) {
            try (PreparedStatement ps = connection.prepareStatement("INSERT INTO userBase (clientName, login, password) VALUES(?, ?, ?);")) {
                ps.setString(1, client.getName());
                ps.setString(2, client.getLog());
                ps.setString(3, client.getPass());

                ps.executeUpdate();
            }
        }
    }

    public void disconnectBase() {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}










