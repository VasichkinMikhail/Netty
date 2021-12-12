package ru.gb.netty.database;

import java.sql.SQLException;

public interface AuthService {

     void createUser(String login, String password, String nickname) throws SQLException;

     String AuthUser(String login, String password) throws SQLException;

}
