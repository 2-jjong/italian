package com.ssafy.italian_brainrot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class ItalianBrainrotServerApplication {

	@PostConstruct
	public void setTimeZone() {
		// 애플리케이션 전체에서 한국 시간(KST) 사용하도록 설정
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

	public static void main(String[] args) {
		// JVM 레벨에서 한국 시간대 설정
		System.setProperty("user.timezone", "Asia/Seoul");

		SpringApplication.run(ItalianBrainrotServerApplication.class, args);
	}
}