package ru.gb.netty.database;

import java.sql.*;
import java.util.Scanner;

public class DataBase {
    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement registUserStatement;
    private static PreparedStatement authUserLoginStatement;


    public static void main(String[] args) {
        new DataBase().connect();
    }
    public  boolean connect() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:java.db");
            System.out.println("Connected to the database");
            statement = connection.createStatement();
            createUserTable();
            prepareAllStatement();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
    public static String AuthUser(String login, String password)  {
        String nickname = null;
        try {
            authUserLoginStatement.setString(1, login);
            authUserLoginStatement.setString(2, password);
            ResultSet rs = authUserLoginStatement.executeQuery();
            if (rs.next()) {
                nickname = rs.getString(1);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
       return nickname;
    }


    public static void prepareAllStatement() throws SQLException {
        registUserStatement = connection.prepareStatement("INSERT INTO user (login, password, nickname) VALUES (?, ?, ?);");
        authUserLoginStatement = connection.prepareStatement("SELECT nickname FROM user WHERE  login = ? AND password = ?;");


    }

    public static boolean createUser(String login, String password, String nickname) {
        try {

            registUserStatement.setString(1, login);
            registUserStatement.setString(2, password);
            registUserStatement.setString(3, nickname);
            registUserStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }


    }







