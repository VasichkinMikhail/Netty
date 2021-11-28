package ru.gb.netty.database;

import java.util.Scanner;

public class AuthLoginPassword{

    public void authService() {
        Scanner in = new Scanner(System.in);
        System.out.println("Здравствуйте!\n. Регистрация напишите - 1 \n. Войти в свой аккаунт напишете - 2");
        String choice = in.nextLine();
        if (choice.equals("1")) {
            System.out.println("Введите имя");
            String nick = in.nextLine();
            System.out.println("Введите login");
            String log = in.nextLine();
            System.out.println("Введите pass");
            String pass = in.nextLine();

            DataBase.createUser(log,pass,nick);
            authService();
        }
        if (choice.equals("2")) {
            while (true) {
                System.out.println("Введите имя");
                String nick = in.nextLine();
                System.out.println("Введите login");
                String log = in.nextLine();
                System.out.println("Введите pass");
                String pass = in.nextLine();

                if (nick.equals(DataBase.AuthUser(log, pass))) {
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










