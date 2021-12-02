package ru.gb.netty.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AuthLoginPassword {
    public void registration() throws SQLException {
        Scanner in = new Scanner(System.in);
        System.out.println("Введите имя");
        String nickname = in.nextLine();
        System.out.println("Введите login");
        String login = in.nextLine();
        System.out.println("Введите pass");
        String password = in.nextLine();
        final DataBase dataBase = new DataBase();
        dataBase.createUser(login,password,nickname);

    }

    public  void authService() throws SQLException {
        Scanner in = new Scanner(System.in);
        System.out.println("Здравствуйте!\n. Регистрация напишите - 1 \n. Войти в свой аккаунт напишете - 2");
        String choice = in.nextLine();
        if (choice.equals("1")) {
            System.out.println("Введите имя");
            String nickname = in.nextLine();
            System.out.println("Введите login");
            String login = in.nextLine();
            System.out.println("Введите pass");
            String password = in.nextLine();
            final DataBase dataBase = new DataBase();
            dataBase.createUser(login,password,nickname);

        }
        if (choice.equals("2")) {
            while (true) {
                System.out.println("Введите имя");
                String nick = in.nextLine();
                System.out.println("Введите login");
                String log = in.nextLine();
                System.out.println("Введите pass");
                String pass = in.nextLine();
                String nickname = DataBase.AuthUser(log,pass);

                if (nick.equals(nickname)) {
                    System.out.println("Верно!");
                    in.close();


                } else
                    System.out.println("Пароль и логин не верный!");
                return;
            }
        } else {
            System.out.println("Не правильная команда!");
        }

    }

}










