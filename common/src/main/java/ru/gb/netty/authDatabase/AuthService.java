package ru.gb.netty.authDatabase;



import ru.gb.netty.message.Message;

import java.io.IOException;

import java.sql.SQLException;
import java.util.Scanner;

public class AuthService  extends Message {

    public AuthClient authorisation() throws SQLException, IOException {
        System.out.println("Авторизируйтесь ... ");

        final Scanner scanner = new Scanner(System.in);
        final AuthClient client = new AuthClient();

        System.out.println("Введите Ваш имя ... ");
        String name = scanner.nextLine();
        client.setName(name);

        System.out.println("Введите Ваш логин ... ");
        String log = scanner.nextLine();
        client.setLog(log);

        System.out.println("Введите Ваш пароль ... ");
        String pass = scanner.nextLine();
        client.setPass(pass);

        return client;

}
    public RegClient registration() throws SQLException, IOException {

        System.out.println("Здравствуйте!\n " +
                "Зарегистрируйтесь ... ");

        final Scanner scanner = new Scanner(System.in);
        final RegClient client = new RegClient();

        System.out.println("Введите Ваш имя ... ");
        String name = scanner.nextLine();
        client.setName(name);

        System.out.println("Введите Ваш логин ... ");
        String log = scanner.nextLine();
        client.setLog(log);

        System.out.println("Введите Ваш пароль ... ");
        String pass = scanner.nextLine();
        client.setPass(pass);

        return client;

    }
}
