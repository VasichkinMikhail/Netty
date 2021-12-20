package ru.gb.netty.authDatabase;

import ru.gb.netty.message.Message;

import java.io.*;

public class  AuthClient extends Message {

        private String name;
        private String log;
        private String pass;



        public AuthClient() {
        }


        public void setName(String name) throws IOException {
                this.name = name;

        }

        public String getName() throws IOException {

                return name;
        }

        public String getLog() throws IOException {

                return log;
        }

        public void setLog(String log) {
                this.log = log;
        }

        public String getPass() throws IOException {

                return pass;
        }

        public void setPass(String pass) {
                this.pass = pass;
        }
}

