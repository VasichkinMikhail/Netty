package ru.gb.netty.message;

import ru.gb.netty.authDatabase.DataBase;

public class EndFileTransferMessage extends Message{
    public EndFileTransferMessage() {
        System.out.println("End of file download");
        new DataBase().disconnectBase();
    }
    
}
