package com.subforest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // com.subforest.. 모두 스캔
public class SubscriptionManagerApplication {
	public static void main(String[] args) {
		SpringApplication.run(SubscriptionManagerApplication.class, args);
	}
}
