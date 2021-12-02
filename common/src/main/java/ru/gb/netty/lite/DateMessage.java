package ru.gb.netty.lite;


import java.util.Date;

public class DateMessage extends Message {
    private Date data;

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }
}
