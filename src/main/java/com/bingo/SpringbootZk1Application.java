package com.bingo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class SpringbootZk1Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootZk1Application.class, args);
	}

}
