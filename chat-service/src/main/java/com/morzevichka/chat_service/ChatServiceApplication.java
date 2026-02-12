package com.morzevichka.chat_service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class ChatServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatServiceApplication.class, args);
	}

	@GetMapping("/api/test")
	public ResponseEntity<String> test(HttpServletRequest request) {
		return ResponseEntity.ok("String" + request.getHeader("Authorization"));
	}
}
