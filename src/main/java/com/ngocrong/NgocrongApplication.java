package com.ngocrong;

import com.ngocrong.server.DragonBall;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NgocrongApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(NgocrongApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		DragonBall.getInstance().start();
	}
}
