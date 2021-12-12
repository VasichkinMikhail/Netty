package ru.gb.netty.database;

import javax.management.Query;
import java.sql.*;

public class DataBase  {
    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement registUserStatement;
    private static PreparedStatement authUserLoginStatement;


    public void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:java.db");
            System.out.println("База данных подключенна");
            statement = connection.createStatement();
            createUserTable();
            prepareAllStatement();

        } catch (SQLException e) {
            e.printStackTrace();

        }
    }


    public static void disconnect() {
        try {
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createUserTable() throws SQLException {
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS user (" +
                "    id       INTEGER      PRIMARY KEY AUTOINCREMENT" +
                "                          NOT NULL" +
                "                          UNIQUE," +
                "    login    VARCHAR (32) UNIQUE" +
                "                          NOT NULL," +
                "    password VARCHAR (32) NOT NULL," +
                "    nickname VARCHAR (32) UNIQUE" +
                "                          NOT NULL" +
                ");"
        );
    }

    public static String AuthUser(AuthClient client) throws SQLException {
        String nickname = null;
        if (connection != null) {
            try {
                authUserLoginStatement.setString(1, client.getLogin());
                authUserLoginStatement.setString(2, client.getPassword());

                ResultSet rs = authUserLoginStatement.executeQuery();
                if (rs.next()){
                    nickname = rs.getString(1);
                }rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }return nickname;

    }




    public static void prepareAllStatement() throws SQLException {
        registUserStatement = connection.prepareStatement("INSERT INTO user (login, password, nickname) VALUES (?, ?, ?);");
        authUserLoginStatement = connection.prepareStatement("SELECT nickname FROM user WHERE  login = ? AND password = ?;");


    }

    public void createUser(AuthClient client) throws SQLException {
        if (connection != null) {
            try {
                registUserStatement.setString(1, client.getLogin());
                registUserStatement.setString(2, client.getPassword());
                registUserStatement.setString(3, client.getNickname());
                System.out.println("Пользователь " + client.getNickname() + " добавлен");
                registUserStatement.execute();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}










