package ru.gb.netty.server;

import ru.gb.netty.authDatabase.AuthClient;
import ru.gb.netty.authDatabase.RegClient;


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
        statement.executeUpdate("create table if not exists DataBase (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "login  VARCHAR(32)  NOT NULL," +
                "password VARCHAR(32)  NOT NULL" +
                ");"
        );
    }

    public String getClients(AuthClient client) throws SQLException {

        String login = null;
        if (connection != null) {
            try (PreparedStatement ps = connection.prepareStatement("SELECT login FROM DataBase WHERE password = ?;")) {
                ps.setString(1, client.getPass());
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    login = rs.getString(1);
                }rs.close();
            }
        }
        return login;
    }
    public String clientCheck(RegClient regClient) throws SQLException {

        String login = null;
        if (connection != null) {
            try (PreparedStatement ps = connection.prepareStatement("SELECT login FROM DataBase WHERE password = ?;")) {
                ps.setString(1, regClient.getPass());
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    login = rs.getString(1);
                }rs.close();
            }
        }
        return login;
    }


    public void getNewClients(RegClient client) throws SQLException {
        if (connection != null) {
            try (PreparedStatement ps = connection.prepareStatement("INSERT INTO DataBase ( login, password) VALUES( ?, ?);")) {
                ps.setString(1, client.getLog());
                ps.setString(2, client.getPass());

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










