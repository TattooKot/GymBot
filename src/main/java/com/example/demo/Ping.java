package com.example.demo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Ping {

    static Thread t = new Thread(() -> {
        while(true) {
            HttpURLConnection connection;
            try {
                connection = (HttpURLConnection) new URL("https://google.com/").openConnection();
                connection.connect();
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    });


}
