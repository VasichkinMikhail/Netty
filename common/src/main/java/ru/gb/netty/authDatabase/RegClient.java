package ru.gb.netty.authDatabase;

import ru.gb.netty.message.Message;

import java.io.IOException;

public class RegClient extends Message {
     private String name;
     private String log;
     private String pass;


     public RegClient() {

     }

     public String getLog() {
          return log;
     }

     public void setLog(String log) {
          this.log = log;
     }

     public String getPass() {
          return pass;
     }

     public void setPass(String pass) {
          this.pass = pass;
     }

     public String getName() {
          return name;
     }

     public void setName(String name) {
          this.name = name;
     }
}
