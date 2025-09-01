package com.cd_u.catatdulu_users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CatatduluUsersApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatatduluUsersApplication.class, args);
	}

}
