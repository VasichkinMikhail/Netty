package ru.gb.netty.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AuthLoginPassword extends AuthClient {

    private Scanner in = new Scanner(System.in);
    private DataBase dataBase = new DataBase();
    private AuthClient client = new AuthClient();

    public void registration() throws SQLException {
        System.out.println("Введите имя");
        String nickname = in.nextLine();
        client.setNickname(nickname);
        System.out.println("Введите login");
        String login = in.nextLine();
        client.setLogin(login);
        System.out.println("Введите pass");
        String password = in.nextLine();
        client.setPassword(password);
        dataBase.createUser(client);
        authentication();
        in.close();
        return;

    }

    public void authentication() throws SQLException {
        while (true) {
            System.out.println("Введите имя");
            String nickname = in.nextLine();
            client.setNickname(nickname);
            System.out.println("Введите login");
            String login = in.nextLine();
            client.setLogin(login);
            System.out.println("Введите pass");
            String password = in.nextLine();
            client.setPassword(password);
            String nick = DataBase.AuthUser(client);
            if (nick.equals(nickname)) {
                System.out.println("Верно!");
                in.close();
                return;
            } else
                System.out.println("Пароль и логин не верный!");

        }

    }

    public boolean authService() throws SQLException {
        Scanner in = new Scanner(System.in);
        System.out.println("Здравствуйте!\n. Регистрация напишите - 1 \n. Войти в свой аккаунт напишете - 2");
        String choice = in.nextLine();
        if (choice.equals("1")) {
            registration();
        }
        if (choice.equals("2")) {
            authentication();
            return true;
            } else{
                System.out.println("Не правильная команда!");
            }return false;

        }

    }











