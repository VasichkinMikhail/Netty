package ru.gb.netty.database;

import java.sql.SQLException;

public interface AuthService {
     String getNicknameByLoginAndPassword() throws SQLException;

}
