package ru.gb.netty.authDatabase;

import ru.gb.netty.message.Message;

public class Verification extends Message {

    public Verification(){
        System.out.println("Авторизация прошла успешно!");
    }
}
