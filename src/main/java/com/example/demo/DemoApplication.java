package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);

		Thread t = new Thread(() -> {
			while(true) {
				HttpURLConnection connection = null;
				try {
					connection = (HttpURLConnection) new URL("https://google.com/").openConnection();
					connection.setRequestMethod("HEAD");
					int responseCode = connection.getResponseCode();
					if (responseCode != 200) {
						// Not OK.
					}
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

		t.start();
	}

}
