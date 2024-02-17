package com.example.demo;

import java.io.IOException;
import java.net.UnknownHostException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) throws UnknownHostException, IOException {
		SpringApplication.run(DemoApplication.class, args);

		CLI cli = new CLI();

		cli.startCLI();
	}

}
