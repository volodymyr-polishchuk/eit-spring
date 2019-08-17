package com.volodymyrpo.eit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebApplication {

    public static void main(String[] args) {
        String port = System.getenv().get("PORT");
        if (port != null) {
            System.setProperty("server.port", port);
        }
        SpringApplication.run(WebApplication.class, args);
    }
}
